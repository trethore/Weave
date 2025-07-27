package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;

import java.util.List;

public class SiblingConstraint implements XConstraint, YConstraint {
    private final float padding;

    public SiblingConstraint(float padding) {
        this.padding = padding;
    }

    @Override
    public float getX(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) return padding;

        List<Component<?>> siblings = parent.getChildren();
        int index = siblings.indexOf(component);

        if (index == 0) {
            return parent.getInnerLeft() + padding;
        }

        Component<?> previousSibling = siblings.get(index - 1);
        return previousSibling.getRawLeft() + previousSibling.getRawWidth() + padding;
    }

    @Override
    public float getY(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) return padding;

        List<Component<?>> siblings = parent.getChildren();
        int index = siblings.indexOf(component);

        if (index == 0) {
            return parent.getInnerTop() + padding;
        }

        Component<?> previousSibling = siblings.get(index - 1);
        return previousSibling.getRawTop() + previousSibling.getRawHeight() + padding;
    }
}