package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.utils.McUtils;

public class RelativeConstraint implements XConstraint, YConstraint, WidthConstraint, HeightConstraint {
    private final float value;
    private final float offset;

    public RelativeConstraint(float value, float offset) {
        this.value = value;
        this.offset = offset;
    }

    @Override
    public float getX(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledWidth() * value)
                    .orElse(0f);
        }
        return parent.getInnerLeft() + parent.getInnerWidth() * value + offset;
    }

    @Override
    public float getY(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledHeight() * value)
                    .orElse(0f);
        }
        return parent.getInnerTop() + parent.getInnerHeight() * value + offset;
    }

    @Override
    public float getWidth(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledWidth() * value)
                    .orElse(0f);
        }
        return parent.getInnerWidth() * value + offset;
    }

    @Override
    public float getHeight(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledHeight() * value)
                    .orElse(0f);
        }
        return parent.getInnerHeight() * value + offset;
    }
}