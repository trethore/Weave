package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;

public class Window extends BasePanel<Window> {
    protected Window() {
        this.setX(Constraints.center());
        this.setY(Constraints.center());
        this.setWidth(Constraints.pixels(400));
        this.setHeight(Constraints.pixels(300));
    }

    public static Window create() {
        return new Window();
    }
}