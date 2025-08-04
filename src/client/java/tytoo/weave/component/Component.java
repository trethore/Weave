package tytoo.weave.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import tytoo.weave.animation.AnimationBuilder;
import tytoo.weave.animation.Animator;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effect;
import tytoo.weave.event.Event;
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
import tytoo.weave.style.StyleState;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.utils.McUtils;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> implements Cloneable {
    protected final LayoutState layoutState;
    protected final RenderState renderState;
    protected final EventState eventState;

    private final Set<StyleState> activeStyleStates = new HashSet<>();
    protected Component<?> parent;
    protected List<Component<?>> children = new LinkedList<>();
    protected ComponentStyle style;
    @Nullable
    protected TextRenderer textRenderer;

    public Component() {
        this.layoutState = new LayoutState(this);
        this.renderState = new RenderState(this);
        this.eventState = new EventState();

        ComponentStyle sheetStyle = ThemeManager.getStylesheet().getStyleFor(this.getClass());
        this.style = sheetStyle != null ? sheetStyle.clone() : new ComponentStyle();
    }

    public LayoutState getLayoutState() {
        return layoutState;
    }

    public RenderState getRenderState() {
        return renderState;
    }

    public EventState getEventState() {
        return eventState;
    }

    protected void applyTransformations(DrawContext context) {
        this.renderState.applyTransformations(context);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public void draw(DrawContext context) {
        if (!this.renderState.isVisible()) return;
        if (this.renderState.opacity.get() <= 0.001f) return;

        float[] lastColor = RenderSystem.getShaderColor().clone();
        RenderSystem.setShaderColor(lastColor[0], lastColor[1], lastColor[2], lastColor[3] * this.renderState.opacity.get());

        context.getMatrices().push();
        try {
            this.renderState.applyTransformations(context);

            for (Effect effect : this.renderState.getEffects()) {
                effect.beforeDraw(context, this);
            }

            ComponentRenderer renderer = style.getRenderer(this);
            if (renderer != null) renderer.render(context, this);
            drawChildren(context);

            for (int i = this.renderState.getEffects().size() - 1; i >= 0; i--) {
                this.renderState.getEffects().get(i).afterDraw(context, this);
            }
        } finally {
            context.getMatrices().pop();

            RenderSystem.setShaderColor(lastColor[0], lastColor[1], lastColor[2], lastColor[3]);
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
        this.layoutState.invalidateLayout();
    }

    public void removeAllChildren() {
        for (Component<?> child : new ArrayList<>(this.children)) {
            child.parent = null;
        }
        this.children.clear();
        invalidateLayout();
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
        return this.layoutState.getFinalX();
    }

    public float getRawTop() {
        return this.layoutState.getFinalY();
    }

    public float getRawWidth() {
        return this.layoutState.getFinalWidth();
    }

    public float getRawHeight() {
        return this.layoutState.getFinalHeight();
    }

    public float getLeft() {
        return this.layoutState.getLeft();
    }

    public float getTop() {
        return this.layoutState.getTop();
    }

    public float getWidth() {
        return this.layoutState.getWidth();
    }

    public T setWidth(WidthConstraint constraint) {
        this.layoutState.getConstraints().setWidth(constraint);
        invalidateLayout();
        return self();
    }

    public T setMinWidth(float minWidth) {
        this.layoutState.getConstraints().setMinWidth(minWidth);
        invalidateLayout();
        return self();
    }

    public T setMaxWidth(float maxWidth) {
        this.layoutState.getConstraints().setMaxWidth(maxWidth);
        invalidateLayout();
        return self();
    }

    public float getHeight() {
        return this.layoutState.getHeight();
    }

    public T setHeight(HeightConstraint constraint) {
        this.layoutState.getConstraints().setHeight(constraint);
        invalidateLayout();
        return self();
    }

    public T setMinHeight(float minHeight) {
        this.layoutState.getConstraints().setMinHeight(minHeight);
        invalidateLayout();
        return self();
    }

    public T setMaxHeight(float maxHeight) {
        this.layoutState.getConstraints().setMaxHeight(maxHeight);
        invalidateLayout();
        return self();
    }

    public float getInnerLeft() {
        return this.layoutState.getInnerLeft();
    }

    public float getInnerTop() {
        return this.layoutState.getInnerTop();
    }

    public float getInnerWidth() {
        return this.layoutState.getInnerWidth();
    }

    public float getInnerHeight() {
        return this.layoutState.getInnerHeight();
    }

    public T setX(XConstraint constraint) {
        this.layoutState.getConstraints().setX(constraint);
        invalidateLayout();
        return self();
    }

    private void handleAutoMargins() {
        float top = this.layoutState.getMargin().top(), right = this.layoutState.getMargin().right(), bottom = this.layoutState.getMargin().bottom(), left = this.layoutState.getMargin().left();

        boolean horizontalAuto = Float.isNaN(left) && Float.isNaN(right);
        boolean verticalAuto = Float.isNaN(top) && Float.isNaN(bottom);

        if (horizontalAuto) {
            this.layoutState.getConstraints().setX(Constraints.center());
            left = 0;
            right = 0;
        }
        if (verticalAuto) {
            this.layoutState.getConstraints().setY(Constraints.center());
            top = 0;
            bottom = 0;
        }

        if (horizontalAuto || verticalAuto) {
            this.layoutState.setMargin(new EdgeInsets(top, right, bottom, left));
        }
    }

    public T setMargin(float vertical, float horizontal) {
        this.layoutState.setMargin(new EdgeInsets(vertical, horizontal));
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public T setMargin(float top, float right, float bottom, float left) {
        this.layoutState.setMargin(new EdgeInsets(top, right, bottom, left));
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public T setPadding(float all) {
        this.layoutState.setPadding(new EdgeInsets(all));
        invalidateLayout();
        return self();
    }

    public T setPadding(float vertical, float horizontal) {
        this.layoutState.setPadding(new EdgeInsets(vertical, horizontal));
        invalidateLayout();
        return self();
    }

    public T setPadding(float top, float right, float bottom, float left) {
        this.layoutState.setPadding(new EdgeInsets(top, right, bottom, left));
        invalidateLayout();
        return self();
    }

    public T setY(YConstraint constraint) {
        this.layoutState.getConstraints().setY(constraint);
        invalidateLayout();
        return self();
    }

    public T setTextRenderer(@Nullable TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        invalidateLayout();
        return self();
    }

    public TextRenderer getEffectiveTextRenderer() {
        for (Component<?> c = this; c != null; c = c.getParent()) {
            if (c.textRenderer != null) {
                return c.textRenderer;
            }
        }
        return ThemeManager.getTheme().getTextRenderer();
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
        this.eventState.getEventListeners().computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
        return self();
    }

    public @Nullable Layout getLayout() {
        return this.layoutState.getLayout();
    }

    public T setLayout(@Nullable Layout layout) {
        this.layoutState.setLayout(layout);
        invalidateLayout();
        return self();
    }

    public Object getLayoutData() {
        return this.layoutState.getLayoutData();
    }

    public T setLayoutData(Object layoutData) {
        this.layoutState.setLayoutData(layoutData);
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
        this.renderState.getEffects().add(effect);
        invalidateLayout();
        return self();
    }

    public <E extends Event> void fireEvent(E event) {
        this.eventState.fireEvent(event);
    }

    public Component<?> hitTest(float x, float y) {
        if (!isPointInside(x, y)) return null;
        for (Component<?> child : children.reversed()) {
            if (child.isVisible()) {
                Component<?> hit = child.hitTest(x, y);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return this;
    }

    public boolean isFocusable() {
        return this.eventState.isFocusable();
    }

    public T setFocusable(boolean focusable) {
        this.eventState.setFocusable(focusable);
        return self();
    }

    public boolean isVisible() {
        return this.renderState.isVisible();
    }

    public T setVisible(boolean visible) {
        this.renderState.setVisible(visible);
        if (parent != null) parent.invalidateLayout();
        return self();
    }

    public T bindVisibility(State<Boolean> visibilityState) {
        visibilityState.bind(this::setVisible);
        return self();
    }

    public float getOpacity() {
        return this.renderState.opacity.get();
    }

    public T setOpacity(float opacity) {
        this.renderState.opacity.set(Math.max(0.0f, Math.min(1.0f, opacity)));
        return self();
    }

    public State<Float> getOpacityState() {
        return this.renderState.opacity;
    }

    public float getRotation() {
        return this.renderState.rotation.get();
    }

    public T setRotation(float rotation) {
        this.renderState.rotation.set(rotation);
        return self();
    }

    public State<Float> getRotationState() {
        return this.renderState.rotation;
    }

    public T setScale(float scale) {
        return setScale(scale, scale);
    }

    public T setScale(float scaleX, float scaleY) {
        this.renderState.scaleX.set(scaleX);
        this.renderState.scaleY.set(scaleY);
        return self();
    }

    public float getScaleX() {
        return this.renderState.scaleX.get();
    }

    public State<Float> getScaleXState() {
        return this.renderState.scaleX;
    }

    public float getScaleY() {
        return this.renderState.scaleY.get();
    }

    public State<Float> getScaleYState() {
        return this.renderState.scaleY;
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
                .map(state -> state.getHoveredComponent() == this)
                .orElse(false);
    }

    public Set<StyleState> getActiveStyleStates() {
        return activeStyleStates;
    }

    public T addStyleState(StyleState state) {
        if (activeStyleStates.add(state)) {
            invalidateLayout();
        }
        return self();
    }

    public T removeStyleState(StyleState state) {
        if (activeStyleStates.remove(state)) {
            invalidateLayout();
        }
        return self();
    }

    private Matrix4f getInverseTransformationMatrix() {
        Matrix4f matrix = new Matrix4f();
        float pivotX = getLeft() + getWidth() / 2;
        float pivotY = getTop() + getHeight() / 2;

        matrix.translate(pivotX, pivotY, 0);
        matrix.rotateZ((float) Math.toRadians(getRotation()));
        matrix.scale(getScaleX(), getScaleY(), 1.0f);
        matrix.translate(-pivotX, -pivotY, 0);

        return matrix.invert();
    }

    public boolean isPointInside(float x, float y) {
        if (getRotation() == 0.0f && getScaleX() == 1.0f && getScaleY() == 1.0f) {
            return x >= getLeft() && x <= getLeft() + getWidth() && y >= getTop() && y <= getTop() + getHeight();
        }

        Matrix4f inverse = getInverseTransformationMatrix();
        Vector4f point = new Vector4f(x, y, 0, 1);
        inverse.transform(point);

        float tx = point.x;
        float ty = point.y;

        return tx >= getLeft() && tx <= getLeft() + getWidth() && ty >= getTop() && ty <= getTop() + getHeight();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            T clonedComponent = (T) super.clone();

            clonedComponent.parent = null;
            clonedComponent.invalidateLayout();

            // Get the state objects that were created by the cloned component's constructor
            LayoutState clonedLayoutState = clonedComponent.getLayoutState();
            EventState clonedEventState = clonedComponent.getEventState();
            RenderState clonedRenderState = clonedComponent.getRenderState();

            // Copy LayoutState properties
            clonedLayoutState.getConstraints().setX(this.layoutState.getConstraints().getXConstraint());
            clonedLayoutState.getConstraints().setY(this.layoutState.getConstraints().getYConstraint());
            clonedLayoutState.getConstraints().setWidth(this.layoutState.getConstraints().getWidthConstraint());
            clonedLayoutState.getConstraints().setHeight(this.layoutState.getConstraints().getHeightConstraint());
            clonedLayoutState.setMargin(new EdgeInsets(this.layoutState.getMargin().top(), this.layoutState.getMargin().right(), this.layoutState.getMargin().bottom(), this.layoutState.getMargin().left()));
            clonedLayoutState.setPadding(new EdgeInsets(this.layoutState.getPadding().top(), this.layoutState.getPadding().right(), this.layoutState.getPadding().bottom(), this.layoutState.getPadding().left()));
            clonedLayoutState.setLayoutData(this.getLayoutData());

            // Copy EventState properties
            for (Map.Entry<EventType<?>, List<Consumer<?>>> entry : this.eventState.getEventListeners().entrySet()) {
                clonedEventState.getEventListeners().put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }

            // Clone children
            clonedComponent.children = new LinkedList<>();
            for (Component<?> child : this.children) {
                Component<?> childClone = child.clone();
                clonedComponent.addChild(childClone);
            }

            // Copy RenderState properties
            clonedRenderState.rotation.set(this.getRotation());
            clonedRenderState.opacity.set(this.getOpacity());
            clonedRenderState.scaleX.set(this.getScaleX());
            clonedRenderState.scaleY.set(this.getScaleY());
            clonedRenderState.setVisible(this.renderState.isVisible());
            clonedRenderState.setEffects(new ArrayList<>(this.renderState.getEffects()));

            // Copy other properties
            clonedComponent.style = this.style.clone();
            clonedComponent.updateClonedChildReferences(this);

            return clonedComponent;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Component is Cloneable but clone() failed", e);
        }
    }

    protected void updateClonedChildReferences(Component<T> original) {
        // Default implementation is empty.
    }

    public AnimationBuilder<T> animate() {
        return Animator.getBuilderFor(self());
    }

    public Constraints getConstraints() {
        return this.layoutState.getConstraints();
    }

    public EdgeInsets getMargin() {
        return this.layoutState.getMargin();
    }

    public T setMargin(float all) {
        this.layoutState.setMargin(new EdgeInsets(all));
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public float getMeasuredWidth() {
        return this.layoutState.getMeasuredWidth();
    }

    public float getMeasuredHeight() {
        return this.layoutState.getMeasuredHeight();
    }

    public float getFinalWidth() {
        return this.layoutState.getFinalWidth();
    }

    public float getFinalHeight() {
        return this.layoutState.getFinalHeight();
    }

    public boolean isLayoutDirty() {
        return this.layoutState.isLayoutDirty();
    }

    public void measure(float availableWidth, float availableHeight) {
        if (!this.renderState.isVisible()) {
            this.layoutState.setMeasuredWidth(0);
            this.layoutState.setMeasuredHeight(0);
            return;
        }

        WidthConstraint wc = this.layoutState.getConstraints().getWidthConstraint();
        HeightConstraint hc = this.layoutState.getConstraints().getHeightConstraint();

        boolean widthDependsOnChildren = wc instanceof tytoo.weave.constraint.constraints.ChildBasedSizeConstraint;
        boolean heightDependsOnChildren = hc instanceof tytoo.weave.constraint.constraints.ChildBasedSizeConstraint || hc instanceof tytoo.weave.constraint.constraints.SumOfChildrenHeightConstraint;

        if (widthDependsOnChildren || heightDependsOnChildren) {
            float horizontalPadding = this.layoutState.getPadding().left() + this.layoutState.getPadding().right();
            float verticalPadding = this.layoutState.getPadding().top() + this.layoutState.getPadding().bottom();
            for (Component<?> child : children) {
                child.measure(availableWidth - horizontalPadding, availableHeight - verticalPadding);
            }
        }

        float w, h;

        if (wc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint && !(hc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint)) {
            h = hc.calculateHeight(this, availableHeight);
            this.layoutState.setMeasuredHeight(this.layoutState.getConstraints().clampHeight(h));
            w = wc.calculateWidth(this, availableWidth);
            this.layoutState.setMeasuredWidth(this.layoutState.getConstraints().clampWidth(w));
        } else {
            w = wc.calculateWidth(this, availableWidth);
            this.layoutState.setMeasuredWidth(this.layoutState.getConstraints().clampWidth(w));
            h = hc.calculateHeight(this, availableHeight);
            this.layoutState.setMeasuredHeight(this.layoutState.getConstraints().clampHeight(h));
        }

        if (!widthDependsOnChildren && !heightDependsOnChildren) {
            float horizontalPadding = this.layoutState.getPadding().left() + this.layoutState.getPadding().right();
            float verticalPadding = this.layoutState.getPadding().top() + this.layoutState.getPadding().bottom();
            for (Component<?> child : children) {
                child.measure(this.layoutState.getMeasuredWidth() - horizontalPadding, this.layoutState.getMeasuredHeight() - verticalPadding);
            }
        }
    }

    public void arrange(float x, float y) {
        if (!this.renderState.isVisible()) return;

        this.layoutState.setFinalX(x);
        this.layoutState.setFinalY(y);
        this.layoutState.setFinalWidth(this.layoutState.getMeasuredWidth() + this.layoutState.getMargin().left() + this.layoutState.getMargin().right());
        this.layoutState.setFinalHeight(this.layoutState.getMeasuredHeight() + this.layoutState.getMargin().top() + this.layoutState.getMargin().bottom());

        if (this.layoutState.getLayout() != null) {
            this.layoutState.getLayout().arrangeChildren(this);
        } else {
            for (Component<?> child : this.children) {
                if (!child.isVisible()) continue;
                float childX = child.getConstraints().getXConstraint().calculateX(child, getInnerWidth(), child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right());
                float childY = child.getConstraints().getYConstraint().calculateY(child, getInnerHeight(), child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom());
                child.arrange(this.getInnerLeft() + childX, this.getInnerTop() + childY);
            }
        }

        this.layoutState.setLayoutDirty(false);
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