package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

public class Window extends BasePanel<Window> {
    protected Window() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
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
        public static final StyleSlot DEFAULT_WIDTH = StyleSlot.of("window.default-width", Window.class, Float.class);
        public static final StyleSlot DEFAULT_HEIGHT = StyleSlot.of("window.default-height", Window.class, Float.class);

        private StyleProps() {
        }
    }
}
