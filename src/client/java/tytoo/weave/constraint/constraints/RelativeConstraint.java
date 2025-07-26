package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.utils.McUtils;

public class RelativeConstraint implements XConstraint, YConstraint, WidthConstraint, HeightConstraint {
    private final float value;

    public RelativeConstraint(float value) {
        this.value = value;
    }

    @Override
    public float getX(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledWidth() * value)
                    .orElse(0f);
        }
        return parent.getLeft() + parent.getWidth() * value;
    }

    @Override
    public float getY(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledHeight() * value)
                    .orElse(0f);
        }
        return parent.getTop() + parent.getHeight() * value;
    }

    @Override
    public float getWidth(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledWidth() * value)
                    .orElse(0f);
        }
        return parent.getWidth() * value;
    }

    @Override
    public float getHeight(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledHeight() * value)
                    .orElse(0f);
        }
        return parent.getHeight() * value;
    }
}