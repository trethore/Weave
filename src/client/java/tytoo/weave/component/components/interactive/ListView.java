package tytoo.weave.component.components.interactive;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.state.ObservableList;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleState;
import tytoo.weave.utils.InputHelper;

import java.util.*;
import java.util.function.Function;

public class ListView<T> extends BasePanel<ListView<T>> {
    private final ScrollPanel scrollPanel;
    private final Panel contentPanel;
    private final Map<Integer, ItemHolder> active = new HashMap<>();
    private final Deque<ItemHolder> pool = new ArrayDeque<>();
    private final List<Runnable> stateUnbind = new ArrayList<>();
    private final Set<Integer> selectedIndices = new LinkedHashSet<>();
    @Nullable
    private ObservableList<T> observableItems = null;
    @Nullable
    private State<List<T>> itemsState = null;
    private Function<T, Component<?>> itemFactory = item -> SimpleTextComponent.of(String.valueOf(item));
    private HeightMode heightMode = HeightMode.MEASURE_ONCE;
    private float fixedItemHeight = 20f;
    private float measuredItemHeight = -1f;
    private float gap = 2f;
    private final ObservableList.ChangeListener<T> changeListener = c -> invalidateAll();
    private SelectionMode selectionMode = SelectionMode.SINGLE;
    private int focusedIndex = -1;
    private int anchorIndex = -1;
    @Nullable
    private java.util.function.Consumer<Set<Integer>> selectionListener = null;
    private int lastFirst = -1;
    private int lastLast = -1;
    private float lastScrollY = Float.NaN;
    private float lastViewportH = Float.NaN;
    private long lastArrowKeyTimeNs = 0L;
    private int arrowKeyStreak = 0;

    public ListView() {
        this.scrollPanel = new ScrollPanel();
        this.scrollPanel.setVerticalScrollbar(true);
        this.scrollPanel.setWidth(Constraints.relative(1.0f));
        this.scrollPanel.setHeight(Constraints.relative(1.0f));

        this.contentPanel = Panel.create();
        this.contentPanel.setWidth(Constraints.relative(1.0f));
        this.scrollPanel.addChild(this.contentPanel);

        this.addChild(this.scrollPanel);

        this.setFocusable(true);
        this.onEvent(KeyPressEvent.TYPE, this::handleKey);
    }

    public static <T> ListView<T> create() {
        return new ListView<>();
    }

    public ListView<T> setItems(ObservableList<T> items) {
        clearItemsBinding();
        this.observableItems = items;
        this.itemsState = null;
        items.addListener(changeListener);
        invalidateAll();
        return this;
    }

    public ListView<T> bindItems(State<List<T>> itemsState) {
        clearItemsBinding();
        this.itemsState = itemsState;
        java.util.function.Consumer<List<T>> listener = v -> invalidateAll();
        itemsState.addListener(listener);
        stateUnbind.add(() -> itemsState.removeListener(listener));
        invalidateAll();
        return this;
    }

    public ListView<T> setItemFactory(Function<T, Component<?>> factory) {
        this.itemFactory = Objects.requireNonNull(factory);
        invalidateAll();
        return this;
    }

    public ListView<T> setHeightMode(HeightMode mode) {
        this.heightMode = mode;
        if (mode == HeightMode.MEASURE_ONCE) measuredItemHeight = -1f;
        invalidateAll();
        return this;
    }

    public ListView<T> setFixedItemHeight(float height) {
        this.fixedItemHeight = height;
        if (this.heightMode == HeightMode.FIXED) invalidateAll();
        return this;
    }

    public ListView<T> setGap(float gap) {
        this.gap = gap;
        invalidateAll();
        return this;
    }

    public ListView<T> setSelectionMode(SelectionMode mode) {
        this.selectionMode = mode;
        if (mode == SelectionMode.SINGLE && selectedIndices.size() > 1) {
            int keep = selectedIndices.stream().findFirst().orElse(-1);
            selectedIndices.clear();
            if (keep >= 0) selectedIndices.add(keep);
            notifySelectionChanged();
        }
        // keep current multi-selection as-is
        refreshSelectionStates();
        return this;
    }

    public ListView<T> onSelectionChanged(java.util.function.Consumer<Set<Integer>> listener) {
        this.selectionListener = listener;
        return this;
    }

    private void clearItemsBinding() {
        if (observableItems != null) observableItems.removeListener(changeListener);
        observableItems = null;
        for (Runnable r : new ArrayList<>(stateUnbind)) r.run();
        stateUnbind.clear();
        itemsState = null;
    }

    private List<T> snapshotItems() {
        if (observableItems != null) return List.copyOf(observableItems);
        if (itemsState != null) {
            List<T> v = itemsState.get();
            return v == null ? List.of() : List.copyOf(v);
        }
        return List.of();
    }

    private void invalidateAll() {
        clearActive();
        measuredItemHeight = (heightMode == HeightMode.MEASURE_ONCE) ? -1f : measuredItemHeight;
        updateTotalContentHeight();
        invalidateLayout();
    }

