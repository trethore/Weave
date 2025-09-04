package tytoo.weave.component.components.interactive;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.animation.Easings;
import tytoo.weave.component.NamedPart;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

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
        public static final StyleProperty<Float> GAP = new StyleProperty<>("radio.gap", Float.class);
        public static final StyleProperty<Float> OUTLINE_SIZE = new StyleProperty<>("radio.outline.size", Float.class);
        public static final StyleProperty<Float> DOT_SIZE = new StyleProperty<>("radio.dot.size", Float.class);

        private StyleProps() {
        }
    }
}
