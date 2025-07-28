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
            applyHorizontalLayout(component, children);
        } else {
            applyVerticalLayout(component, children);
        }
    }

    private void applyHorizontalLayout(Component<?> parent, List<Component<?>> children) {
        for (Component<?> child : children) {
            child.setY(Constraints.center());
        }

        if (alignment == Alignment.START || alignment == Alignment.CENTER || alignment == Alignment.END) {
            applyHorizontalAxisAlignment(parent, children);
        } else {
            applyHorizontalAxisDistribution(parent, children);
        }
    }

    private void applyHorizontalAxisAlignment(Component<?> parent, List<Component<?>> children) {
        if (alignment == Alignment.START) {
            children.getFirst().setX(Constraints.pixels(0));
        } else {
            children.getFirst().setX(c -> {
                float totalChildrenWidth = 0;
                if (!children.isEmpty()) {
                    for (Component<?> sibling : children) totalChildrenWidth += sibling.getRawWidth();
                    totalChildrenWidth += gap * (children.size() - 1);
                }
                return parent.getInnerLeft() + switch (alignment) {
                    case CENTER -> (parent.getInnerWidth() - totalChildrenWidth) / 2f;
                    case END -> parent.getInnerWidth() - totalChildrenWidth;
                    default -> 0;
                };
            });
        }
        for (int i = 1; i < children.size(); i++) {
            children.get(i).setX(Constraints.sibling(gap));
        }
    }

    private void applyHorizontalAxisDistribution(Component<?> parent, List<Component<?>> children) {
        for (int i = 0; i < children.size(); i++) {
            final int index = i;
            children.get(index).setX(c -> {
                if (children.isEmpty()) return parent.getInnerLeft();

                float totalSiblingsWidth = 0;
                for (Component<?> child : children) {
                    totalSiblingsWidth += child.getRawWidth();
                }
                float freeSpace = parent.getInnerWidth() - totalSiblingsWidth;

                float previousSiblingsWidth = 0;
                for (int j = 0; j < index; j++) {
                    previousSiblingsWidth += children.get(j).getWidth();
                }

                return parent.getInnerLeft() + previousSiblingsWidth + switch (alignment) {
                    case SPACE_BETWEEN -> {
                        if (children.size() <= 1) yield freeSpace / 2;
                        yield index * (freeSpace / (children.size() - 1));
                    }
                    case SPACE_AROUND -> {
                        if (children.isEmpty()) yield 0f;
                        float space = freeSpace / children.size();
                        yield index * space + space / 2;
                    }
                    case SPACE_EVENLY -> {
                        if (children.isEmpty()) yield 0f;
                        float space = freeSpace / (children.size() + 1);
                        yield (index + 1) * space;
                    }
                    default -> 0f;
                };
            });
        }
    }

    private void applyVerticalLayout(Component<?> parent, List<Component<?>> children) {
        for (Component<?> child : children) {
            child.setX(Constraints.center());
        }

        if (alignment == Alignment.START || alignment == Alignment.CENTER || alignment == Alignment.END) {
            applyVerticalAxisAlignment(parent, children);
        } else {
            applyVerticalAxisDistribution(parent, children);
        }
    }

    private void applyVerticalAxisAlignment(Component<?> parent, List<Component<?>> children) {
        if (alignment == Alignment.START) {
            children.getFirst().setY(Constraints.pixels(0));
        } else {
            children.getFirst().setY(c -> {
                float totalChildrenHeight = 0;
                if (!children.isEmpty()) {
                    for (Component<?> sibling : children) totalChildrenHeight += sibling.getRawHeight();
                    totalChildrenHeight += gap * (children.size() - 1);
                }
                return parent.getInnerTop() + switch (alignment) {
                    case CENTER -> (parent.getInnerHeight() - totalChildrenHeight) / 2f;
                    case END -> parent.getInnerHeight() - totalChildrenHeight;
                    default -> 0;
                };
            });
        }
        for (int i = 1; i < children.size(); i++) {
            children.get(i).setY(Constraints.sibling(gap));
        }
    }

    private void applyVerticalAxisDistribution(Component<?> parent, List<Component<?>> children) {
        for (int i = 0; i < children.size(); i++) {
            final int index = i;
            children.get(index).setY(c -> {
                if (children.isEmpty()) return parent.getInnerTop();
                float totalSiblingsHeight = 0;
                for (Component<?> child : children) {
                    totalSiblingsHeight += child.getRawHeight();
                }
                float freeSpace = parent.getInnerHeight() - totalSiblingsHeight;
                float previousSiblingsHeight = 0;
                for (int j = 0; j < index; j++) {
                    previousSiblingsHeight += children.get(j).getHeight();
                }
                return parent.getInnerTop() + previousSiblingsHeight + switch (alignment) {
                    case SPACE_BETWEEN -> {
                        if (children.size() <= 1) yield freeSpace / 2;
                        yield index * (freeSpace / (children.size() - 1));
                    }
                    case SPACE_AROUND -> {
                        if (children.isEmpty()) yield 0f;
                        float space = freeSpace / children.size();
                        yield index * space + space / 2;
                    }
                    case SPACE_EVENLY -> {
                        if (children.isEmpty()) yield 0f;
                        float space = freeSpace / (children.size() + 1);
                        yield (index + 1) * space;
                    }
                    default -> 0f;
                };
            });
        }
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum Alignment {
        START,
        CENTER,
        END,
        SPACE_BETWEEN,
        SPACE_AROUND,
        SPACE_EVENLY
    }
}