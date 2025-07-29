package tytoo.weave.component;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.animation.AnimationBuilder;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effect;
import tytoo.weave.event.EventType;
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.*;
import tytoo.weave.layout.Layout;
import tytoo.weave.state.State;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.EdgeInsets;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.utils.McUtils;

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
    protected ComponentStyle style;
    protected List<Effect> effects = new ArrayList<>();

    protected float measuredWidth, measuredHeight;
    protected float finalX, finalY, finalWidth, finalHeight;
    protected boolean layoutDirty = true;
    private boolean focusable = false;
    private boolean visible = true;


    public Component() {
        ComponentStyle sheetStyle = ThemeManager.getStylesheet().getStyleFor(this.getClass());
        this.style = sheetStyle != null ? sheetStyle.clone() : new ComponentStyle();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public void draw(DrawContext context) {
        if (!this.visible) return;

        for (Effect effect : effects) {
            effect.beforeDraw(context, this);
        }

        ComponentRenderer renderer = style.getRenderer(this);
        if (renderer != null) renderer.render(context, this);
        drawChildren(context);

        for (int i = effects.size() - 1; i >= 0; i--) {
            effects.get(i).afterDraw(context, this);
        }
    }

    public void drawChildren(DrawContext context) {
        for (Component<?> child : children) {
            child.draw(context);
        }
    }

    public void addChild(Component<?> child) {
        this.children.add(child);
        child.parent = this;
        invalidateLayout();
    }

    public void invalidateLayout() {
        if (!this.layoutDirty) {
            this.layoutDirty = true;
            if (parent != null) {
                parent.invalidateLayout();
            }
        }
    }

    public void removeChild(Component<?> child) {
        this.children.remove(child);
        child.parent = null;
        invalidateLayout();
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
        return this.finalX;
    }

    public float getRawTop() {
        return this.finalY;
    }

    public float getRawWidth() {
        return this.finalWidth;
    }

    public float getRawHeight() {
        return this.finalHeight;
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
        invalidateLayout();
        return self();
    }

    public T setMinWidth(float minWidth) {
        this.constraints.setMinWidth(minWidth);
        invalidateLayout();
        return self();
    }

    public T setMaxWidth(float maxWidth) {
        this.constraints.setMaxWidth(maxWidth);
        invalidateLayout();
        return self();
    }

    public float getHeight() {
        float rawHeight = getRawHeight();
        if (rawHeight == 0) return 0;
        return rawHeight - margin.top - margin.bottom;
    }

    public T setHeight(HeightConstraint constraint) {
        this.constraints.setHeight(constraint);
        invalidateLayout();
        return self();
    }

    public T setMinHeight(float minHeight) {
        this.constraints.setMinHeight(minHeight);
        invalidateLayout();
        return self();
    }

    public T setMaxHeight(float maxHeight) {
        this.constraints.setMaxHeight(maxHeight);
        invalidateLayout();
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
        invalidateLayout();
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

    public T setMargin(float vertical, float horizontal) {
        this.margin = new EdgeInsets(vertical, horizontal);
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public T setMargin(float top, float right, float bottom, float left) {
        this.margin = new EdgeInsets(top, right, bottom, left);
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public T setPadding(float all) {
        this.padding = new EdgeInsets(all);
        invalidateLayout();
        return self();
    }

    public T setPadding(float vertical, float horizontal) {
        this.padding = new EdgeInsets(vertical, horizontal);
        invalidateLayout();
        return self();
    }

    public T setPadding(float top, float right, float bottom, float left) {
        this.padding = new EdgeInsets(top, right, bottom, left);
        invalidateLayout();
        return self();
    }

    public T setY(YConstraint constraint) {
        this.constraints.setY(constraint);
        invalidateLayout();
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
        invalidateLayout();
        return self();
    }

    public Object getLayoutData() {
        return layoutData;
    }

    public T setLayoutData(Object layoutData) {
        this.layoutData = layoutData;
        invalidateLayout();
        return self();
    }

    public ComponentStyle getStyle() {
        return style;
    }

    public T setStyle(ComponentStyle style) {
        this.style = style;
        invalidateLayout();
        return self();
    }

    public T addChildren(Component<?>... components) {
        for (Component<?> component : components) {
            this.addChild(component);
        }
        return self();
    }

    public T addEffect(Effect effect) {
        this.effects.add(effect);
        invalidateLayout();
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
        if (parent != null) parent.invalidateLayout();
        return self();
    }

    public T bindVisibility(State<Boolean> visibilityState) {
        visibilityState.bind(this::setVisible);
        return self();
    }

    public boolean isFocused() {
        return McUtils.getMc().map(mc -> mc.currentScreen)
                .flatMap(UIManager::getState)
                .map(state -> state.getFocusedComponent() == this)
                .orElse(false);
    }

    public boolean isHovered() {
        return McUtils.getMc().map(mc -> mc.currentScreen)
                .flatMap(UIManager::getState)
                .map(state -> {
                    Component<?> hovered = state.getHoveredComponent();
                    if (hovered == null) {
                        return false;
                    }
                    for (Component<?> c = hovered; c != null; c = c.getParent()) {
                        if (c == this) return true;
                    }
                    return false;
                }).orElse(false);
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

            clone.layoutDirty = true;

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
            clone.effects = new ArrayList<>(this.effects);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Component is Cloneable but clone() failed", e);
        }
    }

    protected void updateClonedChildReferences(Component<T> original) {
        // Default implementation is empty.
    }

    public AnimationBuilder<T> animate() {
        return new AnimationBuilder<>(self());
    }

    public Constraints getConstraints() {
        return this.constraints;
    }

    public EdgeInsets getMargin() {
        return this.margin;
    }

    public T setMargin(float all) {
        this.margin = new EdgeInsets(all);
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public float getMeasuredWidth() {
        return measuredWidth;
    }

    public float getMeasuredHeight() {
        return measuredHeight;
    }

    public float getFinalWidth() {
        return finalWidth;
    }

    public float getFinalHeight() {
        return finalHeight;
    }

    public boolean isLayoutDirty() {
        return layoutDirty;
    }

    public void measure(float availableWidth, float availableHeight) {
        if (!this.visible) {
            this.measuredWidth = 0;
            this.measuredHeight = 0;
            return;
        }

        WidthConstraint wc = constraints.getWidthConstraint();
        HeightConstraint hc = constraints.getHeightConstraint();

        boolean widthDependsOnChildren = wc instanceof tytoo.weave.constraint.constraints.ChildBasedSizeConstraint;
        boolean heightDependsOnChildren = hc instanceof tytoo.weave.constraint.constraints.ChildBasedSizeConstraint || hc instanceof tytoo.weave.constraint.constraints.SumOfChildrenHeightConstraint;

        if (widthDependsOnChildren || heightDependsOnChildren) {
            float horizontalPadding = padding.left + padding.right;
            float verticalPadding = padding.top + padding.bottom;
            for (Component<?> child : children) {
                child.measure(availableWidth - horizontalPadding, availableHeight - verticalPadding);
            }
        }

        float w, h;

        if (wc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint && !(hc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint)) {
            h = hc.calculateHeight(this, availableHeight);
            this.measuredHeight = h;
            w = wc.calculateWidth(this, availableWidth);
        } else if (hc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint && !(wc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint)) {
            w = wc.calculateWidth(this, availableWidth);
            this.measuredWidth = w;
            h = hc.calculateHeight(this, availableHeight);
        } else {
            w = wc.calculateWidth(this, availableWidth);
            h = hc.calculateHeight(this, availableHeight);
        }

        this.measuredWidth = constraints.clampWidth(w);
        this.measuredHeight = constraints.clampHeight(h);

        if (!widthDependsOnChildren && !heightDependsOnChildren) {
            float horizontalPadding = padding.left + padding.right;
            float verticalPadding = padding.top + padding.bottom;
            for (Component<?> child : children) {
                child.measure(this.measuredWidth - horizontalPadding, this.measuredHeight - verticalPadding);
            }
        }
    }

    public void arrange(float x, float y) {
        if (!this.visible) return;

        this.finalX = x;
        this.finalY = y;
        this.finalWidth = this.measuredWidth + this.margin.left + this.margin.right;
        this.finalHeight = this.measuredHeight + this.margin.top + this.margin.bottom;

        if (this.layout != null) {
            this.layout.arrangeChildren(this);
        } else {
            for (Component<?> child : this.children) {
                if (!child.isVisible()) continue;
                float childX = child.getConstraints().getXConstraint().calculateX(child, this.getInnerWidth(), child.getMeasuredWidth() + child.getMargin().left + child.getMargin().right);
                float childY = child.getConstraints().getYConstraint().calculateY(child, this.getInnerHeight(), child.getMeasuredHeight() + child.getMargin().top + child.getMargin().bottom);
                child.arrange(this.getInnerLeft() + childX, this.getInnerTop() + childY);
            }
        }

        this.layoutDirty = false;
    }

    public T bringToFront() {
        if (parent != null) {
            parent.children.remove(this);
            parent.children.add(this);
        }
        return self();
    }

    public T sendToBack() {
        if (parent != null) {
            parent.children.remove(this);
            parent.children.addFirst(this);
        }
        return self();
    }
}