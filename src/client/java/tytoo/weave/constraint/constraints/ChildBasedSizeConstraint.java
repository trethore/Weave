package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;

import java.util.function.ToDoubleFunction;

public class ChildBasedSizeConstraint implements WidthConstraint, HeightConstraint {
    private final float padding;

    public ChildBasedSizeConstraint(float padding) {
        this.padding = padding;
    }

    private float calculateMaxSize(Component<?> component, ToDoubleFunction<Component<?>> measureFunc) {
        if (component.getChildren().isEmpty()) {
            return this.padding * 2;
        }
        double maxSize = 0;
        for (Component<?> child : component.getChildren()) {
            if (child.isVisible()) {
                maxSize = Math.max(maxSize, measureFunc.applyAsDouble(child));
            }
        }
        return (float) maxSize + this.padding * 2;
    }

    @Override
    public float calculateWidth(Component<?> component, float parentWidth) {
        return calculateMaxSize(component, child -> child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right());
    }

    @Override
    public float calculateHeight(Component<?> component, float parentHeight) {
        return calculateMaxSize(component, child -> child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom());
    }
}