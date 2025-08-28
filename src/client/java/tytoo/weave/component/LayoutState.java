package tytoo.weave.component;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.Layout;
import tytoo.weave.style.EdgeInsets;

public class LayoutState {
    private final Component<?> owner;
    private final Constraints constraints;
    private EdgeInsets margin = EdgeInsets.zero();
    private EdgeInsets padding = EdgeInsets.zero();
    @Nullable
    private Layout layout;
    private Object layoutData;
    private float measuredWidth, measuredHeight;
    private float finalX, finalY, finalWidth, finalHeight;
    private boolean layoutDirty = true;
    private boolean managed = true;

    public LayoutState(Component<?> owner) {
        this.owner = owner;
        this.constraints = new Constraints(owner);
    }

    public void invalidateLayout() {
        if (!this.layoutDirty) {
            this.layoutDirty = true;
            if (owner.getParent() != null) {
                owner.getParent().getLayoutState().invalidateLayout();
            }
        }
    }

    public float getLeft() {
        return finalX + margin.left();
    }

    public float getTop() {
        return finalY + margin.top();
    }

    public float getWidth() {
        float rawWidth = finalWidth;
        if (rawWidth == 0) return 0;
        return rawWidth - margin.left() - margin.right();
    }

    public float getHeight() {
        float rawHeight = finalHeight;
        if (rawHeight == 0) return 0;
        return rawHeight - margin.top() - margin.bottom();
    }

    public float getInnerLeft() {
        return getLeft() + padding.left();
    }

    public float getInnerTop() {
        return getTop() + padding.top();
    }

    public float getInnerWidth() {
        return getWidth() - padding.left() - padding.right();
    }

    public float getInnerHeight() {
        return getHeight() - padding.top() - padding.bottom();
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public EdgeInsets getMargin() {
        return margin;
    }

    public void setMargin(EdgeInsets margin) {
        this.margin = margin;
    }

    public EdgeInsets getPadding() {
        return padding;
    }

    public void setPadding(EdgeInsets padding) {
        this.padding = padding;
    }

    @Nullable
    public Layout getLayout() {
        return layout;
    }

    public void setLayout(@Nullable Layout layout) {
        this.layout = layout;
    }

    public Object getLayoutData() {
        return layoutData;
    }

    public void setLayoutData(Object layoutData) {
        this.layoutData = layoutData;
    }

    public float getMeasuredWidth() {
        return measuredWidth;
    }

    public void setMeasuredWidth(float measuredWidth) {
        this.measuredWidth = measuredWidth;
    }

    public float getMeasuredHeight() {
        return measuredHeight;
    }

    public void setMeasuredHeight(float measuredHeight) {
        this.measuredHeight = measuredHeight;
    }

    public float getFinalX() {
        return finalX;
    }

    public void setFinalX(float finalX) {
        this.finalX = finalX;
    }

    public float getFinalY() {
        return finalY;
    }

    public void setFinalY(float finalY) {
        this.finalY = finalY;
    }

    public float getFinalWidth() {
        return finalWidth;
    }

    public void setFinalWidth(float finalWidth) {
        this.finalWidth = finalWidth;
    }

    public float getFinalHeight() {
        return finalHeight;
    }

    public void setFinalHeight(float finalHeight) {
        this.finalHeight = finalHeight;
    }

    public boolean isLayoutDirty() {
        return layoutDirty;
    }

    public void setLayoutDirty(boolean layoutDirty) {
        this.layoutDirty = layoutDirty;
    }

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }
}