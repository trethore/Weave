package tytoo.weave.layout;

import tytoo.weave.component.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    private static Data getLayoutData(Component<?> component) {
        Object layoutData = component.getLayoutData();
        if (layoutData instanceof Data) {
            return (Data) layoutData;
        }
        return new Data(0);
    }

    @Override
    public void arrangeChildren(Component<?> parent) {
        List<Component<?>> visibleChildren = parent.getChildren().stream()
                .filter(Component::isVisible)
                .collect(Collectors.toList());

        if (visibleChildren.isEmpty()) return;

        if (orientation == Orientation.HORIZONTAL) {
            applyHorizontalLayout(parent, visibleChildren);
        } else {
            applyVerticalLayout(parent, visibleChildren);
        }
    }

    private void applyHorizontalLayout(Component<?> parent, List<Component<?>> visibleChildren) {
        float totalGrow = 0;
        float fixedWidth = 0;

        for (Component<?> child : visibleChildren) {
            Data data = getLayoutData(child);
            totalGrow += data.grow;
            if (data.grow == 0) {
                fixedWidth += child.getMeasuredWidth() + child.getMargin().left + child.getMargin().right;
            }
        }

        float totalGap = Math.max(0, visibleChildren.size() - 1) * gap;
        float availableWidth = parent.getInnerWidth();
        float remainingWidth = availableWidth - fixedWidth - totalGap;

        float currentX = parent.getInnerLeft();

        if (totalGrow == 0 && alignment != Alignment.START) {
            currentX += switch (alignment) {
                case CENTER -> remainingWidth / 2f;
                case END -> remainingWidth;
                case SPACE_EVENLY -> remainingWidth / (visibleChildren.size() + 1);
                case SPACE_AROUND -> remainingWidth / (visibleChildren.size() * 2f);
                default -> 0;
            };
        }

        for (Component<?> child : visibleChildren) {
            Data data = getLayoutData(child);
            if (data.grow > 0 && totalGrow > 0) {
                float childShare = (data.grow / totalGrow) * remainingWidth;
                child.measure(childShare - (child.getMargin().left + child.getMargin().right), parent.getInnerHeight());
            }

            float childY = parent.getInnerTop() + (parent.getInnerHeight() - child.getFinalHeight()) / 2f;
            child.arrange(currentX + child.getMargin().left, childY + child.getMargin().top);
            float childWidthWithMargin = child.getFinalWidth();
            currentX += childWidthWithMargin + gap;
        }
    }

    private void applyVerticalLayout(Component<?> parent, List<Component<?>> visibleChildren) {
        float totalGrow = 0;
        float fixedHeight = 0;

        for (Component<?> child : visibleChildren) {
            Data data = getLayoutData(child);
            totalGrow += data.grow;
            if (data.grow == 0) {
                fixedHeight += child.getMeasuredHeight() + child.getMargin().top + child.getMargin().bottom;
            }
        }

        float totalGap = Math.max(0, visibleChildren.size() - 1) * gap;
        float availableHeight = parent.getInnerHeight();
        float remainingHeight = availableHeight - fixedHeight - totalGap;

        float currentY = parent.getInnerTop();

        if (totalGrow == 0 && alignment != Alignment.START) {
            currentY += switch (alignment) {
                case CENTER -> remainingHeight / 2f;
                case END -> remainingHeight;
                case SPACE_EVENLY -> remainingHeight / (visibleChildren.size() + 1);
                case SPACE_AROUND -> remainingHeight / (visibleChildren.size() * 2f);
                default -> 0;
            };
        }

        for (Component<?> child : visibleChildren) {
            Data data = getLayoutData(child);
            if (data.grow > 0 && totalGrow > 0) {
                float childShare = (data.grow / totalGrow) * remainingHeight;
                child.measure(parent.getInnerWidth(), childShare - (child.getMargin().top + child.getMargin().bottom));
            }

            float childX = parent.getInnerLeft() + (parent.getInnerWidth() - child.getFinalWidth()) / 2f;
            child.arrange(childX + child.getMargin().left, currentY + child.getMargin().top);
            float childHeightWithMargin = child.getFinalHeight();
            currentY += childHeightWithMargin + gap;
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

    public static class Data {
        public float grow;

        public Data(float grow) {
            this.grow = Math.max(0, grow);
        }

        public static Data grow(float factor) {
            return new Data(factor);
        }
    }
}