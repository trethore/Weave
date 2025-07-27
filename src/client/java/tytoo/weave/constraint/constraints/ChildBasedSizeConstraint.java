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
        if (component.getChildren().isEmpty()) return padding * 2;
        float totalWidth = 0;
        for (Component<?> child : component.getChildren()) {
            totalWidth += child.getRawWidth();
        }
        return totalWidth + (padding * (component.getChildren().size() + 1));
    }

    @Override
    public float getHeight(Component<?> component) {
        if (component.getChildren().isEmpty()) return 0;
        float totalHeight = 0;
        for (Component<?> child : component.getChildren()) {
            totalHeight += child.getRawHeight();
        }
        return totalHeight + (padding * (component.getChildren().size() - 1));
    }
}