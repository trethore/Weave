package tytoo.weave.ui.shortcuts;

import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.ui.UIState;
import tytoo.weave.utils.InputHelper;

import java.lang.ref.WeakReference;
import java.util.*;

public final class ShortcutRegistry {
    private static final List<RegisteredShortcut> REGISTRY = new ArrayList<>();
    private static long orderCounter = 0;

    private ShortcutRegistry() {
    }

    public static Registration registerGlobal(Shortcut shortcut) {
        RegisteredShortcut reg = new RegisteredShortcut(ShortcutScope.GLOBAL, null, null, shortcut, nextOrder());
        REGISTRY.add(reg);
        return reg;
    }

    public static Registration registerForScreen(Screen screen, Shortcut shortcut) {
        RegisteredShortcut reg = new RegisteredShortcut(ShortcutScope.SCREEN, new WeakReference<>(screen), null, shortcut, nextOrder());
        REGISTRY.add(reg);
        return reg;
    }

    public static Registration registerForComponent(Component<?> root, Shortcut shortcut) {
        RegisteredShortcut reg = new RegisteredShortcut(ShortcutScope.COMPONENT_TREE, null, new WeakReference<>(root), shortcut, nextOrder());
        REGISTRY.add(reg);
        return reg;
    }

    public static boolean dispatch(Screen screen, UIState state, int keyCode, int modifiers) {
        purgeStale();
        Component<?> focused = state.getFocusedComponent();

        ShortcutContext ctx = new ShortcutContext(screen, state, focused, keyCode, modifiers);

        List<RegisteredShortcut> candidates = new ArrayList<>();
        for (RegisteredShortcut rs : REGISTRY) {
            if (!rs.appliesTo(ctx)) continue;
            if (!rs.shortcut().enabledPredicate().test(ctx)) continue;
            if (!rs.shortcut().chord().matches()) continue;
            candidates.add(rs);
        }

        if (candidates.isEmpty()) return false;

        candidates.sort(Comparator
                .comparingInt((RegisteredShortcut r) -> r.shortcut().priority()).reversed()
                .thenComparingLong(RegisteredShortcut::order).reversed());

        for (RegisteredShortcut rs : candidates) {
            if (rs.shortcut().handler().handle(ctx)) {
                return true;
            }
        }
        return false;
    }

    private static void purgeStale() {
        Iterator<RegisteredShortcut> it = REGISTRY.iterator();
        while (it.hasNext()) {
            RegisteredShortcut rs = it.next();
            if (rs.scope() == ShortcutScope.GLOBAL) continue;
            if (rs.scope() == ShortcutScope.SCREEN && (rs.screenRef() == null || rs.screenRef().get() == null)) {
                it.remove();
                continue;
            }
            if (rs.scope() == ShortcutScope.COMPONENT_TREE && (rs.componentRef() == null || rs.componentRef().get() == null)) {
                it.remove();
            }
        }
    }

    private static long nextOrder() {
        return ++orderCounter;
    }

    public enum ShortcutScope {GLOBAL, SCREEN, COMPONENT_TREE}

    public interface Registration {
        void unregister();
    }

    public interface Predicate {
        Predicate ALWAYS = ctx -> true;

        boolean test(ShortcutContext ctx);
    }

    public interface Handler {
        boolean handle(ShortcutContext ctx);
    }

    public record Shortcut(KeyChord chord, int priority, Predicate enabledPredicate, Handler handler) {
        public Shortcut {
            enabledPredicate = Objects.requireNonNullElse(enabledPredicate, Predicate.ALWAYS);
        }

        public static Shortcut of(KeyChord chord, Handler handler) {
            return new Shortcut(chord, 0, Predicate.ALWAYS, handler);
        }

        public Shortcut withPriority(int prio) {
            return new Shortcut(this.chord, prio, this.enabledPredicate, this.handler);
        }

        public Shortcut when(Predicate predicate) {
            return new Shortcut(this.chord, this.priority, predicate, this.handler);
        }
    }

    public record KeyChord(int key, boolean requireCtrl, boolean requireShift, boolean requireAlt,
                           boolean requireMeta) {
        public static KeyChord of(int key) {
            return new KeyChord(key, false, false, false, false);
        }

        public static KeyChord ctrl(int key) {
            return new KeyChord(key, true, false, false, false);
        }

        public static KeyChord ctrlShift(int key) {
            return new KeyChord(key, true, true, false, false);
        }

        public static KeyChord ctrlAlt(int key) {
            return new KeyChord(key, true, false, true, false);
        }

        public static KeyChord meta(int key) {
            return new KeyChord(key, false, false, false, true);
        }

        public boolean matches() {
            if (!InputHelper.isKeyPressed(key)) return false;
            if (requireCtrl && !InputHelper.isControlDown()) return false;
            if (!requireCtrl && InputHelper.isControlDown()) return false;
            if (requireShift && !InputHelper.isShiftDown()) return false;
            if (!requireShift && InputHelper.isShiftDown()) return false;
            if (requireAlt && !InputHelper.isAltDown()) return false;
            return requireAlt || !InputHelper.isAltDown();
        }
    }

    public record ShortcutContext(Screen screen, UIState state, @Nullable Component<?> focused, int keyCode,
                                  int modifiers) {
    }

    private record RegisteredShortcut(ShortcutScope scope,
                                      @Nullable WeakReference<Screen> screenRef,
                                      @Nullable WeakReference<Component<?>> componentRef,
                                      Shortcut shortcut,
                                      long order) implements Registration {
        private static boolean isFocusedInComponentTree(@Nullable Component<?> focused, @Nullable Component<?> root) {
            if (focused == null || root == null) return false;
            for (Component<?> c = focused; c != null; c = c.getParent()) {
                if (c == root) return true;
            }
            return false;
        }

        private boolean appliesTo(ShortcutContext ctx) {
            return switch (scope) {
                case GLOBAL -> true;
                case SCREEN -> screenRef != null && screenRef.get() == ctx.screen();
                case COMPONENT_TREE ->
                        componentRef != null && isFocusedInComponentTree(ctx.focused(), componentRef.get());
            };
        }

        @Override
        public void unregister() {
            REGISTRY.remove(this);
        }
    }
}
