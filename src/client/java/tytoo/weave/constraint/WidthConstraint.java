package tytoo.weave.constraint;

import tytoo.weave.component.Component;

public interface WidthConstraint {
    float calculateWidth(Component<?> component, float parentWidth);
}