    private void updateTotalContentHeight() {
        int count = snapshotItems().size();
        float itemH = getItemHeightForLayout();
        float g = (count > 0) ? (count - 1) * gap : 0f;
        float totalContentHeight = count * itemH + g;
        contentPanel.setHeight(Constraints.pixels(totalContentHeight));
    }

    private float getItemHeightForLayout() {
        if (heightMode == HeightMode.FIXED) return fixedItemHeight;
        if (measuredItemHeight > 0) return measuredItemHeight;
        List<T> items = snapshotItems();
        if (items.isEmpty()) return fixedItemHeight;
        Component<?> sample = itemFactory.apply(items.getFirst());
        sample.setWidth(Constraints.relative(1.0f));
        sample.measure(contentPanel.getInnerWidth(), contentPanel.getInnerHeight());
        measuredItemHeight = sample.getMeasuredHeight();
        if (measuredItemHeight <= 0) measuredItemHeight = fixedItemHeight;
        return measuredItemHeight;
    }

    @Override
    public void draw(DrawContext context) {
        ensureVirtualization();
        super.draw(context);
    }

    private void ensureVirtualization() {
        float viewportH = this.getInnerHeight();
        float scrollY = scrollPanel.getScrollY();
        int count = snapshotItems().size();
        float itemH = getItemHeightForLayout();
        updateTotalContentHeight();
        if (count == 0) {
            clearActive();
            lastFirst = lastLast = -1;
            lastScrollY = scrollY;
            lastViewportH = viewportH;
            return;
        }

        float viewportStart = -scrollY;
        float viewportEnd = viewportStart + viewportH;
        float stride = itemH + gap;
        int first = (int) Math.floor(viewportStart / Math.max(1f, stride));
        first = Math.max(0, Math.min(count - 1, first));
        int last = (int) Math.floor((viewportEnd) / Math.max(1f, stride));
        last = Math.max(0, Math.min(count - 1, last));

        first = Math.max(0, first - 1);
        last = Math.min(count - 1, last + 1);

        boolean needUpdate = first != lastFirst || last != lastLast || scrollY != lastScrollY || viewportH != lastViewportH;
        if (!needUpdate) return;

        Set<Integer> needed = new LinkedHashSet<>();
        for (int i = first; i <= last; i++) needed.add(i);

        List<Integer> toRemove = new ArrayList<>();
        for (int idx : active.keySet()) if (!needed.contains(idx)) toRemove.add(idx);
        for (int idx : toRemove) release(idx);

        List<T> items = snapshotItems();
        for (int i = first; i <= last; i++) {
            if (!active.containsKey(i)) acquire(i, items.get(i), itemH);
            position(i, itemH);
        }

        lastFirst = first;
        lastLast = last;
        lastScrollY = scrollY;
        lastViewportH = viewportH;
    }

    private void position(int index, float itemH) {
        ItemHolder holder = active.get(index);
        if (holder == null) return;
        float stride = itemH + gap;
        float y = index * stride;
        holder.container.setY(Constraints.pixels(y));
        holder.container.setHeight(Constraints.pixels(itemH));
        holder.container.invalidateLayout();
    }

    private void acquire(int index, T item, float itemH) {
        ItemHolder holder = pool.pollFirst();
        if (holder == null) {
            holder = new ItemHolder();
            holder.container = Panel.create().setManagedByLayout(true);
            holder.container.setWidth(Constraints.relative(1.0f));
            holder.container.setY(Constraints.pixels(0));
            holder.container.setHeight(Constraints.pixels(itemH));
            holder.container.setFocusable(false);
            contentPanel.addChild(holder.container);

            final ItemHolder bound = holder;
            holder.container.onMouseRelease(e -> {
                if (e.getButton() == 0) handleClick(bound.index);
            });
        }

        holder.index = index;
        holder.container.removeAllChildren();
        Component<?> content = itemFactory.apply(item);
        content.setWidth(Constraints.relative(1.0f));
        holder.container.addChild(content);
        holder.content = content;
        active.put(index, holder);
        updateItemSelectionVisual(holder);
    }

    private void release(int index) {
        ItemHolder holder = active.remove(index);
        if (holder == null) return;
        holder.container.removeAllChildren();
        pool.addLast(holder);
    }

    private void clearActive() {
        for (int idx : new ArrayList<>(active.keySet())) release(idx);
        active.clear();
    }

    private void handleClick(int index) {
        boolean shift = InputHelper.isShiftDown();
        boolean ctrl = InputHelper.isControlDown();

        if (selectionMode == SelectionMode.SINGLE) {
            selectedIndices.clear();
            selectedIndices.add(index);
            focusedIndex = index;
            anchorIndex = index;
        } else {
            if (shift && anchorIndex >= 0) {
                selectedIndices.clear();
                int a = Math.min(anchorIndex, index);
                int b = Math.max(anchorIndex, index);
                for (int i = a; i <= b; i++) selectedIndices.add(i);
                focusedIndex = index;
            } else if (ctrl) {
                if (selectedIndices.contains(index)) selectedIndices.remove(index);
                else selectedIndices.add(index);
                focusedIndex = index;
                anchorIndex = index;
            } else {
                selectedIndices.clear();
                selectedIndices.add(index);
                focusedIndex = index;
                anchorIndex = index;
            }
        }
        refreshSelectionStates();
        notifySelectionChanged();
    }

