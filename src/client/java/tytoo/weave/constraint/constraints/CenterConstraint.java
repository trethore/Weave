package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.utils.McUtils;

public class CenterConstraint implements XConstraint, YConstraint {

    @Override
    public float calculateX(Component<?> component, float parentWidth, float componentWidth) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> (mc.getWindow().getScaledWidth() - componentWidth) / 2f)
                    .orElse(0f);
        }
        return (parentWidth - componentWidth) / 2f;
    }

    @Override
    public float calculateY(Component<?> component, float parentHeight, float componentHeight) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> (mc.getWindow().getScaledHeight() - componentHeight) / 2f)
                    .orElse(0f);
        }
        return (parentHeight - componentHeight) / 2f;
    }
}