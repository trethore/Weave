package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.WidthConstraint;

import java.util.List;

public record SumOfChildrenWidthConstraint(float padding, float gap) implements WidthConstraint {

    @Override
    public float calculateWidth(Component<?> component, float parentWidth) {
        List<Component<?>> children = component.getChildren();
        if (children.isEmpty()) {
            return padding * 2;
        }

        float totalWidth = 0;
        long visibleCount = 0;
        for (Component<?> child : children) {
            if (child.isVisible()) {
                totalWidth += child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right();
                visibleCount++;
            }
        }

        totalWidth += Math.max(0, visibleCount - 1) * gap;
        totalWidth += padding * 2 + component.getLayoutState().getPadding().left() + component.getLayoutState().getPadding().right();

        return totalWidth;
    }
}
