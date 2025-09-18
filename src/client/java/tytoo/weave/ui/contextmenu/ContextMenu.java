package tytoo.weave.ui.contextmenu;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.popup.Anchor;
import tytoo.weave.ui.popup.PopupOptions;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;
import tytoo.weave.utils.McUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ContextMenu {
    private final List<Entry> entries = new ArrayList<>();
    @Nullable
    private UIManager.PopupHandle handle;

    private ContextMenu() {
    }

    public static ContextMenu create() {
        return new ContextMenu();
    }

    private static Panel buildView(ContextMenu menu) {
        Panel container = Panel.create();
        container.addStyleClass("context-menu");
        container.setPadding(2f, 2f);
        container.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START));
        container.setWidth(Constraints.childBased(2f));
        container.setHeight(Constraints.childBased(2f));

        for (Entry e : menu.entries) {
            if (e instanceof Item(String label, Runnable action)) {
                Button b = Button.create();
                b.addStyleClass("context-menu-item");
                b.setWidth(Constraints.childBased(6f));
                b.setHeight(Constraints.childBased(4f));
                b.addChild(SimpleTextComponent.of(label).setX(Constraints.pixels(0f)).setY(Constraints.center()).setHittable(false));
                b.onClick(ignored -> {
                    menu.close();
                    action.run();
                });
                container.addChild(b);
            } else if (e instanceof Separator) {
                Panel sep = Panel.create();
                sep.addStyleClass("context-menu-separator");
                sep.setWidth(Constraints.relative(1.0f));
                sep.setHeight(Constraints.pixels(1f));
                container.addChild(sep);
            }
        }

        return container;
    }

    public static void attachTo(@NotNull Component<?> owner, @NotNull Supplier<ContextMenu> builder) {
        owner.onMouseClick(e -> {
            if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                ContextMenu menu = builder.get();
                if (menu != null) {
                    menu.openAtMouse(owner);
                    e.cancel();
                }
            }
        });

        ShortcutRegistry.registerForComponent(owner, ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_MENU).allowingAnyAdditionalModifiers(),
                ctx -> {
                    ContextMenu menu = builder.get();
                    if (menu != null) {
                        menu.openBelow(owner);
                        return true;
                    }
                    return false;
                }));

        ShortcutRegistry.registerForComponent(owner, ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_F10)
                        .withModifiers(ShortcutRegistry.KeyChord.Modifier.SHIFT)
                        .allowingAnyAdditionalModifiers(),
                ctx -> {
                    ContextMenu menu = builder.get();
                    if (menu != null) {
                        menu.openBelow(owner);
                        return true;
                    }
                    return false;
                }));
    }

    public ContextMenu item(@NotNull String label, @NotNull Runnable action) {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(action, "action");
        entries.add(new Item(label, action));
        return this;
    }

    public ContextMenu separator() {
        entries.add(Separator.INSTANCE);
        return this;
    }

    public void close() {
        if (handle == null) return;
        UIManager.closePopup(handle);
        handle = null;
    }

    public ContextMenu openBelow(@NotNull Component<?> owner) {
        Panel view = buildView(this);
        Anchor anchor = new Anchor(owner, Anchor.Side.BOTTOM, Anchor.Align.START, 0f, 0f, 0f);
        PopupOptions opts = new PopupOptions()
                .setTrapFocus(true)
                .setCloseOnFocusLoss(true)
                .setCloseOnEsc(true)
                .setGap(2f);
        this.handle = UIManager.openPopup(view, anchor, opts);
        return this;
    }

    public ContextMenu openAtMouse(@NotNull Component<?> owner) {
        float mouseX = McUtils.getMc().map(mc -> (float) (mc.mouse.getX() / mc.getWindow().getScaleFactor())).orElse(0f);
        float mouseY = McUtils.getMc().map(mc -> (float) (mc.mouse.getY() / mc.getWindow().getScaleFactor())).orElse(0f);

        float offsetX = mouseX - owner.getLeft();
        float offsetY = mouseY - owner.getTop();

        Panel view = buildView(this);

        Anchor anchor = new Anchor(owner, Anchor.Side.TOP, Anchor.Align.START, offsetX, offsetY, 0f);
        PopupOptions opts = new PopupOptions()
                .setTrapFocus(true)
                .setCloseOnFocusLoss(true)
                .setCloseOnEsc(true)
                .setGap(0f);
        this.handle = UIManager.openPopup(view, anchor, opts);
        return this;
    }

    private sealed interface Entry permits Item, Separator {
    }

    private record Item(String label, Runnable action) implements Entry {
    }

    private static final class Separator implements Entry {
        private static final Separator INSTANCE = new Separator();

        private Separator() {
        }
    }
}
