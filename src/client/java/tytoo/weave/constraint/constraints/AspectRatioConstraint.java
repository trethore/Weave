package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;

public class AspectRatioConstraint implements WidthConstraint, HeightConstraint {
    private final float ratio;

    public AspectRatioConstraint(float ratio) {
        this.ratio = ratio;
    }

    @Override
    public float getWidth(Component<?> component) {
        return component.getRawHeight() * ratio;
    }

    @Override
    public float getHeight(Component<?> component) {
        return component.getRawWidth() / ratio;
    }
}