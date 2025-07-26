package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.utils.McUtils;

public class CenterConstraint implements XConstraint, YConstraint {
    @Override
    public float getX(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> (mc.getWindow().getScaledWidth() - component.getWidth()) / 2f)
                    .orElse(0f);
        }
        return parent.getLeft() + (parent.getWidth() - component.getWidth()) / 2f;
    }

    @Override
    public float getY(Component<?> component) {
        Component<?> parent = component.getParent();
        if (parent == null) {
            return McUtils.getMc()
                    .map(mc -> (mc.getWindow().getScaledHeight() - component.getHeight()) / 2f)
                    .orElse(0f);
        }
        return parent.getTop() + (parent.getHeight() - component.getHeight()) / 2f;
    }
}