package tytoo.weave.component;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.EventType;
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.*;
import tytoo.weave.layout.Layout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.EdgeInsets;
import tytoo.weave.style.renderer.ComponentRenderer;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> implements Cloneable {
    protected Component<?> parent;
    protected List<Component<?>> children = new ArrayList<>();
    protected Constraints constraints = new Constraints(this);
    protected EdgeInsets margin = EdgeInsets.zero();
    protected EdgeInsets padding = EdgeInsets.zero();
    protected Layout layout;
    protected Map<EventType<?>, List<Consumer<?>>> eventListeners = new HashMap<>();
    protected Object layoutData;
    protected ComponentStyle style = new ComponentStyle();
    private boolean focusable = false;
    private boolean visible = true;

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public void draw(DrawContext context) {
        if (!this.visible) return;
        ComponentRenderer renderer = style.getRenderer(this);
        if (renderer != null) renderer.render(context, this);
        drawChildren(context);
    }

    public void drawChildren(DrawContext context) {
        for (Component<?> child : children) {
            child.draw(context);
        }
    }

    private void relayout() {
        if (this.layout != null) {
            this.layout.apply(this);
        }
    }

    public void addChild(Component<?> child) {
        this.children.add(child);
        child.parent = this;
        relayout();
    }

    public void removeChild(Component<?> child) {
        this.children.remove(child);
        child.parent = null;
        relayout();
    }

    public Component<?> getParent() {
        return parent;
    }

    public T setParent(Component<?> parent) {
        parent.addChild(this);
        return self();
    }

    public List<Component<?>> getChildren() {
        return children;
    }

    public float getRawLeft() {
        return this.constraints.getX();
    }

    public float getRawTop() {
        return this.constraints.getY();
    }

    public float getRawWidth() {
        return this.constraints.getWidth();
    }

    public float getRawHeight() {
        return this.constraints.getHeight();
    }

    public float getLeft() {
        return getRawLeft() + margin.left;
    }

    public float getTop() {
        return getRawTop() + margin.top;
    }

    public float getWidth() {
        float rawWidth = getRawWidth();
        if (rawWidth == 0) return 0;
        return rawWidth - margin.left - margin.right;
    }

    public T setWidth(WidthConstraint constraint) {
        this.constraints.setWidth(constraint);
        return self();
    }

    public float getHeight() {
        float rawHeight = getRawHeight();
        if (rawHeight == 0) return 0;
        return rawHeight - margin.top - margin.bottom;
    }

    public T setHeight(HeightConstraint constraint) {
        this.constraints.setHeight(constraint);
        return self();
    }

    public float getInnerLeft() {
        return getLeft() + padding.left;
    }

    public float getInnerTop() {
        return getTop() + padding.top;
    }

    public float getInnerWidth() {
        return getWidth() - padding.left - padding.right;
    }

    public float getInnerHeight() {
        return getHeight() - padding.top - padding.bottom;
    }

    public T setX(XConstraint constraint) {
        this.constraints.setX(constraint);
        return self();
    }

    private void handleAutoMargins() {
        if (Float.isNaN(this.margin.left) && Float.isNaN(this.margin.right)) {
            this.constraints.setX(Constraints.center());
            this.margin.left = 0;
            this.margin.right = 0;
        }

        if (Float.isNaN(this.margin.top) && Float.isNaN(this.margin.bottom)) {
            this.constraints.setY(Constraints.center());
            this.margin.top = 0;
            this.margin.bottom = 0;
        }
    }

    public T setMargin(float all) {
        this.margin = new EdgeInsets(all);
        handleAutoMargins();
        return self();
    }

    public T setMargin(float vertical, float horizontal) {
        this.margin = new EdgeInsets(vertical, horizontal);
        handleAutoMargins();
        return self();
    }

    public T setMargin(float top, float right, float bottom, float left) {
        this.margin = new EdgeInsets(top, right, bottom, left);
        handleAutoMargins();
        return self();
    }

    public T setPadding(float all) {
        this.padding = new EdgeInsets(all);
        return self();
    }

    public T setPadding(float vertical, float horizontal) {
        this.padding = new EdgeInsets(vertical, horizontal);
        return self();
    }

    public T setPadding(float top, float right, float bottom, float left) {
        this.padding = new EdgeInsets(top, right, bottom, left);
        return self();
    }

    public T setY(YConstraint constraint) {
        this.constraints.setY(constraint);
        return self();
    }

    public T onMouseClick(Consumer<MouseClickEvent> listener) {
        return onEvent(MouseClickEvent.TYPE, listener);
    }

    public T onMouseEnter(Consumer<MouseEnterEvent> listener) {
        return onEvent(MouseEnterEvent.TYPE, listener);
    }

    public T onMouseLeave(Consumer<MouseLeaveEvent> listener) {
        return onEvent(MouseLeaveEvent.TYPE, listener);
    }

    public T onMouseDrag(Consumer<MouseDragEvent> listener) {
        return onEvent(MouseDragEvent.TYPE, listener);
    }

    public T onMouseScroll(Consumer<MouseScrollEvent> listener) {
        return onEvent(MouseScrollEvent.TYPE, listener);
    }

    public T onKeyPress(Consumer<KeyPressEvent> listener) {
        return onEvent(KeyPressEvent.TYPE, listener);
    }

    public T onCharTyped(Consumer<CharTypeEvent> listener) {
        return onEvent(CharTypeEvent.TYPE, listener);
    }

    public T onFocusGained(Consumer<FocusGainedEvent> listener) {
        return onEvent(FocusGainedEvent.TYPE, listener);
    }

    public T onFocusLost(Consumer<FocusLostEvent> listener) {
        return onEvent(FocusLostEvent.TYPE, listener);
    }

    public <E extends tytoo.weave.event.Event> T onEvent(EventType<E> type, Consumer<E> listener) {
        this.eventListeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
        return self();
    }

    public T setLayout(@Nullable Layout layout) {
        this.layout = layout;
        if (this.layout != null) {
            this.layout.apply(this);
        }
        return self();
    }

    public Object getLayoutData() {
        return layoutData;
    }

    public T setLayoutData(Object layoutData) {
        this.layoutData = layoutData;
        return self();
    }

    public ComponentStyle getStyle() {
        return style;
    }

    public T setStyle(ComponentStyle style) {
        this.style = style;
        return self();
    }

    public T addChildren(Component<?>... components) {
        for (Component<?> component : components) {
            this.addChild(component);
        }
        return self();
    }

    @SuppressWarnings("unchecked")
    public <E extends tytoo.weave.event.Event> void fireEvent(E event) {
        EventType<E> type = (EventType<E>) event.getType();

        List<Consumer<?>> listeners = eventListeners.get(type);
        if (listeners != null) {
            for (Consumer<?> listener : new ArrayList<>(listeners)) {
                ((Consumer<E>) listener).accept(event);
            }
        }

        if (!event.isCancelled()) {
            List<Consumer<?>> anyListeners = eventListeners.get(tytoo.weave.event.Event.ANY);
            if (anyListeners != null) {
                for (Consumer<?> listener : new ArrayList<>(anyListeners)) {
                    ((Consumer<tytoo.weave.event.Event>) listener).accept(event);
                }
            }
        }
    }

    public Component<?> hitTest(float x, float y) {
        for (ListIterator<Component<?>> it = children.listIterator(children.size()); it.hasPrevious(); ) {
            Component<?> child = it.previous();
            if (child.isVisible() && child.isPointInside(x, y)) {
                return child.hitTest(x, y);
            }
        }
        return this;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public T setFocusable(boolean focusable) {
        this.focusable = focusable;
        return self();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public T setVisible(boolean visible) {
        this.visible = visible;
        return self();
    }

    public T bindVisibility(State<Boolean> visibilityState) {
        visibilityState.bind(this::setVisible);
        return self();
    }

    public boolean isFocused() {
        WeaveScreen screen = WeaveScreen.getCurrentScreen();
        if (screen == null) {
            return false;
        }
        return screen.getFocusedComponent() == this;
    }

    public boolean isHovered() {
        WeaveScreen screen = WeaveScreen.getCurrentScreen();
        if (screen == null) {
            return false;
        }
        Component<?> hovered = screen.getHoveredComponent();
        if (hovered == null) {
            return false;
        }
        for (Component<?> c = hovered; c != null; c = c.getParent()) {
            if (c == this) return true;
        }
        return false;
    }

    public boolean isPointInside(float x, float y) {
        return x >= getLeft() && x <= getLeft() + getWidth() && y >= getTop() && y <= getTop() + getHeight();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            T clone = (T) super.clone();

            clone.parent = null;

            clone.constraints = new Constraints(clone);
            clone.constraints.setX(this.constraints.getXConstraint());
            clone.constraints.setY(this.constraints.getYConstraint());
            clone.constraints.setWidth(this.constraints.getWidthConstraint());
            clone.constraints.setHeight(this.constraints.getHeightConstraint());

            clone.margin = new EdgeInsets(this.margin.top, this.margin.right, this.margin.bottom, this.margin.left);
            clone.padding = new EdgeInsets(this.padding.top, this.padding.right, this.padding.bottom, this.padding.left);

            clone.eventListeners = new HashMap<>();
            for (Map.Entry<EventType<?>, List<Consumer<?>>> entry : this.eventListeners.entrySet()) {
                clone.eventListeners.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }

            clone.children = new ArrayList<>();
            for (Component<?> child : this.children) {
                Component<?> childClone = child.clone();
                clone.addChild(childClone);
            }

            clone.setVisible(this.isVisible());

            clone.style = this.style.clone();

            clone.setLayoutData(this.layoutData);

            clone.updateClonedChildReferences(this);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Component is Cloneable but clone() failed", e);
        }
    }

    protected void updateClonedChildReferences(Component<T> original) {
        // Default implementation is empty.
    }
}