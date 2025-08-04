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
    protected List<Component<?>> children = new ArrayList<>();
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
        if (!this.renderState.visible) return;
        if (this.renderState.opacity.get() <= 0.001f) return;

        float[] lastColor = RenderSystem.getShaderColor().clone();
        RenderSystem.setShaderColor(lastColor[0], lastColor[1], lastColor[2], lastColor[3] * this.renderState.opacity.get());

        context.getMatrices().push();
        try {
            this.renderState.applyTransformations(context);

            for (Effect effect : this.renderState.effects) {
                effect.beforeDraw(context, this);
            }

            ComponentRenderer renderer = style.getRenderer(this);
            if (renderer != null) renderer.render(context, this);
            drawChildren(context);

            for (int i = this.renderState.effects.size() - 1; i >= 0; i--) {
                this.renderState.effects.get(i).afterDraw(context, this);
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
        return this.layoutState.finalX;
    }

    public float getRawTop() {
        return this.layoutState.finalY;
    }

    public float getRawWidth() {
        return this.layoutState.finalWidth;
    }

    public float getRawHeight() {
        return this.layoutState.finalHeight;
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
        this.layoutState.constraints.setWidth(constraint);
        invalidateLayout();
        return self();
    }

    public T setMinWidth(float minWidth) {
        this.layoutState.constraints.setMinWidth(minWidth);
        invalidateLayout();
        return self();
    }

    public T setMaxWidth(float maxWidth) {
        this.layoutState.constraints.setMaxWidth(maxWidth);
        invalidateLayout();
        return self();
    }

    public float getHeight() {
        return this.layoutState.getHeight();
    }

    public T setHeight(HeightConstraint constraint) {
        this.layoutState.constraints.setHeight(constraint);
        invalidateLayout();
        return self();
    }

    public T setMinHeight(float minHeight) {
        this.layoutState.constraints.setMinHeight(minHeight);
        invalidateLayout();
        return self();
    }

    public T setMaxHeight(float maxHeight) {
        this.layoutState.constraints.setMaxHeight(maxHeight);
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
        this.layoutState.constraints.setX(constraint);
        invalidateLayout();
        return self();
    }

    private void handleAutoMargins() {
        float top = this.layoutState.margin.top(), right = this.layoutState.margin.right(), bottom = this.layoutState.margin.bottom(), left = this.layoutState.margin.left();

        boolean horizontalAuto = Float.isNaN(left) && Float.isNaN(right);
        boolean verticalAuto = Float.isNaN(top) && Float.isNaN(bottom);

        if (horizontalAuto) {
            this.layoutState.constraints.setX(Constraints.center());
            left = 0;
            right = 0;
        }
        if (verticalAuto) {
            this.layoutState.constraints.setY(Constraints.center());
            top = 0;
            bottom = 0;
        }

        if (horizontalAuto || verticalAuto) {
            this.layoutState.margin = new EdgeInsets(top, right, bottom, left);
        }
    }

    public T setMargin(float vertical, float horizontal) {
        this.layoutState.margin = new EdgeInsets(vertical, horizontal);
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public T setMargin(float top, float right, float bottom, float left) {
        this.layoutState.margin = new EdgeInsets(top, right, bottom, left);
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public T setPadding(float all) {
        this.layoutState.padding = new EdgeInsets(all);
        invalidateLayout();
        return self();
    }

    public T setPadding(float vertical, float horizontal) {
        this.layoutState.padding = new EdgeInsets(vertical, horizontal);
        invalidateLayout();
        return self();
    }

    public T setPadding(float top, float right, float bottom, float left) {
        this.layoutState.padding = new EdgeInsets(top, right, bottom, left);
        invalidateLayout();
        return self();
    }

    public T setY(YConstraint constraint) {
        this.layoutState.constraints.setY(constraint);
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
        this.eventState.eventListeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
        return self();
    }

    public @Nullable Layout getLayout() {
        return this.layoutState.layout;
    }

    public T setLayout(@Nullable Layout layout) {
        this.layoutState.layout = layout;
        invalidateLayout();
        return self();
    }

    public Object getLayoutData() {
        return this.layoutState.layoutData;
    }

    public T setLayoutData(Object layoutData) {
        this.layoutState.layoutData = layoutData;
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
        this.renderState.effects.add(effect);
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
        return this.eventState.focusable;
    }

    public T setFocusable(boolean focusable) {
        this.eventState.focusable = focusable;
        return self();
    }

    public boolean isVisible() {
        return this.renderState.visible;
    }

    public T setVisible(boolean visible) {
        this.renderState.visible = visible;
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
        Matrix4f matrix = new Matrix4f(); // Start with identity
        float pivotX = getLeft() + getWidth() / 2; // Use public API for position
        float pivotY = getTop() + getHeight() / 2;

        matrix.translate(pivotX, pivotY, 0); // Translate to pivot
        matrix.rotateZ((float) Math.toRadians(getRotation())); // Rotate
        matrix.scale(getScaleX(), getScaleY(), 1.0f); // Scale
        matrix.translate(-pivotX, -pivotY, 0); // Translate back

        return matrix.invert(); // Return the inverse
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
            clonedLayoutState.constraints.setX(this.layoutState.constraints.getXConstraint());
            clonedLayoutState.constraints.setY(this.layoutState.constraints.getYConstraint());
            clonedLayoutState.constraints.setWidth(this.layoutState.constraints.getWidthConstraint());
            clonedLayoutState.constraints.setHeight(this.layoutState.constraints.getHeightConstraint());
            clonedLayoutState.margin = new EdgeInsets(this.layoutState.margin.top(), this.layoutState.margin.right(), this.layoutState.margin.bottom(), this.layoutState.margin.left());
            clonedLayoutState.padding = new EdgeInsets(this.layoutState.padding.top(), this.layoutState.padding.right(), this.layoutState.padding.bottom(), this.layoutState.padding.left());
            clonedLayoutState.setLayoutData(this.getLayoutData());

            // Copy EventState properties
            for (Map.Entry<EventType<?>, List<Consumer<?>>> entry : this.eventState.eventListeners.entrySet()) {
                clonedEventState.eventListeners.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }

            // Clone children
            clonedComponent.children = new ArrayList<>();
            for (Component<?> child : this.children) {
                Component<?> childClone = child.clone();
                clonedComponent.addChild(childClone);
            }

            // Copy RenderState properties
            clonedRenderState.rotation.set(this.getRotation());
            clonedRenderState.opacity.set(this.getOpacity());
            clonedRenderState.scaleX.set(this.getScaleX());
            clonedRenderState.scaleY.set(this.getScaleY());
            clonedRenderState.visible = this.renderState.visible;
            clonedRenderState.effects = new ArrayList<>(this.renderState.effects);

            // Copy other properties
            clonedComponent.style = this.style.clone();
            clonedComponent.updateClonedChildReferences(this);

            return clonedComponent;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Component is Cloneable but clone() failed", e); // Should not happen
        }
    }

    protected void updateClonedChildReferences(Component<T> original) {
        // Default implementation is empty.
    }

    public AnimationBuilder<T> animate() {
        return Animator.getBuilderFor(self());
    }

    public Constraints getConstraints() {
        return this.layoutState.constraints;
    }

    public EdgeInsets getMargin() {
        return this.layoutState.margin;
    }

    public T setMargin(float all) {
        this.layoutState.margin = new EdgeInsets(all);
        invalidateLayout();
        handleAutoMargins();
        return self();
    }

    public float getMeasuredWidth() {
        return this.layoutState.measuredWidth;
    }

    public float getMeasuredHeight() {
        return this.layoutState.measuredHeight;
    }

    public float getFinalWidth() {
        return this.layoutState.finalWidth;
    }

    public float getFinalHeight() {
        return this.layoutState.finalHeight;
    }

    public boolean isLayoutDirty() {
        return this.layoutState.layoutDirty;
    }

    public void measure(float availableWidth, float availableHeight) {
        if (!this.renderState.visible) {
            this.layoutState.measuredWidth = 0;
            this.layoutState.measuredHeight = 0;
            return;
        }

        WidthConstraint wc = this.layoutState.constraints.getWidthConstraint();
        HeightConstraint hc = this.layoutState.constraints.getHeightConstraint();

        boolean widthDependsOnChildren = wc instanceof tytoo.weave.constraint.constraints.ChildBasedSizeConstraint;
        boolean heightDependsOnChildren = hc instanceof tytoo.weave.constraint.constraints.ChildBasedSizeConstraint || hc instanceof tytoo.weave.constraint.constraints.SumOfChildrenHeightConstraint;

        if (widthDependsOnChildren || heightDependsOnChildren) {
            float horizontalPadding = this.layoutState.padding.left() + this.layoutState.padding.right();
            float verticalPadding = this.layoutState.padding.top() + this.layoutState.padding.bottom();
            for (Component<?> child : children) {
                child.measure(availableWidth - horizontalPadding, availableHeight - verticalPadding);
            }
        }

        float w, h;

        if (wc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint && !(hc instanceof tytoo.weave.constraint.constraints.AspectRatioConstraint)) {
            h = hc.calculateHeight(this, availableHeight);
            this.layoutState.measuredHeight = this.layoutState.constraints.clampHeight(h);
            w = wc.calculateWidth(this, availableWidth);
            this.layoutState.measuredWidth = this.layoutState.constraints.clampWidth(w);
        } else { // This also handles (hc is aspect, wc is not) and (neither is aspect)
            w = wc.calculateWidth(this, availableWidth);
            this.layoutState.measuredWidth = this.layoutState.constraints.clampWidth(w);
            h = hc.calculateHeight(this, availableHeight);
            this.layoutState.measuredHeight = this.layoutState.constraints.clampHeight(h);
        }

        if (!widthDependsOnChildren && !heightDependsOnChildren) {
            float horizontalPadding = this.layoutState.padding.left() + this.layoutState.padding.right();
            float verticalPadding = this.layoutState.padding.top() + this.layoutState.padding.bottom();
            for (Component<?> child : children) {
                child.measure(this.layoutState.measuredWidth - horizontalPadding, this.layoutState.measuredHeight - verticalPadding);
            }
        }
    }

    public void arrange(float x, float y) {
        if (!this.renderState.visible) return;

        this.layoutState.finalX = x;
        this.layoutState.finalY = y;
        this.layoutState.finalWidth = this.layoutState.measuredWidth + this.layoutState.margin.left() + this.layoutState.margin.right();
        this.layoutState.finalHeight = this.layoutState.measuredHeight + this.layoutState.margin.top() + this.layoutState.margin.bottom();

        if (this.layoutState.layout != null) {
            this.layoutState.layout.arrangeChildren(this);
        } else {
            for (Component<?> child : this.children) {
                if (!child.isVisible()) continue;
                float childX = child.getConstraints().getXConstraint().calculateX(child, getInnerWidth(), child.getMeasuredWidth() + child.getMargin().left() + child.getMargin().right());
                float childY = child.getConstraints().getYConstraint().calculateY(child, getInnerHeight(), child.getMeasuredHeight() + child.getMargin().top() + child.getMargin().bottom());
                child.arrange(this.getInnerLeft() + childX, this.getInnerTop() + childY);
            }
        }

        this.layoutState.layoutDirty = false;
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