package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.theme.ThemeManager;

public class Window extends BasePanel<Window> {
    public Window() {
        this.parent = null;
        this.setX(Constraints.center());
        this.setY(Constraints.center());
        this.setWidth(Constraints.pixels(400));
        this.setHeight(Constraints.pixels(300));
        this.setColor(ThemeManager.getTheme().getWindowColor());
    }
}