    private void handleKey(KeyPressEvent event) {
        switch (event.getKeyCode()) {
            case GLFW.GLFW_KEY_UP -> {
                int steps = computeArrowSteps();
                moveFocus(-steps);
            }
            case GLFW.GLFW_KEY_DOWN -> {
                int steps = computeArrowSteps();
                moveFocus(steps);
            }
            case GLFW.GLFW_KEY_HOME -> setFocusToEdge(true);
            case GLFW.GLFW_KEY_END -> setFocusToEdge(false);
            case GLFW.GLFW_KEY_PAGE_UP -> pageMove(-1);
            case GLFW.GLFW_KEY_PAGE_DOWN -> pageMove(1);
            default -> {
                return;
            }
        }
        event.cancel();
    }

    private void moveFocus(int delta) {
        int count = snapshotItems().size();
        if (count == 0) return;
        if (focusedIndex < 0) {
            int visibleStart = getFirstVisibleIndex();
            focusedIndex = visibleStart >= 0 ? visibleStart : 0;
        }
        focusedIndex = Math.max(0, Math.min(count - 1, focusedIndex + delta));
        if (selectionMode == SelectionMode.SINGLE || !InputHelper.isControlDown()) {
            if (!InputHelper.isShiftDown()) {
                selectedIndices.clear();
                anchorIndex = focusedIndex;
            }
            if (InputHelper.isShiftDown() && anchorIndex >= 0) {
                selectedIndices.clear();
                int a = Math.min(anchorIndex, focusedIndex);
                int b = Math.max(anchorIndex, focusedIndex);
                for (int i = a; i <= b; i++) selectedIndices.add(i);
            } else {
                selectedIndices.add(focusedIndex);
            }
        }
        ensureIndexVisible(focusedIndex);
        refreshSelectionStates();
        notifySelectionChanged();
    }

    private void setFocusToEdge(boolean start) {
        int count = snapshotItems().size();
        if (count == 0) return;
        focusedIndex = start ? 0 : count - 1;
        anchorIndex = focusedIndex;
        selectedIndices.clear();
        selectedIndices.add(focusedIndex);
        ensureIndexVisible(focusedIndex);
        refreshSelectionStates();
        notifySelectionChanged();
    }

    private void pageMove(int direction) {
        float viewportH = this.getInnerHeight();
        float itemH = getItemHeightForLayout();
        int delta = Math.max(1, (int) Math.floor(viewportH / Math.max(1f, itemH + gap)) - 1);
        moveFocus(direction * delta);
    }

    private void ensureIndexVisible(int index) {
        float itemH = getItemHeightForLayout();
        float stride = itemH + gap;
        float itemTop = index * stride;
        float itemBottom = itemTop + itemH;
        float viewportStart = -scrollPanel.getScrollY();
        float viewportEnd = viewportStart + this.getInnerHeight();

        if (itemTop < viewportStart) {
            scrollPanel.setScrollY(-itemTop);
        } else if (itemBottom > viewportEnd) {
            float newStart = itemBottom - this.getInnerHeight();
            scrollPanel.setScrollY(-newStart);
        }
    }

    private int getFirstVisibleIndex() {
        List<T> items = snapshotItems();
        if (items.isEmpty()) return -1;
        float itemH = getItemHeightForLayout();
        float stride = itemH + gap;
        float viewportStart = -scrollPanel.getScrollY();
        int first = (int) Math.floor(viewportStart / Math.max(1f, stride));
        return Math.max(0, Math.min(items.size() - 1, first));
    }

    private int computeArrowSteps() {
        long now = System.nanoTime();
        long deltaNs = now - lastArrowKeyTimeNs;
        // If the last arrow key was pressed recently, ramp up speed
        if (deltaNs <= 300_000_000L) { // 300ms window
            if (arrowKeyStreak < 30) arrowKeyStreak++;
        } else {
            arrowKeyStreak = 0;
        }
        lastArrowKeyTimeNs = now;

        int tier = arrowKeyStreak / 3; // every 3 quick presses increases tier
        if (tier <= 0) return 1;
        int steps = 1 << Math.min(tier, 6); // cap at 2^6 = 64
        return Math.max(1, steps);
    }

    private void refreshSelectionStates() {
        for (Map.Entry<Integer, ItemHolder> e : active.entrySet()) updateItemSelectionVisual(e.getValue());
    }

    private void updateItemSelectionVisual(ItemHolder holder) {
        boolean selected = selectedIndices.contains(holder.index);
        holder.container.setStyleState(StyleState.SELECTED, selected);
    }

    private void notifySelectionChanged() {
        if (selectionListener != null) selectionListener.accept(Set.copyOf(selectedIndices));
    }

    public enum HeightMode {FIXED, MEASURE_ONCE}

    public enum SelectionMode {SINGLE, MULTIPLE}

    private static final class ItemHolder {
        int index;
        Panel container;
        @Nullable Component<?> content;
    }
}
