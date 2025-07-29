package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

@SuppressWarnings("unused")
public class Constraints {
    private final Component<?> component;

    private XConstraint x = new PixelConstraint(0);
    private YConstraint y = new PixelConstraint(0);
    private WidthConstraint width = new PixelConstraint(0);
    private HeightConstraint height = new PixelConstraint(0);

    private float minWidth = 0;
    private float maxWidth = Float.MAX_VALUE;
    private float minHeight = 0;
    private float maxHeight = Float.MAX_VALUE;

    public Constraints(Component<?> component) {
        this.component = component;
    }

    public static PixelConstraint pixels(float value) {
        return new PixelConstraint(value);
    }

    public static RelativeConstraint relative(float value) {
        return new RelativeConstraint(value, 0);
    }

    public static RelativeConstraint relative(float value, float offset) {
        return new RelativeConstraint(value, offset);
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

    public static AspectRatioConstraint aspect(float ratio) {
        return new AspectRatioConstraint(ratio);
    }

    public static SumOfChildrenHeightConstraint sumOfChildrenHeight(float padding, float gap) {
        return new SumOfChildrenHeightConstraint(padding, gap);
    }

    public XConstraint getXConstraint() {
        return x;
    }

    public YConstraint getYConstraint() {
        return y;
    }

    public WidthConstraint getWidthConstraint() {
        return width;
    }

    public HeightConstraint getHeightConstraint() {
        return height;
    }

    public float getX() {
        return x.getX(component);
    }

    public void setX(XConstraint x) {
        this.x = x;
    }

    public float getY() {
        return y.getY(component);
    }

    public void setY(YConstraint y) {
        this.y = y;
    }

    public float getWidth() {
        float calculatedWidth = width.getWidth(component);
        return Math.max(this.minWidth, Math.min(this.maxWidth, calculatedWidth));
    }

    public void setWidth(WidthConstraint width) {
        this.width = width;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getHeight() {
        float calculatedHeight = height.getHeight(component);
        return Math.max(this.minHeight, Math.min(this.maxHeight, calculatedHeight));
    }

    public void setHeight(HeightConstraint height) {
        this.height = height;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }
}