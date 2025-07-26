package tytoo.weave.component;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.Constraints;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> {
    protected Component<?> parent;
    protected List<Component<?>> children = new ArrayList<>();
    protected Constraints constraints = new Constraints(this);

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
}