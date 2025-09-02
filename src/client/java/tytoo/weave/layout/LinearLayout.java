package tytoo.weave.layout;

import tytoo.weave.WeaveClient;
import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.constraints.Constraints;

import java.util.List;
import java.util.stream.Collectors;

public record LinearLayout(Orientation orientation, Alignment alignment, CrossAxisAlignment crossAxisAlignment,
                           float gap) implements Layout {

    public static LinearLayout of(Orientation orientation, Alignment alignment, float gap) {
        return new LinearLayout(orientation, alignment, CrossAxisAlignment.CENTER, gap);
    }

    public static LinearLayout of(Orientation orientation, Alignment alignment) {
        return new LinearLayout(orientation, alignment, CrossAxisAlignment.CENTER, 0);
    }

    public static LinearLayout of(Orientation orientation, Alignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment, float gap) {
        return new LinearLayout(orientation, mainAxisAlignment, crossAxisAlignment, gap);
    }

    public static LinearLayout of(Orientation orientation, Alignment mainAxisAlignment, CrossAxisAlignment crossAxisAlignment) {
        return new LinearLayout(orientation, mainAxisAlignment, crossAxisAlignment, 0);
    }

    private static Data getLayoutData(Component<?> component) {
        Object layoutData = component.getLayoutData();
        if (layoutData instanceof Data) {
            return (Data) layoutData;
        }
        return new Data(0);
    }

    public float getGap() {
        return gap;
    }

    @Override
    public void arrangeChildren(Component<?> parent) {
        List<Component<?>> visibleChildren = parent.getChildren().stream()
                .filter(Component::isVisible)
                .filter(Component::isManagedByLayout)
                .collect(Collectors.toList());

        if (!visibleChildren.isEmpty()) {
            if (orientation == Orientation.HORIZONTAL) {
                applyHorizontalLayout(parent, visibleChildren);
            } else {
                applyVerticalLayout(parent, visibleChildren);
            }
        }

        // Arrange unmanaged children according to their own constraints
        List<Component<?>> unmanagedChildren = parent.getChildren().stream()
                .filter(Component::isVisible)
                .filter(c -> !c.isManagedByLayout())
                .toList();

        for (Component<?> child : unmanagedChildren) {
            float childX = child.getConstraints().getXConstraint().calculateX(child, parent.getInnerWidth(), child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right());
            float childY = child.getConstraints().getYConstraint().calculateY(child, parent.getInnerHeight(), child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom());
            child.arrange(parent.getInnerLeft() + childX, parent.getInnerTop() + childY);
        }
    }

    private void applyHorizontalLayout(Component<?> parent, List<Component<?>> visibleChildren) {
        float totalGrow = 0;
        float fixedWidth = 0;

        for (Component<?> child : visibleChildren) {
            Data data = getLayoutData(child);
            totalGrow += data.grow();
            if (data.grow() == 0) {
                fixedWidth += child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right();
            }
        }

        float totalGap = Math.max(0, visibleChildren.size() - 1) * gap;
        float availableWidth = parent.getInnerWidth();
        float remainingWidth = availableWidth - fixedWidth - totalGap;

        float offsetX = 0;
        float gapToUse = gap;
        int count = visibleChildren.size();

        if (totalGrow == 0 && count > 0 && remainingWidth > 0) {
            offsetX = switch (this.alignment) {
                case START, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY -> 0f;
                case CENTER -> remainingWidth / 2f;
                case END -> remainingWidth;
            };
            gapToUse = switch (this.alignment) {
                case SPACE_BETWEEN -> (count > 1) ? gap + remainingWidth / (count - 1) : gap;
                case SPACE_AROUND -> {
                    float space = remainingWidth / count;
                    offsetX = space / 2f;
                    yield gap + space;
                }
                case SPACE_EVENLY -> {
                    float space = remainingWidth / (count + 1);
                    offsetX = space;
                    yield gap + space;
                }
                default -> gap;
            };
        }
        float currentX = parent.getInnerLeft() + offsetX;

        for (Component<?> child : visibleChildren) {
            WidthConstraint originalWidthConstraint = child.getConstraints().getWidthConstraint();
            HeightConstraint originalHeightConstraint = child.getConstraints().getHeightConstraint();

            Data data = getLayoutData(child);
            if (data.grow() > 0 && totalGrow > 0) {
                float childShare = (data.grow() / totalGrow) * Math.max(0, remainingWidth);
                float newWidth = Math.max(0, childShare - (child.getMargin().left() + child.getMargin().right()));
                child.getConstraints().setWidth(Constraints.pixels(newWidth));
            }

            if (this.crossAxisAlignment == CrossAxisAlignment.STRETCH) {
                float newHeight = parent.getInnerHeight() - (child.getMargin().top() + child.getMargin().bottom());
                child.getConstraints().setHeight(Constraints.pixels(newHeight));
            }

            child.measure(parent.getInnerWidth(), parent.getInnerHeight());

            float childHeightWithMargin = child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom();
            float childY = parent.getInnerTop();
            switch (crossAxisAlignment) {
                case CENTER:
                    childY += (parent.getInnerHeight() - childHeightWithMargin) / 2f;
                    break;
                case END:
                    childY += parent.getInnerHeight() - childHeightWithMargin;
                    break;
            }

            child.arrange(currentX + child.getMargin().left(), childY + child.getMargin().top());
            child.getConstraints().setWidth(originalWidthConstraint);
            child.getConstraints().setHeight(originalHeightConstraint);

            float childWidthWithMargin = child.getFinalWidth();
            currentX += childWidthWithMargin + gapToUse;
        }
    }

    private void applyVerticalLayout(Component<?> parent, List<Component<?>> visibleChildren) {
        float totalGrow = 0;
        float fixedHeight = 0;
        for (Component<?> child : visibleChildren) {
            Data data = getLayoutData(child);
            totalGrow += data.grow();
            if (data.grow() == 0)
                fixedHeight += child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom();
        }
        float totalGap = Math.max(0, visibleChildren.size() - 1) * gap;
        float availableHeight = parent.getInnerHeight();
        float remainingHeight = availableHeight - fixedHeight - totalGap;

        float offsetY = 0;
        float gapToUse = gap;
        int count = visibleChildren.size();

        if (totalGrow == 0 && count > 0 && remainingHeight > 0) {
            offsetY = switch (this.alignment) {
                case START, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY -> 0f;
                case CENTER -> remainingHeight / 2f;
                case END -> remainingHeight;
            };
            gapToUse = switch (this.alignment) {
                case SPACE_BETWEEN -> (count > 1) ? gap + remainingHeight / (count - 1) : gap;
                case SPACE_AROUND -> {
                    float space = remainingHeight / count;
                    offsetY = space / 2f;
                    yield gap + space;
                }
                case SPACE_EVENLY -> {
                    float space = remainingHeight / (count + 1);
                    offsetY = space;
                    yield gap + space;
                }
                default -> gap;
            };
        }
        float currentY = parent.getInnerTop() + offsetY;

        for (Component<?> child : visibleChildren) {
            WidthConstraint originalWidthConstraint = child.getConstraints().getWidthConstraint();
            HeightConstraint originalHeightConstraint = child.getConstraints().getHeightConstraint();

            Data data = getLayoutData(child);
            if (data.grow() > 0 && totalGrow > 0) {
                float childShare = (data.grow() / totalGrow) * Math.max(0, remainingHeight);
                float newHeight = Math.max(0, childShare - (child.getMargin().top() + child.getMargin().bottom()));
                child.getConstraints().setHeight(Constraints.pixels(newHeight));
            }

            if (this.crossAxisAlignment == CrossAxisAlignment.STRETCH) {
                float newWidth = parent.getInnerWidth() - (child.getMargin().left() + child.getMargin().right());
                child.getConstraints().setWidth(Constraints.pixels(newWidth));
            }

            child.measure(parent.getInnerWidth(), parent.getInnerHeight());

            float childWidthWithMargin = child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right();
            float childX = parent.getInnerLeft();
            switch (crossAxisAlignment) {
                case CENTER:
                    childX += (parent.getInnerWidth() - childWidthWithMargin) / 2f;
                    break;
                case END:
                    childX += parent.getInnerWidth() - childWidthWithMargin;
                    break;
            }

            child.arrange(childX + child.getMargin().left(), currentY + child.getMargin().top());
            child.getConstraints().setWidth(originalWidthConstraint);
            child.getConstraints().setHeight(originalHeightConstraint);

            float childHeightWithMargin = child.getFinalHeight();
            currentY += childHeightWithMargin + gapToUse;
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

    public enum CrossAxisAlignment {
        START,
        CENTER,
        END,
        STRETCH
    }

    public record Data(float grow) {
        public Data {
            if (grow < 0) {
                WeaveClient.LOGGER.error("LinearLayout grow factor cannot be negative, but was {}.", grow);
                throw new IllegalArgumentException("Grow factor cannot be negative.");
            }
        }

        public static Data grow(float factor) {
            return new Data(factor);
        }
    }
}
