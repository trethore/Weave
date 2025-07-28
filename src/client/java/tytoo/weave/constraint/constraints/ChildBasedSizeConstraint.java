package tytoo.weave.constraint.constraints;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;

public class ChildBasedSizeConstraint implements WidthConstraint, HeightConstraint {
    private final float padding;

    public ChildBasedSizeConstraint(float padding) {
        this.padding = padding;
    }

    @Override
    public float getWidth(Component<?> component) {
        if (component.getChildren().isEmpty()) return this.padding * 2;
        float maxWidth = 0;
        for (Component<?> child : component.getChildren()) {
            maxWidth = Math.max(maxWidth, child.getRawWidth());
        }
        return maxWidth + this.padding * 2;
    }

    @Override
    public float getHeight(Component<?> component) {
        if (component.getChildren().isEmpty()) return this.padding * 2;
        float maxHeight = 0;
        for (Component<?> child : component.getChildren()) {
            maxHeight = Math.max(maxHeight, child.getRawHeight());
        }
        return maxHeight + this.padding * 2;
    }
}