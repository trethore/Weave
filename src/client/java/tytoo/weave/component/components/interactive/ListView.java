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
import java.util.function.Consumer;
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
    private float estimatedItemHeight = 20f;
    private float gap = 2f;
    private SelectionMode selectionMode = SelectionMode.SINGLE;
    private int focusedIndex = -1;
    private int anchorIndex = -1;
    @Nullable
    private Consumer<Set<Integer>> selectionListener = null;
    private int lastFirst = -1;
    private int lastLast = -1;
    private float lastScrollY = Float.NaN;
    private float lastViewportH = Float.NaN;
    private long lastArrowKeyTimeNs = 0L;
    private int arrowKeyStreak = 0;
    // VARIABLE height mode state
    private float[] heightCache = new float[0];
    private boolean[] measuredFlags = new boolean[0];
    private FenwickTree fenwick = new FenwickTree(0);
    private final ObservableList.ChangeListener<T> changeListener = c -> invalidateAll();

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
        Consumer<List<T>> listener = v -> invalidateAll();
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
        if (mode == HeightMode.VARIABLE) {
            measuredItemHeight = -1f;
        }
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
        if (this.heightMode == HeightMode.VARIABLE) rebuildFenwickForVariable();
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
        refreshSelectionStates();
        return this;
    }

    public ListView<T> onSelectionChanged(Consumer<Set<Integer>> listener) {
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

    public ListView<T> setEstimatedItemHeight(float height) {
        this.estimatedItemHeight = height;
        if (this.heightMode == HeightMode.VARIABLE) invalidateAll();
        return this;
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
        if (heightMode == HeightMode.VARIABLE) initVariableCaches();
        updateTotalContentHeight();
        invalidateLayout();
    }

    private void updateTotalContentHeight() {
        int count = snapshotItems().size();
        if (heightMode == HeightMode.VARIABLE) {
            ensureFenwickSize(count);
            float total = (count > 0) ? fenwick.sum(count) - gap : 0f;
            contentPanel.setHeight(Constraints.pixels(total));
            return;
        }
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
        updateTotalContentHeight();
        if (count == 0) {
            clearActive();
            lastFirst = lastLast = -1;
            lastScrollY = scrollY;
            lastViewportH = viewportH;
            return;
        }

        if (heightMode == HeightMode.VARIABLE) {
            ensureFenwickSize(count);
            float viewportStart = -scrollY;
            float viewportEnd = viewportStart + viewportH;

            int first = Math.max(0, fenwick.findPrefixIndex(viewportStart));
            int last = Math.max(0, fenwick.findPrefixIndex(viewportEnd));

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
                if (!active.containsKey(i)) acquireVariable(i, items.get(i));
                positionVariable(i);
                measureAndUpdate(i);
            }

            lastFirst = first;
            lastLast = last;
            lastScrollY = scrollY;
            lastViewportH = viewportH;
            return;
        }

        float itemH = getItemHeightForLayout();
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
            if (!active.containsKey(i)) acquireFixed(i, items.get(i), itemH);
            positionFixed(i, itemH);
        }

        lastFirst = first;
        lastLast = last;
        lastScrollY = scrollY;
        lastViewportH = viewportH;
    }

    private void positionFixed(int index, float itemH) {
        ItemHolder holder = active.get(index);
        if (holder == null) return;
        float stride = itemH + gap;
        float y = index * stride;
        holder.container.setY(Constraints.pixels(y));
        holder.container.setHeight(Constraints.pixels(itemH));
        holder.container.invalidateLayout();
    }

    private void positionVariable(int index) {
        ItemHolder holder = active.get(index);
        if (holder == null) return;
        float top = fenwick.sum(index) - (heightAt(index) + gap);
        if (top < 0) top = 0f;
        float h = heightAt(index);
        holder.container.setY(Constraints.pixels(top));
        holder.container.setHeight(Constraints.pixels(h));
        holder.container.invalidateLayout();
    }

    private void acquireFixed(int index, T item, float itemH) {
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

    private void acquireVariable(int index, T item) {
        ItemHolder holder = pool.pollFirst();
        float initialH = heightAt(index);
        if (holder == null) {
            holder = new ItemHolder();
            holder.container = Panel.create().setManagedByLayout(true);
            holder.container.setWidth(Constraints.relative(1.0f));
            holder.container.setY(Constraints.pixels(0));
            holder.container.setHeight(Constraints.pixels(initialH));
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
        holder.container.setHeight(Constraints.pixels(initialH));
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
            focusedIndex = Math.max(visibleStart, 0);
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
        if (heightMode == HeightMode.VARIABLE) {
            float viewportStart = -scrollPanel.getScrollY();
            int start = getFirstVisibleIndex();
            int end = Math.max(0, fenwick.findPrefixIndex(viewportStart + viewportH));
            int delta = Math.max(1, Math.abs(end - start));
            moveFocus(direction * delta);
            return;
        }
        float itemH = getItemHeightForLayout();
        int delta = Math.max(1, (int) Math.floor(viewportH / Math.max(1f, itemH + gap)) - 1);
        moveFocus(direction * delta);
    }

    private void ensureIndexVisible(int index) {
        float viewportStart = -scrollPanel.getScrollY();
        float viewportEnd = viewportStart + this.getInnerHeight();
        if (heightMode == HeightMode.VARIABLE) {
            float top = fenwick.sum(index) - (heightAt(index) + gap);
            float bottom = top + heightAt(index);
            if (top < viewportStart) {
                scrollPanel.setScrollY(-top);
            } else if (bottom > viewportEnd) {
                float newStart = bottom - this.getInnerHeight();
                scrollPanel.setScrollY(-newStart);
            }
            return;
        }
        float itemH = getItemHeightForLayout();
        float stride = itemH + gap;
        float itemTop = index * stride;
        float itemBottom = itemTop + itemH;
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
        float viewportStart = -scrollPanel.getScrollY();
        if (heightMode == HeightMode.VARIABLE) {
            ensureFenwickSize(items.size());
            int idx = Math.max(0, fenwick.findPrefixIndex(viewportStart));
            if (idx >= items.size()) idx = items.size() - 1;
            return idx;
        }
        float itemH = getItemHeightForLayout();
        float stride = itemH + gap;
        int first = (int) Math.floor(viewportStart / Math.max(1f, stride));
        return Math.max(0, Math.min(items.size() - 1, first));
    }

    private int computeArrowSteps() {
        long now = System.nanoTime();
        long deltaNs = now - lastArrowKeyTimeNs;
        if (deltaNs <= 300_000_000L) {
            if (arrowKeyStreak < 30) arrowKeyStreak++;
        } else {
            arrowKeyStreak = 0;
        }
        lastArrowKeyTimeNs = now;

        int tier = arrowKeyStreak / 3;
        if (tier <= 0) return 1;
        int steps = 1 << Math.min(tier, 6);
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

    private void initVariableCaches() {
        int n = snapshotItems().size();
        heightCache = new float[n];
        measuredFlags = new boolean[n];
        Arrays.fill(heightCache, Math.max(1f, estimatedItemHeight));
        Arrays.fill(measuredFlags, false);
        fenwick = new FenwickTree(n);
        float v = Math.max(1f, estimatedItemHeight) + gap;
        for (int i = 0; i < n; i++) fenwick.add(i + 1, v);
    }

    private void ensureFenwickSize(int n) {
        if (fenwick.size() == n && heightCache.length == n) return;
        heightCache = Arrays.copyOf(heightCache, n);
        measuredFlags = Arrays.copyOf(measuredFlags, n);
        for (int i = 0; i < n; i++) if (heightCache[i] <= 0) heightCache[i] = Math.max(1f, estimatedItemHeight);
        FenwickTree newFenwick = new FenwickTree(n);
        for (int i = 0; i < n; i++) newFenwick.add(i + 1, heightAt(i) + gap);
        fenwick = newFenwick;
    }

    private void rebuildFenwickForVariable() {
        if (heightMode != HeightMode.VARIABLE) return;
        int n = snapshotItems().size();
        FenwickTree newFenwick = new FenwickTree(n);
        for (int i = 0; i < n; i++) newFenwick.add(i + 1, heightAt(i) + gap);
        fenwick = newFenwick;
        updateTotalContentHeight();
    }

    private float heightAt(int index) {
        if (index < 0 || index >= heightCache.length) return Math.max(1f, estimatedItemHeight);
        float h = heightCache[index];
        return h > 0 ? h : Math.max(1f, estimatedItemHeight);
    }

    private void measureAndUpdate(int index) {
        ItemHolder holder = active.get(index);
        if (holder == null) return;
        if (holder.content == null) return;
        float availW = contentPanel.getInnerWidth();
        float availH = contentPanel.getInnerHeight();
        holder.content.measure(availW, availH);
        float measured = holder.content.getMeasuredHeight();
        if (measured <= 0) measured = Math.max(1f, estimatedItemHeight);
        if (!measuredFlags[index] || Math.abs(measured - heightAt(index)) > 0.5f) {
            float old = heightAt(index);
            heightCache[index] = measured;
            measuredFlags[index] = true;
            float delta = (measured + gap) - (old + gap);
            fenwick.add(index + 1, delta);
            holder.container.setHeight(Constraints.pixels(measured));
            if (index < lastFirst) {
                scrollPanel.setScrollY(scrollPanel.getScrollY() - (measured - old));
            }
            updateTotalContentHeight();
        }
    }

    public enum HeightMode {FIXED, MEASURE_ONCE, VARIABLE}

    public enum SelectionMode {SINGLE, MULTIPLE}

    private static final class ItemHolder {
        int index;
        Panel container;
        @Nullable Component<?> content;
    }

    private record FenwickTree(float[] tree) {
        FenwickTree(int n) {
            this(new float[Math.max(0, n) + 1]);
        }

        int size() {
            return Math.max(0, this.tree.length - 1);
        }

        void add(int i, float delta) {
            for (int x = i; x < this.tree.length; x += x & -x) this.tree[x] += delta;
        }

        float sum(int i) {
            float res = 0f;
            for (int x = i; x > 0; x -= x & -x) res += this.tree[x];
            return res;
        }

        float sum(int l, int r) {
            return sum(r) - sum(l - 1);
        }

        int findPrefixIndex(float target) {
            int n = size();
            int idx = 0;
            int bit = Integer.highestOneBit(n);
            float acc = 0f;
            for (int k = bit; k != 0; k >>= 1) {
                int next = idx + k;
                if (next <= n && acc + this.tree[next] <= target) {
                    acc += this.tree[next];
                    idx = next;
                }
            }
            if (idx >= n) return n - 1;
            return idx; // zero-based index equals idx
        }
    }
}
