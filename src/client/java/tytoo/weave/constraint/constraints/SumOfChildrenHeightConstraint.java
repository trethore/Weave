package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;

import java.util.List;

public class SumOfChildrenHeightConstraint implements HeightConstraint {
    private final float padding;
    private final float gap;

    public SumOfChildrenHeightConstraint(float padding, float gap) {
        this.padding = padding;
        this.gap = gap;
    }

    @Override
    public float getHeight(Component<?> component) {
        List<Component<?>> children = component.getChildren();
        if (children.isEmpty()) {
            return padding * 2;
        }

        float totalHeight = 0;
        for (Component<?> child : children) {
            totalHeight += child.getRawHeight();
        }

        totalHeight += Math.max(0, children.size() - 1) * gap;
        totalHeight += padding * 2;

        return totalHeight;
    }
}