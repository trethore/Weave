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
    public float getX(Component<?> component) {
        if (component.getParent() == null) return value;
        return component.getParent().getInnerLeft() + value;
    }

    @Override
    public float getY(Component<?> component) {
        if (component.getParent() == null) return value;
        return component.getParent().getInnerTop() + value;
    }

    @Override
    public float getWidth(Component<?> component) {
        return value;
    }

    @Override
    public float getHeight(Component<?> component) {
        return value;
    }
}