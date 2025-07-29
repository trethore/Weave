package tytoo.weave.constraint;

import tytoo.weave.component.Component;

public interface HeightConstraint {
    float calculateHeight(Component<?> component, float parentHeight);
}