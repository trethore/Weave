package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;

public record AspectRatioConstraint(float ratio) implements WidthConstraint, HeightConstraint {

    @Override
    public float calculateWidth(Component<?> component, float parentWidth) {
        return component.getMeasuredHeight() * ratio;
    }

    @Override
    public float calculateHeight(Component<?> component, float parentHeight) {
        return component.getMeasuredWidth() / ratio;
    }
}
