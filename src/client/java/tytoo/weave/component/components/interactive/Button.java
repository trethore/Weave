package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.function.Consumer;

public class Button extends BasePanel<Button> {
    public Button() {
        this.setFocusable(true);
        this.setWidth(Constraints.childBased(10));
        this.setHeight(Constraints.childBased(10));

        this.onMouseEnter(e -> updateVisualState());
        this.onMouseLeave(e -> updateVisualState());
        this.onFocusGained(e -> updateVisualState());
        this.onFocusLost(e -> updateVisualState());
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

    private void updateVisualState() {
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.getProperty(this.getClass(), "animation.duration", 150L);

        Color normalColor = stylesheet.getProperty(this.getClass(), "color.normal", new Color(100, 100, 100));
        Color hoveredColor = stylesheet.getProperty(this.getClass(), "color.hovered", new Color(120, 120, 120));
        Color focusedColor = stylesheet.getProperty(this.getClass(), "color.focused", new Color(140, 140, 140));

        Color targetColor = isFocused() ? focusedColor : (isHovered() ? hoveredColor : normalColor);

        this.animate().duration(duration).color(targetColor);
    }

    public Button onClick(Consumer<Button> action) {
        this.onMouseClick(e -> {
            if (e.getButton() == 0) action.accept(this);
        });
        return this;
    }
}