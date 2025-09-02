package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.utils.McUtils;

public record RelativeConstraint(float value,
                                 float offset) implements XConstraint, YConstraint, WidthConstraint, HeightConstraint {

    @Override
    public float calculateX(Component<?> component, float parentWidth, float componentWidth) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledWidth() * value + offset)
                    .orElse(0f);
        }
        return parentWidth * value + offset;
    }

    @Override
    public float calculateY(Component<?> component, float parentHeight, float componentHeight) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledHeight() * value + offset)
                    .orElse(0f);
        }
        return parentHeight * value + offset;
    }

    @Override
    public float calculateWidth(Component<?> component, float parentWidth) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledWidth() * value)
                    .orElse(0f);
        }
        return parentWidth * value + offset;
    }

    @Override
    public float calculateHeight(Component<?> component, float parentHeight) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> mc.getWindow().getScaledHeight() * value)
                    .orElse(0f);
        }
        return parentHeight * value + offset;
    }
}
