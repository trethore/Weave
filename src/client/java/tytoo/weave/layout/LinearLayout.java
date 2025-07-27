package tytoo.weave.layout;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.constraints.Constraints;

import java.util.List;

public class LinearLayout implements Layout {
    private final Orientation orientation;
    private final Alignment alignment;
    private final float gap;

    private LinearLayout(Orientation orientation, Alignment alignment, float gap) {
        this.orientation = orientation;
        this.alignment = alignment;
        this.gap = gap;
    }

    public static LinearLayout of(Orientation orientation, Alignment alignment, float gap) {
        return new LinearLayout(orientation, alignment, gap);
    }

    public static LinearLayout of(Orientation orientation, Alignment alignment) {
        return new LinearLayout(orientation, alignment, 0);
    }

    @Override
    public void apply(Component<?> component) {
        List<Component<?>> children = component.getChildren();
        if (children.isEmpty()) return;

        if (orientation == Orientation.HORIZONTAL) {
            if (alignment == Alignment.START) {
                children.getFirst().setX(Constraints.pixels(0));
            } else {
                children.getFirst().setX(c -> {
                    Component<?> parent = c.getParent();
                    if (parent == null) return 0f;

                    List<Component<?>> siblings = parent.getChildren();
                    float totalChildrenWidth = 0;
                    if (!siblings.isEmpty()) {
                        for (Component<?> sibling : siblings) totalChildrenWidth += sibling.getWidth();
                        totalChildrenWidth += gap * (siblings.size() - 1);
                    }

                    return parent.getLeft() + switch (alignment) {
                        case CENTER -> (parent.getWidth() - totalChildrenWidth) / 2;
                        case END -> parent.getWidth() - totalChildrenWidth;
                        default -> 0;
                    };
                });
            }

            for (int i = 1; i < children.size(); i++) {
                children.get(i).setX(Constraints.sibling(gap));
            }

            for (Component<?> child : children) {
                child.setY(Constraints.center());
            }
        } else { // VERTICAL
            if (alignment == Alignment.START) {
                children.getFirst().setY(Constraints.pixels(0));
            } else {
                children.getFirst().setY(c -> {
                    Component<?> parent = c.getParent();
                    if (parent == null) return 0f;

                    List<Component<?>> siblings = parent.getChildren();
                    float totalChildrenHeight = 0;
                    if (!siblings.isEmpty()) {
                        for (Component<?> sibling : siblings) totalChildrenHeight += sibling.getHeight();
                        totalChildrenHeight += gap * (siblings.size() - 1);
                    }

                    return parent.getTop() + switch (alignment) {
                        case CENTER -> (parent.getHeight() - totalChildrenHeight) / 2;
                        case END -> parent.getHeight() - totalChildrenHeight;
                        default -> 0;
                    };
                });
            }

            for (int i = 1; i < children.size(); i++) {
                children.get(i).setY(Constraints.sibling(gap));
            }

            for (Component<?> child : children) {
                child.setX(Constraints.center());
            }
        }
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum Alignment {
        START,
        CENTER,
        END
    }
}