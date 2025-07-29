package tytoo.weave.layout;

import tytoo.weave.component.Component;

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
    public void arrangeChildren(Component<?> parent) {
        List<Component<?>> children = parent.getChildren();
        if (children.isEmpty()) return;

        if (orientation == Orientation.HORIZONTAL) {
            applyHorizontalLayout(parent, children);
        } else {
            applyVerticalLayout(parent, children);
        }
    }

    private void applyHorizontalLayout(Component<?> parent, List<Component<?>> children) {
        long visibleChildCount = children.stream().filter(Component::isVisible).count();
        if (visibleChildCount == 0) return;

        float totalChildrenWidth = 0;
        for (var child : children) {
            if (!child.isVisible()) continue;
            totalChildrenWidth += child.getMeasuredWidth() + child.getMargin().left + child.getMargin().right;
        }
        totalChildrenWidth += Math.max(0, visibleChildCount - 1) * gap;

        float currentX;
        float freeSpace = parent.getInnerWidth() - totalChildrenWidth;

        currentX = parent.getInnerLeft() + switch (alignment) {
            case CENTER -> freeSpace / 2f;
            case END -> freeSpace;
            default -> 0;
        };

        if (alignment == Alignment.SPACE_EVENLY) currentX += freeSpace / (visibleChildCount + 1);
        if (alignment == Alignment.SPACE_AROUND) currentX += freeSpace / (visibleChildCount * 2f);

        for (Component<?> child : children) {
            if (!child.isVisible()) continue;

            float childY = parent.getInnerTop() + (parent.getInnerHeight() - (child.getMeasuredHeight() + child.getMargin().top + child.getMargin().bottom)) / 2f;
            child.arrange(currentX + child.getMargin().left, childY + child.getMargin().top);

            float childWidthWithMargin = child.getMeasuredWidth() + child.getMargin().left + child.getMargin().right;
            currentX += childWidthWithMargin + gap;

            if (alignment == Alignment.SPACE_BETWEEN && visibleChildCount > 1)
                currentX += freeSpace / (visibleChildCount - 1);
            if (alignment == Alignment.SPACE_AROUND) currentX += freeSpace / visibleChildCount;
            if (alignment == Alignment.SPACE_EVENLY) currentX += freeSpace / (visibleChildCount + 1);
        }
    }

    private void applyVerticalLayout(Component<?> parent, List<Component<?>> children) {
        long visibleChildCount = children.stream().filter(Component::isVisible).count();
        if (visibleChildCount == 0) return;

        float totalChildrenHeight = 0;
        for (var child : children) {
            if (!child.isVisible()) continue;
            totalChildrenHeight += child.getMeasuredHeight() + child.getMargin().top + child.getMargin().bottom;
        }
        totalChildrenHeight += Math.max(0, visibleChildCount - 1) * gap;

        float currentY;
        float freeSpace = parent.getInnerHeight() - totalChildrenHeight;

        currentY = parent.getInnerTop() + switch (alignment) {
            case CENTER -> freeSpace / 2f;
            case END -> freeSpace;
            default -> 0;
        };

        if (alignment == Alignment.SPACE_EVENLY) currentY += freeSpace / (visibleChildCount + 1);
        if (alignment == Alignment.SPACE_AROUND) currentY += freeSpace / (visibleChildCount * 2f);

        for (Component<?> child : children) {
            if (!child.isVisible()) continue;

            float childX = parent.getInnerLeft() + (parent.getInnerWidth() - (child.getMeasuredWidth() + child.getMargin().left + child.getMargin().right)) / 2f;
            child.arrange(childX + child.getMargin().left, currentY + child.getMargin().top);

            float childHeightWithMargin = child.getMeasuredHeight() + child.getMargin().top + child.getMargin().bottom;
            currentY += childHeightWithMargin + gap;

            if (alignment == Alignment.SPACE_BETWEEN && visibleChildCount > 1)
                currentY += freeSpace / (visibleChildCount - 1);
            if (alignment == Alignment.SPACE_AROUND) currentY += freeSpace / visibleChildCount;
            if (alignment == Alignment.SPACE_EVENLY) currentY += freeSpace / (visibleChildCount + 1);
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