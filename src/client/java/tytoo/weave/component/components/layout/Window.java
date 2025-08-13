package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;

public class Window extends BasePanel<Window> {
    protected Window() {
        var stylesheet = ThemeManager.getStylesheet();
        float defaultWidth = stylesheet.get(this, StyleProps.DEFAULT_WIDTH, 400f);
        float defaultHeight = stylesheet.get(this, StyleProps.DEFAULT_HEIGHT, 300f);

        this.setX(Constraints.center());
        this.setY(Constraints.center());
        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight(Constraints.pixels(defaultHeight));
    }

    public static Window create() {
        return new Window();
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> DEFAULT_WIDTH = new StyleProperty<>("window.default-width", Float.class);
        public static final StyleProperty<Float> DEFAULT_HEIGHT = new StyleProperty<>("window.default-height", Float.class);

        private StyleProps() {
        }
    }
}