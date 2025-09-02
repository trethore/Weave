package tytoo.weave.layout;

import tytoo.weave.WeaveClient;
import tytoo.weave.component.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record GridLayout(int columns, float horizontalGap, float verticalGap) implements Layout {

    public GridLayout {
        if (columns <= 0) {
            WeaveClient.LOGGER.error("GridLayout column count must be positive, but was {}.", columns);
            throw new IllegalArgumentException("Number of columns must be positive.");
        }
    }

    public static GridLayout of(int columns) {
        return new GridLayout(columns, 0, 0);
    }

    public static GridLayout of(int columns, float gap) {
        return new GridLayout(columns, gap, gap);
    }

    public static GridLayout of(int columns, float horizontalGap, float verticalGap) {
        return new GridLayout(columns, horizontalGap, verticalGap);
    }

    @Override
    public void arrangeChildren(Component<?> parent) {
        List<Component<?>> managedChildren = parent.getChildren().stream()
                .filter(Component::isVisible)
                .filter(Component::isManagedByLayout)
                .toList();

        if (!managedChildren.isEmpty()) {
            Map<Component<?>, GridData> gridDataMap = getGridDataForChildren(managedChildren);
            PlacementResult placement = calculatePlacement(managedChildren, gridDataMap);
            applyArrangement(parent, managedChildren, placement, gridDataMap);
        }

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

    private Map<Component<?>, GridData> getGridDataForChildren(List<Component<?>> children) {
        Map<Component<?>, GridData> gridDataMap = new HashMap<>();
        for (Component<?> child : children) {
            Object layoutData = child.getLayoutData();
            gridDataMap.put(child, layoutData instanceof GridData data ? data : new GridData());
        }
        return gridDataMap;
    }

    private PlacementResult calculatePlacement(List<Component<?>> children, Map<Component<?>, GridData> gridDataMap) {
        Map<Component<?>, Point> childPositions = new HashMap<>();
        List<boolean[]> occupied = new ArrayList<>();
        int maxRow = 0;
        int cursorRow = 0;
        int cursorCol = 0;

        for (Component<?> child : children) {
            GridData data = gridDataMap.get(child);
            final int colSpan = Math.max(1, Math.min(columns, data.columnSpan()));
            final int rowSpan = Math.max(1, data.rowSpan());

            Point position = findNextAvailablePosition(occupied, cursorRow, cursorCol, colSpan, rowSpan);
            cursorRow = position.y;
            cursorCol = position.x;

            childPositions.put(child, new Point(cursorCol, cursorRow));
            occupyCells(occupied, cursorRow, cursorCol, rowSpan, colSpan);
            maxRow = Math.max(maxRow, cursorRow + rowSpan - 1);
        }

        return new PlacementResult(childPositions, maxRow + 1);
    }

    private Point findNextAvailablePosition(List<boolean[]> occupied, int startRow, int startCol, int colSpan, int rowSpan) {
        int r = startRow;
        int c = startCol;
        while (true) {
            if (c + colSpan > columns) {
                c = 0;
                r++;
                continue;
            }

            while (occupied.size() <= r + rowSpan - 1) {
                occupied.add(new boolean[columns]);
            }

            if (isAreaAvailable(occupied, r, c, rowSpan, colSpan)) {
                return new Point(c, r);
            } else {
                c++;
                if (c >= columns) {
                    c = 0;
                    r++;
                }
            }
        }
    }

    private boolean isAreaAvailable(List<boolean[]> occupied, int r, int c, int rowSpan, int colSpan) {
        for (int row = 0; row < rowSpan; row++) {
            for (int col = 0; col < colSpan; col++) {
                if (occupied.get(r + row)[c + col]) return false;
            }
        }
        return true;
    }

    private void occupyCells(List<boolean[]> occupied, int r, int c, int rowSpan, int colSpan) {
        for (int row = 0; row < rowSpan; row++) {
            for (int col = 0; col < colSpan; col++) {
                occupied.get(r + row)[c + col] = true;
            }
        }
    }

    private void applyArrangement(Component<?> parent, List<Component<?>> children, PlacementResult placement, Map<Component<?>, GridData> gridDataMap) {
        if (placement.totalRows() == 0) return;

        final int totalRows = placement.totalRows();
        final float cellWidth = (parent.getInnerWidth() - (columns - 1) * horizontalGap) / columns;
        final float cellHeight = (parent.getInnerHeight() - (totalRows - 1) * verticalGap) / totalRows;

        for (Component<?> child : children) {
            if (!child.isVisible()) continue;

            Point pos = placement.positions().get(child);
            GridData data = gridDataMap.get(child);
            final int colSpan = Math.max(1, data.columnSpan());
            final int rowSpan = Math.max(1, data.rowSpan());

            float availableWidthForChild = colSpan * cellWidth + (colSpan - 1) * horizontalGap;
            float availableHeightForChild = rowSpan * cellHeight + (rowSpan - 1) * verticalGap;

            child.measure(availableWidthForChild, availableHeightForChild);

            final float startX = parent.getInnerLeft() + pos.x * (cellWidth + horizontalGap);
            final float startY = parent.getInnerTop() + pos.y * (cellHeight + verticalGap);

            final float childX = startX + (availableWidthForChild - (child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right())) / 2f;
            final float childY = startY + (availableHeightForChild - (child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom())) / 2f;

            child.arrange(childX, childY);
        }
    }

    private record PlacementResult(Map<Component<?>, Point> positions, int totalRows) {
    }

    public record GridData(int columnSpan, int rowSpan) {
        public GridData() {
            this(1, 1);
        }

        public static GridData colSpan(int columnSpan) {
            return new GridData(columnSpan, 1);
        }

        public static GridData rowSpan(int rowSpan) {
            return new GridData(1, rowSpan);
        }

        public static GridData span(int colSpan, int rowSpan) {
            return new GridData(colSpan, rowSpan);
        }
    }
}
