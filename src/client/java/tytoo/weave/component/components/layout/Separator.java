package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;

import java.awt.*;

public class Separator extends BasePanel<Separator> {
    public Separator(Orientation orientation) {
        if (orientation == Orientation.HORIZONTAL) {
            this.setHeight(Constraints.pixels(1));
            this.setWidth(Constraints.relative(1.0f));
        } else {
            this.setWidth(Constraints.pixels(1));
            this.setHeight(Constraints.relative(1.0f));
        }
        this.setColor(new Color(128, 128, 128));
    }

    public static Separator horizontal() {
        return new Separator(Orientation.HORIZONTAL);
    }

    public static Separator vertical() {
        return new Separator(Orientation.VERTICAL);
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
}