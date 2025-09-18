package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.contract.ComponentStyleProperties;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

public class Window extends BasePanel<Window> {
    protected Window() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float defaultWidth = stylesheet.get(this, ComponentStyleProperties.WindowStyles.DEFAULT_WIDTH, 400f);
        float defaultHeight = stylesheet.get(this, ComponentStyleProperties.WindowStyles.DEFAULT_HEIGHT, 300f);

        this.setX(Constraints.center());
        this.setY(Constraints.center());
        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight(Constraints.pixels(defaultHeight));
    }

    public static Window create() {
        return new Window();
    }

}
