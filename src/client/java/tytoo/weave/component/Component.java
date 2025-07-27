package tytoo.weave.component;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.*;
import tytoo.weave.layout.Layout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.EdgeInsets;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> {
    protected Component<?> parent;
    protected List<Component<?>> children = new ArrayList<>();
    protected Constraints constraints = new Constraints(this);
    protected EdgeInsets margin = EdgeInsets.zero();
    protected EdgeInsets padding = EdgeInsets.zero();
    protected Layout layout;

    protected List<Consumer<MouseClickEvent>> mouseClickListeners = new ArrayList<>();
    protected List<Consumer<MouseEnterEvent>> mouseEnterListeners = new ArrayList<>();
    protected List<Consumer<MouseLeaveEvent>> mouseLeaveListeners = new ArrayList<>();
    protected List<Consumer<MouseDragEvent>> mouseDragListeners = new ArrayList<>();
    protected List<Consumer<MouseScrollEvent>> mouseScrollListeners = new ArrayList<>();
    protected List<Consumer<KeyPressEvent>> keyPressListeners = new ArrayList<>();
    protected List<Consumer<CharTypeEvent>> charTypeListeners = new ArrayList<>();
    protected List<Consumer<FocusGainedEvent>> focusGainedListeners = new ArrayList<>();
    protected List<Consumer<FocusLostEvent>> focusLostListeners = new ArrayList<>();

    private boolean focusable = false;

    public abstract void draw(DrawContext context);

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public void drawChildren(DrawContext context) {
        for (Component<?> child : children) {
            child.draw(context);
        }
    }

    public void addChild(Component<?> child) {
        this.children.add(child);
        child.parent = this;
        if (this.layout != null) {
            this.layout.apply(this);
        }
    }

    public void removeChild(Component<?> child) {
        this.children.remove(child);
        child.parent = null;
        if (this.layout != null) {
            this.layout.apply(this);
        }
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
        this.mouseClickListeners.add(listener);
        return self();
    }

    public T onMouseEnter(Consumer<MouseEnterEvent> listener) {
        this.mouseEnterListeners.add(listener);
        return self();
    }

    public T onMouseLeave(Consumer<MouseLeaveEvent> listener) {
        this.mouseLeaveListeners.add(listener);
        return self();
    }

    public T onMouseDrag(Consumer<MouseDragEvent> listener) {
        this.mouseDragListeners.add(listener);
        return self();
    }

    public T onMouseScroll(Consumer<MouseScrollEvent> listener) {
        this.mouseScrollListeners.add(listener);
        return self();
    }

    public T onKeyPress(Consumer<KeyPressEvent> listener) {
        this.keyPressListeners.add(listener);
        return self();
    }

    public T onCharTyped(Consumer<CharTypeEvent> listener) {
        this.charTypeListeners.add(listener);
        return self();
    }

    public T onFocusGained(Consumer<FocusGainedEvent> listener) {
        this.focusGainedListeners.add(listener);
        return self();
    }

    public T onFocusLost(Consumer<FocusLostEvent> listener) {
        this.focusLostListeners.add(listener);
        return self();
    }

    public T setLayout(@Nullable Layout layout) {
        this.layout = layout;
        if (this.layout != null) {
            this.layout.apply(this);
        }
        return self();
    }

    public T addChildren(Component<?>... components) {
        for (Component<?> component : components) {
            this.addChild(component);
        }
        return self();
    }

    public void fireMouseClick(MouseClickEvent event) {
        for (Consumer<MouseClickEvent> listener : mouseClickListeners) {
            listener.accept(event);
        }
    }

    public void fireMouseEnter(MouseEnterEvent event) {
        for (Consumer<MouseEnterEvent> listener : mouseEnterListeners) {
            listener.accept(event);
        }
    }

    public void fireMouseLeave(MouseLeaveEvent event) {
        for (Consumer<MouseLeaveEvent> listener : mouseLeaveListeners) {
            listener.accept(event);
        }
    }

    public void fireMouseDrag(MouseDragEvent event) {
        for (Consumer<MouseDragEvent> listener : mouseDragListeners) {
            listener.accept(event);
        }
    }

    public void fireMouseScroll(MouseScrollEvent event) {
        for (Consumer<MouseScrollEvent> listener : mouseScrollListeners) {
            listener.accept(event);
        }
    }

    public void fireKeyPress(KeyPressEvent event) {
        for (Consumer<KeyPressEvent> listener : keyPressListeners) {
            listener.accept(event);
        }
    }

    public void fireCharTyped(CharTypeEvent event) {
        for (Consumer<CharTypeEvent> listener : charTypeListeners) {
            listener.accept(event);
        }
    }

    public void fireFocusGained(FocusGainedEvent event) {
        for (Consumer<FocusGainedEvent> listener : focusGainedListeners) {
            listener.accept(event);
        }
    }

    public void fireFocusLost(FocusLostEvent event) {
        for (Consumer<FocusLostEvent> listener : focusLostListeners) {
            listener.accept(event);
        }
    }

    public Component<?> hitTest(float x, float y) {
        for (ListIterator<Component<?>> it = children.listIterator(children.size()); it.hasPrevious(); ) {
            Component<?> child = it.previous();
            if (child.isPointInside(x, y)) {
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
}