package tytoo.weave.component.components.interactive;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.animation.Easing;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;

public class RadioButton<V> extends InteractiveComponent<RadioButton<V>> {

    private final V value;
    private final Panel outline;
    private final Panel background;
    private final Panel dot;
    private final TextComponent<?> label;
    @Nullable
    private RadioButtonGroup<V> group;

    protected RadioButton(V value, String labelText) {
        this.value = value;

        float gap = 5f;
        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, gap));
        this.setHeight(Constraints.childBased());
        this.setWidth(Constraints.sumOfChildrenWidth(0, gap));
        this.setHittable(true);

        this.outline = Panel.create()
                .setWidth(Constraints.pixels(12))
                .setHeight(Constraints.pixels(12))
                .addStyleClass("radio-button-outline")
                .setHittable(false)
                .addStyleState(StyleState.NORMAL);

        this.background = Panel.create()
                .setWidth(Constraints.pixels(10))
                .setHeight(Constraints.pixels(10))
                .setX(Constraints.center())
                .setY(Constraints.center())
                .addStyleClass("radio-button-background")
                .setHittable(false)
                .addStyleState(StyleState.NORMAL);

        this.dot = Panel.create()
                .setWidth(Constraints.pixels(6))
                .setHeight(Constraints.pixels(6))
                .setX(Constraints.center())
                .setY(Constraints.center())
                .addStyleClass("radio-button-dot")
                .setOpacity(0f)
                .setScale(0f)
                .setHittable(false);

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
        this.onFocusGained(e -> this.background.addStyleState(StyleState.FOCUSED));
        this.onFocusLost(e -> this.background.removeStyleState(StyleState.FOCUSED));
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

    void updateSelected(boolean selected) {
        if (this.hasStyleState(StyleState.SELECTED) == selected) return;

        this.setStyleState(StyleState.SELECTED, selected);
        this.background.setStyleState(StyleState.SELECTED, selected);

        float targetOpacity = selected ? 1.0f : 0.0f;
        float targetScale = selected ? 1.0f : 0.0f;

        long duration = ThemeManager.getStylesheet().get(this, StyleProps.ANIMATION_DURATION, 150L);
        this.dot.animate().duration(duration).easing(Easing.EASE_OUT_QUAD).opacity(targetOpacity);
        this.dot.animate().duration(duration).easing(Easing.EASE_OUT_BACK).scale(targetScale);
    }

    @Override
    public RadioButton<V> setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.outline.setStyleState(StyleState.DISABLED, !enabled);
        this.background.setStyleState(StyleState.DISABLED, !enabled);
        // TODO: TextComponent does not currently support disabled state styling from the stylesheet.
        this.label.setStyleState(StyleState.DISABLED, !enabled);
        return self();
    }
}