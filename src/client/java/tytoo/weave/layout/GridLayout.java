package tytoo.weave.layout;

import tytoo.weave.component.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridLayout implements Layout {
    private final int columns;
    private final float horizontalGap;
    private final float verticalGap;

    private GridLayout(int columns, float horizontalGap, float verticalGap) {
        if (columns <= 0) {
            throw new IllegalArgumentException("Number of columns must be positive.");
        }
        this.columns = columns;
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
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
    public void apply(Component<?> component) {
        List<Component<?>> children = component.getChildren();
        if (children.isEmpty()) return;

        Map<Component<?>, GridData> gridDataMap = new HashMap<>();
        for (Component<?> child : children) {
            Object layoutData = child.getLayoutData();
            if (layoutData instanceof GridData) {
                gridDataMap.put(child, (GridData) layoutData);
            } else {
                gridDataMap.put(child, new GridData());
            }
        }

        Map<Component<?>, Point> childPositions = new HashMap<>();
        List<boolean[]> occupied = new ArrayList<>();
        int maxRow = 0;

        int cursorRow = 0;
        int cursorCol = 0;

        for (Component<?> child : children) {
            GridData data = gridDataMap.get(child);
            int colSpan = Math.max(1, Math.min(columns, data.getColumnSpan()));
            int rowSpan = Math.max(1, data.getRowSpan());

            while (true) {
                if (cursorCol + colSpan > columns) {
                    cursorCol = 0;
                    cursorRow++;
                    continue;
                }

                while (occupied.size() <= cursorRow + rowSpan - 1) {
                    occupied.add(new boolean[columns]);
                }

                boolean fits = true;
                for (int r = 0; r < rowSpan; r++) {
                    for (int c = 0; c < colSpan; c++) {
                        if (occupied.get(cursorRow + r)[cursorCol + c]) {
                            fits = false;
                            break;
                        }
                    }
                    if (!fits) break;
                }

                if (fits) {
                    break;
                } else {
                    cursorCol++;
                    if (cursorCol >= columns) {
                        cursorCol = 0;
                        cursorRow++;
                    }
                }
            }

            childPositions.put(child, new Point(cursorCol, cursorRow));

            for (int r = 0; r < rowSpan; r++) {
                for (int c = 0; c < colSpan; c++) {
                    occupied.get(cursorRow + r)[cursorCol + c] = true;
                }
            }
            maxRow = Math.max(maxRow, cursorRow + rowSpan - 1);
        }

        final int totalRows = maxRow + 1;

        for (Component<?> child : children) {
            Point pos = childPositions.get(child);
            GridData data = gridDataMap.get(child);
            final int col = pos.x;
            final int row = pos.y;
            final int colSpan = Math.max(1, Math.min(columns, data.getColumnSpan()));
            final int rowSpan = Math.max(1, data.getRowSpan());

            child.setWidth(c -> {
                Component<?> p = c.getParent();
                if (p == null) return 0f;
                float cellWidth = (p.getInnerWidth() - (columns - 1) * horizontalGap) / columns;
                return colSpan * cellWidth + (colSpan - 1) * horizontalGap;
            });

            child.setHeight(c -> {
                Component<?> p = c.getParent();
                if (p == null) return 0f;
                float cellHeight = (p.getInnerHeight() - (totalRows - 1) * verticalGap) / totalRows;
                return rowSpan * cellHeight + (rowSpan - 1) * verticalGap;
            });

            child.setX(c -> {
                Component<?> p = c.getParent();
                if (p == null) return 0f;
                float cellWidth = (p.getInnerWidth() - (columns - 1) * horizontalGap) / columns;
                return p.getInnerLeft() + col * (cellWidth + horizontalGap);
            });

            child.setY(c -> {
                Component<?> p = c.getParent();
                if (p == null) return 0f;
                float cellHeight = (p.getInnerHeight() - (totalRows - 1) * verticalGap) / totalRows;
                return p.getInnerTop() + row * (cellHeight + verticalGap);
            });
        }
    }


    public static class GridData {
        private int columnSpan = 1;
        private int rowSpan = 1;

        private GridData() {
        }

        public static GridData colSpan(int columnSpan) {
            GridData data = new GridData();
            data.columnSpan = columnSpan;
            return data;
        }

        public static GridData rowSpan(int rowSpan) {
            GridData data = new GridData();
            data.rowSpan = rowSpan;
            return data;
        }

        public static GridData span(int colSpan, int rowSpan) {
            GridData data = new GridData();
            data.columnSpan = colSpan;
            data.rowSpan = rowSpan;
            return data;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }
    }
}