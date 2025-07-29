package tytoo.weave.constraint;

import tytoo.weave.component.Component;

public interface YConstraint {
    float calculateY(Component<?> component, float parentHeight, float componentHeight);
}