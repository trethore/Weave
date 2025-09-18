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
    private static final Comparator<RegisteredShortcut> SHORTCUT_ORDER = Comparator
            .comparingInt((RegisteredShortcut r) -> r.shortcut().priority()).reversed()
            .thenComparingLong(RegisteredShortcut::order).reversed();
    private static long orderCounter = 0;

    private ShortcutRegistry() {
    }

    public static Registration registerGlobal(Shortcut shortcut) {
        return register(ShortcutScope.GLOBAL, null, null, shortcut);
    }

    public static Registration registerForScreen(Screen screen, Shortcut shortcut) {
        return register(ShortcutScope.SCREEN, screen, null, shortcut);
    }

    public static Registration registerForComponent(Component<?> root, Shortcut shortcut) {
        return register(ShortcutScope.COMPONENT_TREE, null, root, shortcut);
    }

    public static boolean dispatch(Screen screen, UIState state, int keyCode, int modifiers) {
        purgeStale();
        Component<?> focused = state.getFocusedComponent();

        ShortcutContext ctx = new ShortcutContext(screen, state, focused, keyCode, modifiers);

        List<RegisteredShortcut> candidates = collectCandidates(ctx);
        if (candidates.isEmpty()) return false;

        candidates.sort(SHORTCUT_ORDER);

        for (RegisteredShortcut candidate : candidates) {
            if (candidate.shortcut().handler().handle(ctx)) {
                return true;
            }
        }
        return false;
    }

    private static void purgeStale() {
        REGISTRY.removeIf(RegisteredShortcut::isStale);
    }

    private static List<RegisteredShortcut> collectCandidates(ShortcutContext ctx) {
        List<RegisteredShortcut> candidates = new ArrayList<>();
        for (RegisteredShortcut shortcut : REGISTRY) {
            if (!shortcut.isApplicableTo(ctx)) continue;
            if (!shortcut.shortcut().enabledPredicate().test(ctx)) continue;
            if (!shortcut.shortcut().chord().matches(ctx)) continue;
            candidates.add(shortcut);
        }
        return candidates;
    }

    private static Registration register(ShortcutScope scope,
                                         @Nullable Screen screen,
                                         @Nullable Component<?> component,
                                         Shortcut shortcut) {
        if (scope == ShortcutScope.SCREEN && screen == null) {
            throw new IllegalArgumentException("Screen scope requires screen reference");
        }
        if (scope == ShortcutScope.COMPONENT_TREE && component == null) {
            throw new IllegalArgumentException("Component scope requires component reference");
        }
        WeakReference<Screen> screenRef = scope == ShortcutScope.SCREEN
                ? new WeakReference<>(screen)
                : null;
        WeakReference<Component<?>> componentRef = scope == ShortcutScope.COMPONENT_TREE
                ? new WeakReference<>(component)
                : null;
        RegisteredShortcut reg = new RegisteredShortcut(scope, screenRef, componentRef, shortcut, nextOrder());
        REGISTRY.add(reg);
        return reg;
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

    public static final class KeyChord {
        private final List<Integer> keys;
        private final EnumSet<Modifier> requiredModifiers;
        private final EnumSet<Modifier> optionalModifiers;
        private final boolean requireEventKeyMatch;

        private KeyChord(List<Integer> keys,
                         EnumSet<Modifier> requiredModifiers,
                         EnumSet<Modifier> optionalModifiers,
                         boolean requireEventKeyMatch) {
            this.keys = List.copyOf(keys);
            this.requiredModifiers = copyModifiers(requiredModifiers);
            this.optionalModifiers = copyModifiers(optionalModifiers);
            this.requireEventKeyMatch = requireEventKeyMatch;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static KeyChord of(int key) {
            return builder().requireKey(key).build();
        }

        public static KeyChord of(int key, int... additionalKeys) {
            Builder builder = builder().requireKey(key);
            builder.requireKeys(additionalKeys);
            return builder.build();
        }

        public static KeyChord ctrl(int key) {
            return builder().requireModifier(Modifier.CONTROL).requireKey(key).build();
        }

        public static KeyChord ctrlShift(int key) {
            return builder().requireModifiers(Modifier.CONTROL, Modifier.SHIFT).requireKey(key).build();
        }

        public static KeyChord ctrlAlt(int key) {
            return builder().requireModifiers(Modifier.CONTROL, Modifier.ALT).requireKey(key).build();
        }

        public static KeyChord meta(int key) {
            return builder().requireModifier(Modifier.SUPER).requireKey(key).build();
        }

        private static EnumSet<Modifier> currentModifiersDown() {
            EnumSet<Modifier> down = EnumSet.noneOf(Modifier.class);
            for (Modifier modifier : Modifier.values()) {
                if (modifier.isDown()) {
                    down.add(modifier);
                }
            }
            return down;
        }

        private static EnumSet<Modifier> copyModifiers(Set<Modifier> source) {
            EnumSet<Modifier> copy = EnumSet.noneOf(Modifier.class);
            copy.addAll(source);
            return copy;
        }

        private static boolean isKeyActive(int key, ShortcutContext ctx, int normalizedEventKey) {
            if (InputHelper.isKeyPressed(key)) {
                return true;
            }
            return normalizedEventKey == key;
        }

        public KeyChord withKey(int key) {
            Builder builder = toBuilder();
            builder.requireKey(key);
            return builder.build();
        }

        public KeyChord withModifiers(Modifier... modifiers) {
            Builder builder = toBuilder();
            builder.requireModifiers(modifiers);
            return builder.build();
        }

        public KeyChord allowingModifiers(Modifier... modifiers) {
            Builder builder = toBuilder();
            builder.allowModifiers(modifiers);
            return builder.build();
        }

        public KeyChord allowingAnyAdditionalModifiers() {
            Builder builder = toBuilder();
            builder.allowAnyAdditionalModifiers();
            return builder.build();
        }

        public KeyChord triggerOnAnyKey() {
            Builder builder = toBuilder();
            builder.triggerOnAnyKey();
            return builder.build();
        }

        public boolean matches(ShortcutContext ctx) {
            int normalizedEventKey = InputHelper.toQwerty(ctx.keyCode());
            if (!matchesEventKey(normalizedEventKey)) {
                return false;
            }
            if (!areRequiredKeysActive(ctx, normalizedEventKey)) {
                return false;
            }
            EnumSet<Modifier> pressedModifiers = currentModifiersDown();
            if (!pressedModifiers.containsAll(requiredModifiers)) {
                return false;
            }
            EnumSet<Modifier> allowedModifiers = copyModifiers(requiredModifiers);
            allowedModifiers.addAll(optionalModifiers);
            for (Modifier modifier : pressedModifiers) {
                if (!allowedModifiers.contains(modifier)) {
                    return false;
                }
            }
            return true;
        }

        private boolean matchesEventKey(int normalizedEventKey) {
            if (!requireEventKeyMatch) {
                return true;
            }
            if (keys.isEmpty()) {
                return true;
            }
            return keys.contains(normalizedEventKey);
        }

        private boolean areRequiredKeysActive(ShortcutContext ctx, int normalizedEventKey) {
            for (int key : keys) {
                if (!isKeyActive(key, ctx, normalizedEventKey)) {
                    return false;
                }
            }
            return true;
        }

        public List<Integer> keys() {
            return keys;
        }

        public Set<Modifier> requiredModifiers() {
            return Set.copyOf(requiredModifiers);
        }

        public Set<Modifier> optionalModifiers() {
            return Set.copyOf(optionalModifiers);
        }

        private Builder toBuilder() {
            Builder builder = new Builder();
            builder.keys.addAll(this.keys);
            builder.required.addAll(this.requiredModifiers);
            builder.optional.addAll(this.optionalModifiers);
            builder.requireEventKeyMatch = this.requireEventKeyMatch;
            return builder;
        }

        public enum Modifier {
            CONTROL,
            SHIFT,
            ALT,
            SUPER;

            boolean isDown() {
                return switch (this) {
                    case CONTROL -> InputHelper.isControlDown();
                    case SHIFT -> InputHelper.isShiftDown();
                    case ALT -> InputHelper.isAltDown();
                    case SUPER -> InputHelper.isMetaDown();
                };
            }
        }

        public static final class Builder {
            private final LinkedHashSet<Integer> keys = new LinkedHashSet<>();
            private final EnumSet<Modifier> required = EnumSet.noneOf(Modifier.class);
            private final EnumSet<Modifier> optional = EnumSet.noneOf(Modifier.class);
            private boolean requireEventKeyMatch = true;

            public Builder requireKey(int key) {
                keys.add(key);
                return this;
            }

            public Builder requireKeys(int... keyCodes) {
                for (int keyCode : keyCodes) {
                    keys.add(keyCode);
                }
                return this;
            }

            public Builder requireModifier(Modifier modifier) {
                required.add(modifier);
                return this;
            }

            public Builder requireModifiers(Modifier... modifiers) {
                Collections.addAll(required, modifiers);
                return this;
            }

            public Builder allowModifiers(Modifier... modifiers) {
                for (Modifier modifier : modifiers) {
                    if (!required.contains(modifier)) {
                        optional.add(modifier);
                    }
                }
                return this;
            }

            public Builder allowAnyAdditionalModifiers() {
                for (Modifier modifier : Modifier.values()) {
                    if (!required.contains(modifier)) {
                        optional.add(modifier);
                    }
                }
                return this;
            }

            public Builder triggerOnAnyKey() {
                this.requireEventKeyMatch = false;
                return this;
            }

            public KeyChord build() {
                return new KeyChord(new ArrayList<>(keys), required, optional, requireEventKeyMatch);
            }
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

        private boolean isApplicableTo(ShortcutContext ctx) {
            return switch (scope) {
                case GLOBAL -> true;
                case SCREEN -> screenRef != null && screenRef.get() == ctx.screen();
                case COMPONENT_TREE ->
                        componentRef != null && isFocusedInComponentTree(ctx.focused(), componentRef.get());
            };
        }

        private boolean isStale() {
            return switch (scope) {
                case GLOBAL -> false;
                case SCREEN -> screenRef == null || screenRef.get() == null;
                case COMPONENT_TREE -> componentRef == null || componentRef.get() == null;
            };
        }

        @Override
        public void unregister() {
            REGISTRY.remove(this);
        }
    }
}
