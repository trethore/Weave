package tytoo.weave.component;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.Constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> {
    protected Component<?> parent;
    protected List<Component<?>> children = new ArrayList<>();
    protected Constraints constraints = new Constraints(this);
    protected List<Runnable> clickListeners = new ArrayList<>();

    public abstract void draw(DrawContext context);

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T setParent(Component<?> parent) {
        parent.addChild(this);
        return self();
    }

    public void addChild(Component<?> child) {
        this.children.add(child);
        child.parent = this;
    }

    public void removeChild(Component<?> child) {
        this.children.remove(child);
        child.parent = null;
    }

    public Component<?> getParent() {
        return parent;
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

    public float getHeight() {
        return this.constraints.getHeight();
    }

    public T setX(XConstraint constraint) {
        this.constraints.setX(constraint);
        return self();
    }

    public T setY(YConstraint constraint) {
        this.constraints.setY(constraint);
        return self();
    }

    public T setWidth(WidthConstraint constraint) {
        this.constraints.setWidth(constraint);
        return self();
    }

    public T setHeight(HeightConstraint constraint) {
        this.constraints.setHeight(constraint);
        return self();
    }

    public T onMouseClick(Runnable listener) {
        this.clickListeners.add(listener);
        return self();
    }

    public void mouseClick(float mouseX, float mouseY, int button) {
        for (Runnable listener : clickListeners) {
            listener.run();
        }
    }

    public Component<?> hitTest(float x, float y) {
        for (ListIterator<Component<?>> it = children.listIterator(children.size()); it.hasPrevious(); ) {
            Component<?> child = it.previous();
            if (child.isPointInside(x, y)) return child.hitTest(x, y);
        }
        return this;
    }

    public boolean isPointInside(float x, float y) {
        return x >= getLeft() && x <= getLeft() + getWidth() && y >= getTop() && y <= getTop() + getHeight();
    }
}