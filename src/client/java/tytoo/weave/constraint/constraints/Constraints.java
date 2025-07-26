package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

public class Constraints {
    private final Component<?> component;

    private XConstraint x = new PixelConstraint(0);
    private YConstraint y = new PixelConstraint(0);
    private WidthConstraint width = new PixelConstraint(0);
    private HeightConstraint height = new PixelConstraint(0);

    public Constraints(Component<?> component) {
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

    public static RelativeConstraint relative(float value) {
        return new RelativeConstraint(value);
    }

    public static CenterConstraint center() {
        return new CenterConstraint();
    }

    public static SiblingConstraint sibling(float padding) {
        return new SiblingConstraint(padding);
    }

    public static SiblingConstraint sibling() {
        return new SiblingConstraint(0);
    }

    public static ChildBasedSizeConstraint childBased(float padding) {
        return new ChildBasedSizeConstraint(padding);
    }

    public static ChildBasedSizeConstraint childBased() {
        return new ChildBasedSizeConstraint(0);
    }
}