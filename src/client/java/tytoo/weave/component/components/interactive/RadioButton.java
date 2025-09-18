package tytoo.weave.component.components.interactive;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.animation.Easings;
import tytoo.weave.component.Component;
import tytoo.weave.component.NamedPart;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;
import tytoo.weave.utils.McUtils;

import java.util.ArrayList;
import java.util.List;

public class RadioButton<V> extends InteractiveComponent<RadioButton<V>> {

    private final V value;
    @NamedPart
    private final Panel outline;
    @NamedPart
    private final Panel background;
    @NamedPart
    private final Panel dot;
    private final TextComponent<?> label;
    @Nullable
    private RadioButtonGroup<V> group;

    protected RadioButton(V value, String labelText) {
        this.value = value;

        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float gap = stylesheet.get(this, StyleProps.GAP, 5f);
        float outlineSize = stylesheet.get(this, StyleProps.OUTLINE_SIZE, 12f);
        float dotSize = stylesheet.get(this, StyleProps.DOT_SIZE, 6f);

        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, gap));
        this.setHeight(Constraints.childBased());
        this.setWidth(Constraints.sumOfChildrenWidth(0, gap));
        this.setHittable(true);

        this.outline = Panel.create()
                .setWidth(Constraints.pixels(outlineSize))
                .setHeight(Constraints.pixels(outlineSize))
                .setHittable(false)
                .addStyleState(StyleState.NORMAL);

        float backgroundSize = outlineSize - 2f;
        this.background = Panel.create()
                .setWidth(Constraints.pixels(backgroundSize))
                .setHeight(Constraints.pixels(backgroundSize))
                .setX(Constraints.center())
                .setY(Constraints.center())
                .setHittable(false)
                .addStyleState(StyleState.NORMAL);


        this.dot = Panel.create()
                .setWidth(Constraints.pixels(dotSize))
                .setHeight(Constraints.pixels(dotSize))
                .setX(Constraints.center())
                .setY(Constraints.center())
                .setOpacity(0f)
                .setScale(0f)
                .setHittable(false)
                .addStyleState(StyleState.NORMAL);

        this.outline.addChild(this.background);
        this.background.addChild(this.dot);

        this.label = SimpleTextComponent.of(labelText)
                .setHittable(false)
                .setMargin(1, 0);

        this.addChildren(this.outline, this.label);

        this.onClick(e -> {
            if (this.isEnabled() && this.group != null) {
                this.group.onButtonSelected(this);
            }
        });

        this.onMouseEnter(e -> this.background.addStyleState(StyleState.HOVERED));
        this.onMouseLeave(e -> this.background.removeStyleState(StyleState.HOVERED));
        this.onFocusGained(e -> {
            this.addStyleState(StyleState.HOVERED);
            this.background.addStyleState(StyleState.FOCUSED);
            this.background.addStyleState(StyleState.HOVERED);
        });
        this.onFocusLost(e -> {
            this.removeStyleState(StyleState.HOVERED);
            this.background.removeStyleState(StyleState.FOCUSED);
            this.background.removeStyleState(StyleState.HOVERED);
        });

        registerComponentShortcut(button -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_LEFT),
                ctx -> button.focusAdjacent(-1)));

        registerComponentShortcut(button -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_UP),
                ctx -> button.focusAdjacent(-1)));

        registerComponentShortcut(button -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_RIGHT),
                ctx -> button.focusAdjacent(1)));

        registerComponentShortcut(button -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_DOWN),
                ctx -> button.focusAdjacent(1)));
    }

    public static <V> RadioButton<V> of(V value, String labelText) {
        return new RadioButton<>(value, labelText);
    }

    public V getValue() {
        return this.value;
    }

    void setGroup(@Nullable RadioButtonGroup<V> group) {
        this.group = group;
    }

    private boolean focusAdjacent(int direction) {
        if (this.group == null) return false;
        List<Component<?>> children = this.group.getChildren();
        List<RadioButton<V>> radios = new ArrayList<>();
        for (Component<?> child : children) {
            if (child instanceof RadioButton<?>) {
                @SuppressWarnings("unchecked")
                RadioButton<V> rb = (RadioButton<V>) child;
                radios.add(rb);
            }
        }
        if (radios.isEmpty()) return false;
        int index = radios.indexOf(this);
        if (index == -1) return false;
        int next = (index + direction + radios.size()) % radios.size();
        RadioButton<V> target = radios.get(next);
        if (target == this) return true;
        return McUtils.getMc()
                .map(mc -> mc.currentScreen)
                .map(screen -> UIManager.requestFocus(screen, target))
                .orElse(false);
    }

    void updateSelected(boolean selected) {
        if (this.hasStyleState(StyleState.SELECTED) == selected) return;

        this.setStyleState(StyleState.SELECTED, selected);
        this.background.setStyleState(StyleState.SELECTED, selected);

        float targetOpacity = selected ? 1.0f : 0.0f;
        float targetScale = selected ? 1.0f : 0.0f;

        long duration = ThemeManager.getStylesheet().get(this, InteractiveComponent.StyleProps.ANIMATION_DURATION, 150L);
        this.dot.animate().duration(duration).easing(Easings.EASE_OUT_QUAD).opacity(targetOpacity);
        this.dot.animate().duration(duration).easing(Easings.EASE_OUT_BACK).scale(targetScale);
    }

    @Override
    public RadioButton<V> setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.outline.setStyleState(StyleState.DISABLED, !enabled);
        this.background.setStyleState(StyleState.DISABLED, !enabled);
        this.label.setStyleState(StyleState.DISABLED, !enabled);
        return self();
    }

    public static final class StyleProps {
        private static final Class<? extends Component<?>> COMPONENT_CLASS = StyleSlot.componentType(RadioButton.class);

        public static final StyleSlot GAP = StyleSlot.of("radio.gap", COMPONENT_CLASS, Float.class);
        public static final StyleSlot OUTLINE_SIZE = StyleSlot.of("radio.outline.size", COMPONENT_CLASS, Float.class);
        public static final StyleSlot DOT_SIZE = StyleSlot.of("radio.dot.size", COMPONENT_CLASS, Float.class);

        private StyleProps() {
        }
    }
}
