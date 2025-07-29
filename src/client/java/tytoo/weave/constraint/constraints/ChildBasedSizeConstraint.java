package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;

public class ChildBasedSizeConstraint implements WidthConstraint, HeightConstraint {
    private final float padding;

    public ChildBasedSizeConstraint(float padding) {
        this.padding = padding;
    }

    @Override
    public float calculateWidth(Component<?> component, float parentWidth) {
        if (component.getChildren().isEmpty()) return this.padding * 2;
        float maxWidth = 0;
        for (Component<?> child : component.getChildren()) {
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + child.getMargin().left + child.getMargin().right);
        }
        return maxWidth + this.padding * 2;
    }

    @Override
    public float calculateHeight(Component<?> component, float parentHeight) {
        if (component.getChildren().isEmpty()) return this.padding * 2;
        float maxHeight = 0;
        for (Component<?> child : component.getChildren()) {
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + child.getMargin().top + child.getMargin().bottom);
        }
        return maxHeight + this.padding * 2;
    }
}