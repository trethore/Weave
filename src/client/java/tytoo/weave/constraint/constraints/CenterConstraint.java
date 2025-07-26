package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

public class CenterConstraint implements XConstraint, YConstraint {
    @Override
    public float getX(Component<?> component) {
        Component<?> parent = component.getParent();
        return parent.getLeft() + (parent.getWidth() - component.getWidth()) / 2f;
    }

    @Override
    public float getY(Component<?> component) {
        Component<?> parent = component.getParent();
        return parent.getTop() + (parent.getHeight() - component.getHeight()) / 2f;
    }
}