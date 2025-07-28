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
            applyHorizontalLayout(children);
        } else { // VERTICAL
            applyVerticalLayout(children);
        }
    }

    private void applyHorizontalLayout(final List<Component<?>> children) {
        for (Component<?> child : children) {
            child.setY(Constraints.center());
        }

        if (alignment == Alignment.START || alignment == Alignment.CENTER || alignment == Alignment.END) {
            if (alignment == Alignment.START) {
                children.getFirst().setX(Constraints.pixels(0));
            } else {
                children.getFirst().setX(c -> {
                    if (c.getParent() == null) return 0f;
                    List<Component<?>> siblings = c.getParent().getChildren();
                    float totalChildrenWidth = 0;
                    if (!siblings.isEmpty()) {
                        for (Component<?> sibling : siblings) totalChildrenWidth += sibling.getRawWidth();
                        totalChildrenWidth += gap * (siblings.size() - 1);
                    }
                    return c.getParent().getInnerLeft() + switch (alignment) {
                        case CENTER -> (c.getParent().getInnerWidth() - totalChildrenWidth) / 2f;
                        case END -> c.getParent().getInnerWidth() - totalChildrenWidth;
                        default -> 0;
                    };
                });
            }
            for (int i = 1; i < children.size(); i++) {
                children.get(i).setX(Constraints.sibling(gap));
            }
        } else {
            for (int i = 0; i < children.size(); i++) {
                final int index = i;
                children.get(index).setX(c -> {
                    Component<?> p = c.getParent();
                    if (p == null) return 0f;
                    List<Component<?>> siblings = p.getChildren();
                    if (siblings.isEmpty()) return p.getInnerLeft();

                    float totalSiblingsWidth = siblings.stream().map(Component::getRawWidth).reduce(0f, Float::sum);
                    float freeSpace = p.getInnerWidth() - totalSiblingsWidth;

                    float previousSiblingsWidth = 0;
                    for (int j = 0; j < index; j++) {
                        previousSiblingsWidth += siblings.get(j).getWidth();
                    }

                    return p.getInnerLeft() + previousSiblingsWidth + switch (alignment) {
                        case SPACE_BETWEEN -> {
                            if (siblings.size() <= 1) yield freeSpace / 2;
                            yield index * (freeSpace / (siblings.size() - 1));
                        }
                        case SPACE_AROUND -> {
                            if (siblings.isEmpty()) yield 0f;
                            float space = freeSpace / siblings.size();
                            yield index * space + space / 2;
                        }
                        case SPACE_EVENLY -> {
                            if (siblings.isEmpty()) yield 0f;
                            float space = freeSpace / (siblings.size() + 1);
                            yield (index + 1) * space;
                        }
                        default -> 0f;
                    };
                });
            }
        }
    }

    private void applyVerticalLayout(final List<Component<?>> children) {
        for (Component<?> child : children) {
            child.setX(Constraints.center());
        }

        if (alignment == Alignment.START || alignment == Alignment.CENTER || alignment == Alignment.END) {
            if (alignment == Alignment.START) {
                children.getFirst().setY(Constraints.pixels(0));
            } else {
                children.getFirst().setY(c -> {
                    if (c.getParent() == null) return 0f;
                    List<Component<?>> siblings = c.getParent().getChildren();
                    float totalChildrenHeight = 0;
                    if (!siblings.isEmpty()) {
                        for (Component<?> sibling : siblings) totalChildrenHeight += sibling.getRawHeight();
                        totalChildrenHeight += gap * (siblings.size() - 1);
                    }
                    return c.getParent().getInnerTop() + switch (alignment) {
                        case CENTER -> (c.getParent().getInnerHeight() - totalChildrenHeight) / 2f;
                        case END -> c.getParent().getInnerHeight() - totalChildrenHeight;
                        default -> 0;
                    };
                });
            }
            for (int i = 1; i < children.size(); i++) {
                children.get(i).setY(Constraints.sibling(gap));
            }
        } else {
            for (int i = 0; i < children.size(); i++) {
                final int index = i;
                children.get(index).setY(c -> {
                    Component<?> p = c.getParent();
                    if (p == null) return 0f;
                    List<Component<?>> siblings = p.getChildren();
                    if (siblings.isEmpty()) return p.getInnerTop();

                    float totalSiblingsHeight = siblings.stream().map(Component::getRawHeight).reduce(0f, Float::sum);
                    float freeSpace = p.getInnerHeight() - totalSiblingsHeight;

                    float previousSiblingsHeight = 0;
                    for (int j = 0; j < index; j++) {
                        previousSiblingsHeight += siblings.get(j).getHeight();
                    }

                    return p.getInnerTop() + previousSiblingsHeight + switch (alignment) {
                        case SPACE_BETWEEN -> {
                            if (siblings.size() <= 1) yield freeSpace / 2;
                            yield index * (freeSpace / (siblings.size() - 1));
                        }
                        case SPACE_AROUND -> {
                            if (siblings.isEmpty()) yield 0f;
                            float space = freeSpace / siblings.size();
                            yield index * space + space / 2;
                        }
                        case SPACE_EVENLY -> {
                            if (siblings.isEmpty()) yield 0f;
                            float space = freeSpace / (siblings.size() + 1);
                            yield (index + 1) * space;
                        }
                        default -> 0f;
                    };
                });
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
        END,
        SPACE_BETWEEN,
        SPACE_AROUND,
        SPACE_EVENLY
    }
}