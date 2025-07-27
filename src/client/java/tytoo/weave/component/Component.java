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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> {
    protected Component<?> parent;
    protected List<Component<?>> children = new ArrayList<>();
    protected Constraints constraints = new Constraints(this);
    @Nullable
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

    public float getLeft() {
        return this.constraints.getX();
    }

    public float getTop() {
        return this.constraints.getY();
    }

    public float getWidth() {
        return this.constraints.getWidth();
    }

    public T setWidth(WidthConstraint constraint) {
        this.constraints.setWidth(constraint);
        return self();
    }

    public float getHeight() {
        return this.constraints.getHeight();
    }

    public T setHeight(HeightConstraint constraint) {
        this.constraints.setHeight(constraint);
        return self();
    }

    public T setX(XConstraint constraint) {
        this.constraints.setX(constraint);
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