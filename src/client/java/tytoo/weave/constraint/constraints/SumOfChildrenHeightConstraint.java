package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;

import java.util.List;

public record SumOfChildrenHeightConstraint(float padding, float gap) implements HeightConstraint {

    @Override
    public float calculateHeight(Component<?> component, float parentHeight) {
        List<Component<?>> children = component.getChildren();
        if (children.isEmpty()) {
            return padding * 2;
        }

        float totalHeight = 0;
        long visibleCount = 0;
        for (Component<?> child : children) {
            if (child.isVisible()) {
                totalHeight += child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom();
                visibleCount++;
            }
        }

        totalHeight += Math.max(0, visibleCount - 1) * gap;
        totalHeight += padding * 2 + component.getLayoutState().getPadding().top() + component.getLayoutState().getPadding().bottom();

        return totalHeight;
    }
}
