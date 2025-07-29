package tytoo.weave.constraint;

import tytoo.weave.component.Component;

public interface XConstraint {
    float calculateX(Component<?> component, float parentWidth, float componentWidth);
}