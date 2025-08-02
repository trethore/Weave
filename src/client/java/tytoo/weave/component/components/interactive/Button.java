package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;

public class Button extends InteractiveComponent<Button> {

    protected Button() {
        this.setWidth(Constraints.childBased(10));
        this.setHeight(Constraints.childBased(10));
    }

    public static Button create() {
        return new Button();
    }

    public static Button of(String text) {
        return new Button().addChildren(TextComponent.of(text).setX(Constraints.center()).setY(Constraints.center()));
    }

    public static Button of(Text text) {
        return new Button().addChildren(TextComponent.of(text).setX(Constraints.center()).setY(Constraints.center()));
    }

    @Override
    protected void updateVisualState() {
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.get(this.getClass(), StyleProps.ANIMATION_DURATION, 150L);

        Color normalColor = stylesheet.get(this.getClass(), StyleProps.COLOR_NORMAL, new Color(80, 80, 80));
        Color hoveredColor = stylesheet.get(this.getClass(), StyleProps.COLOR_HOVERED, new Color(100, 100, 100));
        Color focusedColor = stylesheet.get(this.getClass(), StyleProps.COLOR_FOCUSED, new Color(120, 120, 120));

        Color targetColor = isFocused() ? focusedColor : (isHovered() ? hoveredColor : normalColor);

        this.animate().duration(duration).color(targetColor);
    }

    public static final class StyleProps {
        public static final StyleProperty<Long> ANIMATION_DURATION = new StyleProperty<>("animation.duration", Long.class);
        public static final StyleProperty<Color> COLOR_NORMAL = new StyleProperty<>("color.normal", Color.class);
        public static final StyleProperty<Color> COLOR_HOVERED = new StyleProperty<>("color.hovered", Color.class);
        public static final StyleProperty<Color> COLOR_FOCUSED = new StyleProperty<>("color.focused", Color.class);

        private StyleProps() {
        }
    }
}