package tytoo.weave.component;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.Layout;
import tytoo.weave.style.EdgeInsets;

public class LayoutState {
    private final Component<?> owner;
    public Constraints constraints;
    public EdgeInsets margin = EdgeInsets.zero();
    public EdgeInsets padding = EdgeInsets.zero();
    @Nullable
    public Layout layout;
    public Object layoutData;
    public float measuredWidth, measuredHeight;
    public float finalX, finalY, finalWidth, finalHeight;
    public boolean layoutDirty = true;

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

    public void setLayoutData(Object layoutData) {
        this.layoutData = layoutData;
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
}