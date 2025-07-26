package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

public class Constraints {
    private final Component component;

    private XConstraint x = new PixelConstraint(0);
    private YConstraint y = new PixelConstraint(0);
    private WidthConstraint width = new PixelConstraint(0);
    private HeightConstraint height = new PixelConstraint(0);

    public Constraints(Component component) {
        this.component = component;
    }

    public float getX() { return x.getX(component); }
    public void setX(XConstraint x) { this.x = x; }

    public float getY() { return y.getY(component); }
    public void setY(YConstraint y) { this.y = y; }

    public float getWidth() { return width.getWidth(component); }
    public void setWidth(WidthConstraint width) { this.width = width; }

    public float getHeight() { return height.getHeight(component); }
    public void setHeight(HeightConstraint height) { this.height = height; }

    public static PixelConstraint pixels(float value) {
        return new PixelConstraint(value);
    }
}