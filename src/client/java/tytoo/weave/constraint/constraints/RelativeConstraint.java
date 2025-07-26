package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

public class RelativeConstraint implements XConstraint, YConstraint, WidthConstraint, HeightConstraint {
    private final float value;

    public RelativeConstraint(float value) {
        this.value = value;
    }

    @Override
    public float getX(Component<?> component) {
        Component<?> parent = component.getParent();
        return parent.getLeft() + parent.getWidth() * value;
    }

    @Override
    public float getY(Component<?> component) {
        Component<?> parent = component.getParent();
        return parent.getTop() + parent.getHeight() * value;
    }

    @Override
    public float getWidth(Component<?> component) {
        return component.getParent().getWidth() * value;
    }

    @Override
    public float getHeight(Component<?> component) {
        return component.getParent().getHeight() * value;
    }
}