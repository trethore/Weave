package tytoo.weave.component.components;

import tytoo.weave.constraint.constraints.Constraints;

import java.awt.*;

public class Window extends Panel {
    public Window() {
        this.parent = null;

        this.setX(Constraints.center());
        this.setY(Constraints.center());
        this.setWidth(Constraints.pixels(400));
        this.setHeight(Constraints.pixels(300));
        this.setColor(new Color(0, 0, 0, 100));
    }
}