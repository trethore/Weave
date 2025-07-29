package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

public class PixelConstraint implements XConstraint, YConstraint, WidthConstraint, HeightConstraint {
    private final float value;

    public PixelConstraint(float value) {
        this.value = value;
    }

    @Override
    public float calculateX(Component<?> component, float parentWidth, float componentWidth) {
        return value;
    }

    @Override
    public float calculateY(Component<?> component, float parentHeight, float componentHeight) {
        return value;
    }

    @Override
    public float calculateWidth(Component<?> component, float parentWidth) {
        return value;
    }

    @Override
    public float calculateHeight(Component<?> component, float parentHeight) {
        return value;
    }
}