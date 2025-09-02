package tytoo.weave.component;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import tytoo.weave.animation.AnimationBuilder;
import tytoo.weave.animation.Animator;
import tytoo.weave.animation.Easing;
import tytoo.weave.animation.Interpolators;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.XConstraint;
import tytoo.weave.constraint.YConstraint;
import tytoo.weave.constraint.constraints.AspectRatioConstraint;
import tytoo.weave.constraint.constraints.ChildBasedSizeConstraint;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.constraint.constraints.SumOfChildrenHeightConstraint;
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
import tytoo.weave.style.*;
import tytoo.weave.style.renderer.ColorableRenderer;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.utils.McUtils;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


@SuppressWarnings("unused")
public abstract class Component<T extends Component<T>> implements Cloneable {
    protected final LayoutState layoutState;
    protected final RenderState renderState;
    protected final EventState eventState;
    protected final Set<String> styleClasses = new HashSet<>();
    private final Set<StyleState> activeStyleStates = EnumSet.noneOf(StyleState.class);
    private final List<StyleRule> localStyleRules = new ArrayList<>();
    private final Map<String, Object> styleVariables = new HashMap<>();
    protected Component<?> parent;
    protected List<Component<?>> children = new LinkedList<>();
    @Nullable
    protected String id;
    @Nullable
    protected TextRenderer textRenderer;
    @Nullable
    private ComponentStyle style;
    private Color lastBackgroundColor;
    private Color lastBorderColor;
    private Float lastBorderWidth;
    private Color lastOverlayBorderColor;
    private Float lastOverlayBorderWidth;
    private Float lastOverlayBorderRadius;
    private EdgeInsets lastPadding;
    private Color animatedBorderColor;
    private Float animatedBorderWidth;
    private Color animatedOverlayBorderColor;
    private Float animatedOverlayBorderWidth;
    private Float animatedOverlayBorderRadius;
    private EdgeInsets animatedPadding;

    public Component() {
        this.layoutState = new LayoutState(this);
        this.renderState = new RenderState(this);
        this.eventState = new EventState();
    }

    private static void updateNamedParts(Component<?> clone, Component<?> original, Map<Component<?>, Component<?>> originalToCloneMap) {
        for (Field field : clone.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(NamedPart.class)) {
                try {
                    field.setAccessible(true);
                    Object originalPart = field.get(original);
                    if (originalPart instanceof Component<?>) {
                        Component<?> clonedPart = originalToCloneMap.get(originalPart);
                        field.set(clone, clonedPart);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to update named child reference during clone", e);
                }
            }
        }
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

            ComponentRenderer renderer = getStyle().getRenderer(this);
            if (renderer != null) {
                renderer.render(context, this);
                if (renderer instanceof ColorableRenderer colorable) {
                    lastBackgroundColor = colorable.getColor();
                }
            }

            drawBorder(context);
            drawChildren(context);

            ComponentRenderer overlayRenderer = getStyle().getOverlayRenderer(this);
            if (overlayRenderer != null) overlayRenderer.render(context, this);
            drawOverlayBorder(context);

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

    public T onMouseRelease(Consumer<MouseReleaseEvent> listener) {
        return onEvent(MouseReleaseEvent.TYPE, listener);
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

    public T onEvent(Consumer<Event> listener) {
        return onEvent(Event.ANY, listener);
    }

    public <E extends Event> T onEvent(EventType<E> type, Consumer<E> listener) {
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
        if (this.style == null) {
            this.style = ThemeManager.getStylesheet().resolveStyleFor(this);
        }
        return style;
    }

    public T setStyle(ComponentStyle style) {
        this.style = style;
        if (this.style != null) {
            invalidateLayout();
        }
        return self();
    }

    public List<StyleRule> getLocalStyleRules() {
        return Collections.unmodifiableList(this.localStyleRules);
    }

    public T addLocalStyleRule(StyleRule rule) {
        if (rule != null) {
            this.localStyleRules.add(rule);
            invalidateSubtreeStyleCache();
        }
        return self();
    }

    public T addLocalStyleRules(Collection<StyleRule> rules) {
        if (rules != null && !rules.isEmpty()) {
            this.localStyleRules.addAll(rules);
            invalidateSubtreeStyleCache();
        }
        return self();
    }

    public T clearLocalStyles() {
        if (!this.localStyleRules.isEmpty()) {
            this.localStyleRules.clear();
            invalidateSubtreeStyleCache();
        }
        return self();
    }

    public T addLocalStylesheet(Stylesheet stylesheet) {
        if (stylesheet != null) {
            addLocalStyleRules(stylesheet.getRules());
        }
        return self();
    }

    public T addLocalStylesheet(java.util.function.Consumer<Stylesheet> builder) {
        if (builder != null) {
            Stylesheet s = new Stylesheet();
            builder.accept(s);
            addLocalStylesheet(s);
        }
        return self();
    }

    public void invalidateSubtreeStyleCache() {
        invalidateStyleCache();
        invalidateLayout();
        for (Component<?> child : this.children) {
            child.invalidateSubtreeStyleCache();
        }
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
        for (Component<?> child : children.reversed()) {
            if (!child.isVisible()) continue;
            boolean childIsOverlay = !child.isManagedByLayout();
            if (childIsOverlay) {
                Component<?> hit = child.hitTest(x, y);
                if (hit != null) {
                    return hit;
                }
            }
        }

        if (!isPointInside(x, y)) return null;

        for (Component<?> child : children.reversed()) {
            if (!child.isVisible()) continue;
            if (!child.isManagedByLayout()) continue;
            Component<?> hit = child.hitTest(x, y);
            if (hit != null) {
                return hit;
            }
        }
        return this.isHittable() ? this : null;
    }

    public boolean isFocusable() {
        return this.eventState.isFocusable();
    }

    public T setFocusable(boolean focusable) {
        this.eventState.setFocusable(focusable);
        return self();
    }

    public boolean isHittable() {
        return this.eventState.isHittable();
    }

    public T setHittable(boolean hittable) {
        this.eventState.setHittable(hittable);
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
            invalidateStyleCache();
            onStyleStateChanged();
            invalidateLayout();
        }
        return self();
    }

    private void invalidateStyleCache() {
        this.style = null;
        ThemeManager.getStylesheet().clearCache(this);
    }

    public T removeStyleState(StyleState state) {
        if (activeStyleStates.remove(state)) {
            invalidateStyleCache();
            onStyleStateChanged();
            invalidateLayout();
        }
        return self();
    }

    public T setStyleState(StyleState state, boolean enable) {
        return enable ? addStyleState(state) : removeStyleState(state);
    }

    protected void onStyleStateChanged() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        Long duration = stylesheet.get(this, CommonStyleProperties.TRANSITION_DURATION, 0L);
        if (duration == null || duration <= 0) return;

        Easing.EasingFunction easing = stylesheet.get(this, CommonStyleProperties.TRANSITION_EASING, Easing.EASE_OUT_SINE);
        ComponentRenderer renderer = getStyle().getRenderer(this);
        if (renderer instanceof ColorableRenderer colorable) {
            Color newColor = colorable.getColor();
            if (newColor != null && lastBackgroundColor != null && !newColor.equals(lastBackgroundColor)) {
                State<Color> state = new State<>(lastBackgroundColor);
                state.addListener(colorable::setColor);
                new AnimationBuilder<>(self()).duration(duration).easing(easing)
                        .animateProperty(state, newColor, Interpolators.COLOR, colorable::setColor, "background-color");
            }
        }

        Float toBorderWidth = stylesheet.get(this, LayoutStyleProperties.BORDER_WIDTH, 0.0f);
        if (lastBorderWidth == null) lastBorderWidth = toBorderWidth;
        if (toBorderWidth != null && lastBorderWidth != null && Math.abs(toBorderWidth - lastBorderWidth) > 0.001f) {
            State<Float> s = new State<>(lastBorderWidth);
            s.addListener(v -> animatedBorderWidth = v);
            new AnimationBuilder<>(self()).duration(duration).easing(easing)
                    .animateProperty(s, toBorderWidth, Interpolators.FLOAT, v -> animatedBorderWidth = v, "border-width")
                    .then(() -> {
                        animatedBorderWidth = null;
                        lastBorderWidth = toBorderWidth;
                    });
        }

        Color toBorderColor = stylesheet.get(this, LayoutStyleProperties.BORDER_COLOR, null);
        if (lastBorderColor == null) lastBorderColor = toBorderColor;
        if (toBorderColor != null && lastBorderColor != null && !toBorderColor.equals(lastBorderColor)) {
            State<Color> s = new State<>(lastBorderColor);
            s.addListener(c -> animatedBorderColor = c);
            new AnimationBuilder<>(self()).duration(duration).easing(easing)
                    .animateProperty(s, toBorderColor, Interpolators.COLOR, c -> animatedBorderColor = c, "border-color")
                    .then(() -> {
                        animatedBorderColor = null;
                        lastBorderColor = toBorderColor;
                    });
        }

        EdgeInsets toPadding = stylesheet.get(this, LayoutStyleProperties.PADDING, null);
        if (lastPadding == null) lastPadding = toPadding;
        if (toPadding != null && lastPadding != null && !lastPadding.equals(toPadding)) {
            State<EdgeInsets> s = new State<>(lastPadding);
            s.addListener(p -> {
                animatedPadding = p;
                invalidateLayout();
            });
            new AnimationBuilder<>(self()).duration(duration).easing(easing)
                    .animateProperty(s, toPadding, Interpolators.EDGE_INSETS, p -> {
                        animatedPadding = p;
                        invalidateLayout();
                    }, "padding")
                    .then(() -> {
                        animatedPadding = null;
                        lastPadding = toPadding;
                        invalidateLayout();
                    });
        }

        Float toOverlayBorderWidth = stylesheet.get(this, LayoutStyleProperties.OVERLAY_BORDER_WIDTH, 0.0f);
        if (lastOverlayBorderWidth == null) lastOverlayBorderWidth = toOverlayBorderWidth;
        if (toOverlayBorderWidth != null && lastOverlayBorderWidth != null && Math.abs(toOverlayBorderWidth - lastOverlayBorderWidth) > 0.001f) {
            State<Float> s = new State<>(lastOverlayBorderWidth);
            s.addListener(v -> animatedOverlayBorderWidth = v);
            new AnimationBuilder<>(self()).duration(duration).easing(easing)
                    .animateProperty(s, toOverlayBorderWidth, Interpolators.FLOAT, v -> animatedOverlayBorderWidth = v, "overlay-border-width")
                    .then(() -> {
                        animatedOverlayBorderWidth = null;
                        lastOverlayBorderWidth = toOverlayBorderWidth;
                    });
        }

        Color toOverlayBorderColor = stylesheet.get(this, LayoutStyleProperties.OVERLAY_BORDER_COLOR, null);
        if (lastOverlayBorderColor == null) lastOverlayBorderColor = toOverlayBorderColor;
        if (toOverlayBorderColor != null && lastOverlayBorderColor != null && !toOverlayBorderColor.equals(lastOverlayBorderColor)) {
            State<Color> s = new State<>(lastOverlayBorderColor);
            s.addListener(c -> animatedOverlayBorderColor = c);
            new AnimationBuilder<>(self()).duration(duration).easing(easing)
                    .animateProperty(s, toOverlayBorderColor, Interpolators.COLOR, c -> animatedOverlayBorderColor = c, "overlay-border-color")
                    .then(() -> {
                        animatedOverlayBorderColor = null;
                        lastOverlayBorderColor = toOverlayBorderColor;
                    });
        }

        Float toOverlayBorderRadius = stylesheet.get(this, LayoutStyleProperties.OVERLAY_BORDER_RADIUS, 0.0f);
        if (lastOverlayBorderRadius == null) lastOverlayBorderRadius = toOverlayBorderRadius;
        if (toOverlayBorderRadius != null && lastOverlayBorderRadius != null && Math.abs(toOverlayBorderRadius - lastOverlayBorderRadius) > 0.001f) {
            State<Float> s = new State<>(lastOverlayBorderRadius);
            s.addListener(v -> animatedOverlayBorderRadius = v);
            new AnimationBuilder<>(self()).duration(duration).easing(easing)
                    .animateProperty(s, toOverlayBorderRadius, Interpolators.FLOAT, v -> animatedOverlayBorderRadius = v, "overlay-border-radius")
                    .then(() -> {
                        animatedOverlayBorderRadius = null;
                        lastOverlayBorderRadius = toOverlayBorderRadius;
                    });
        }
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

            LayoutState clonedLayoutState = clonedComponent.getLayoutState();
            EventState clonedEventState = clonedComponent.getEventState();
            RenderState clonedRenderState = clonedComponent.getRenderState();

            clonedLayoutState.getConstraints().setX(this.layoutState.getConstraints().getXConstraint());
            clonedLayoutState.getConstraints().setY(this.layoutState.getConstraints().getYConstraint());
            clonedLayoutState.getConstraints().setWidth(this.layoutState.getConstraints().getWidthConstraint());
            clonedLayoutState.getConstraints().setHeight(this.layoutState.getConstraints().getHeightConstraint());
            clonedLayoutState.setMargin(new EdgeInsets(this.layoutState.getMargin().top(), this.layoutState.getMargin().right(), this.layoutState.getMargin().bottom(), this.layoutState.getMargin().left()));
            clonedLayoutState.setPadding(new EdgeInsets(this.layoutState.getPadding().top(), this.layoutState.getPadding().right(), this.layoutState.getPadding().bottom(), this.layoutState.getPadding().left()));
            clonedLayoutState.setLayoutData(this.getLayoutData());

            for (Map.Entry<EventType<?>, List<Consumer<?>>> entry : this.eventState.getEventListeners().entrySet()) {
                clonedEventState.getEventListeners().put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }

            Map<Component<?>, Component<?>> originalToCloneMap = Maps.newIdentityHashMap();
            clonedComponent.children = new LinkedList<>();
            for (Component<?> child : this.children) {
                Component<?> childClone = child.clone();
                clonedComponent.addChild(childClone);
                originalToCloneMap.put(child, childClone);
            }

            clonedRenderState.rotation.set(this.getRotation());
            clonedRenderState.opacity.set(this.getOpacity());
            clonedRenderState.scaleX.set(this.getScaleX());
            clonedRenderState.scaleY.set(this.getScaleY());
            clonedRenderState.setVisible(this.renderState.isVisible());
            clonedRenderState.setEffects(new ArrayList<>(this.renderState.getEffects()));

            clonedComponent.setStyle(this.style != null ? this.style.clone() : null);
            clonedComponent.id = this.id;
            clonedComponent.styleClasses.addAll(this.styleClasses);
            updateNamedParts(clonedComponent, this, originalToCloneMap);

            return clonedComponent;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Component is Cloneable but clone() failed", e);
        }
    }

    public AnimationBuilder<T> animate() {
        return Animator.getBuilderFor(self());
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    public T setId(@Nullable String id) {
        if (!Objects.equals(this.id, id)) {
            this.id = id;
            invalidateStyleCache();
        }
        return self();
    }

    public T addStyleClass(String styleClass) {
        if (this.styleClasses.add(styleClass)) {
            invalidateStyleCache();
        }
        return self();
    }

    public T removeStyleClass(String styleClass) {
        if (this.styleClasses.remove(styleClass)) {
            invalidateStyleCache();
        }
        return self();
    }

    public Set<String> getStyleClasses() {
        return Collections.unmodifiableSet(this.styleClasses);
    }

    public boolean hasStyleClass(String styleClass) {
        return this.styleClasses.contains(styleClass);
    }

    public boolean hasStyleState(StyleState state) {
        return this.activeStyleStates.contains(state);
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

    @SuppressWarnings("unchecked")
    public <V> V getVar(String name, V defaultValue) {
        Object v = this.styleVariables.get(name);
        if (v != null) return (V) v;
        return defaultValue;
    }

    public <V> T setVar(String name, V value) {
        this.styleVariables.put(name, value);
        invalidateSubtreeStyleCache();
        return self();
    }

    public T removeVar(String name) {
        if (this.styleVariables.remove(name) != null) {
            invalidateSubtreeStyleCache();
        }
        return self();
    }

    public Map<String, Object> getStyleVariables() {
        return this.styleVariables;
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

        applyLayoutStylesFromStylesheet();

        WidthConstraint wc = this.layoutState.getConstraints().getWidthConstraint();
        HeightConstraint hc = this.layoutState.getConstraints().getHeightConstraint();

        boolean widthDependsOnChildren = wc instanceof ChildBasedSizeConstraint;
        boolean heightDependsOnChildren = hc instanceof ChildBasedSizeConstraint || hc instanceof SumOfChildrenHeightConstraint;

        if (widthDependsOnChildren || heightDependsOnChildren) {
            float horizontalPadding = this.layoutState.getPadding().left() + this.layoutState.getPadding().right();
            float verticalPadding = this.layoutState.getPadding().top() + this.layoutState.getPadding().bottom();
            for (Component<?> child : children) {
                child.measure(availableWidth - horizontalPadding, availableHeight - verticalPadding);
            }
        }

        float w, h;

        if (wc instanceof AspectRatioConstraint && !(hc instanceof AspectRatioConstraint)) {
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

    private void applyLayoutStylesFromStylesheet() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        EdgeInsets stylePadding = stylesheet.get(this, LayoutStyleProperties.PADDING, null);
        EdgeInsets newPadding = animatedPadding != null ? animatedPadding : stylePadding;
        if (newPadding != null) {
            EdgeInsets current = this.layoutState.getPadding();
            if (!current.equals(newPadding)) {
                this.layoutState.setPadding(newPadding);
            }
            lastPadding = newPadding;
        }

        EdgeInsets newMargin = stylesheet.get(this, LayoutStyleProperties.MARGIN, null);
        if (newMargin != null) {
            EdgeInsets marginApplied = newMargin;

            float top = marginApplied.top();
            float right = marginApplied.right();
            float bottom = marginApplied.bottom();
            float left = marginApplied.left();

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

            EdgeInsets finalMargin = new EdgeInsets(top, right, bottom, left);
            EdgeInsets current = this.layoutState.getMargin();
            if (!current.equals(finalMargin)) {
                this.layoutState.setMargin(finalMargin);
            }
        }

        Float widthProp = stylesheet.get(this, LayoutStyleProperties.WIDTH, null);
        if (widthProp != null) {
            this.layoutState.getConstraints().setWidth(Constraints.pixels(widthProp));
        }
        Float heightProp = stylesheet.get(this, LayoutStyleProperties.HEIGHT, null);
        if (heightProp != null) {
            this.layoutState.getConstraints().setHeight(Constraints.pixels(heightProp));
        }
        Float minWidthProp = stylesheet.get(this, LayoutStyleProperties.MIN_WIDTH, null);
        if (minWidthProp != null) {
            this.layoutState.getConstraints().setMinWidth(minWidthProp);
        }
        Float maxWidthProp = stylesheet.get(this, LayoutStyleProperties.MAX_WIDTH, null);
        if (maxWidthProp != null) {
            this.layoutState.getConstraints().setMaxWidth(maxWidthProp);
        }
        Float minHeightProp = stylesheet.get(this, LayoutStyleProperties.MIN_HEIGHT, null);
        if (minHeightProp != null) {
            this.layoutState.getConstraints().setMinHeight(minHeightProp);
        }
        Float maxHeightProp = stylesheet.get(this, LayoutStyleProperties.MAX_HEIGHT, null);
        if (maxHeightProp != null) {
            this.layoutState.getConstraints().setMaxHeight(maxHeightProp);
        }
    }

    private void drawBorder(DrawContext context) {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        Float borderWidth = animatedBorderWidth != null ? animatedBorderWidth : stylesheet.get(this, LayoutStyleProperties.BORDER_WIDTH, 0.0f);
        if (borderWidth == null || borderWidth <= 0f) return;
        Color borderColor = animatedBorderColor != null ? animatedBorderColor : stylesheet.get(this, LayoutStyleProperties.BORDER_COLOR, null);
        if (borderColor == null) return;

        Float radius = stylesheet.get(this, LayoutStyleProperties.BORDER_RADIUS, 0.0f);
        if (radius != null && radius > 0f) {
            Render2DUtils.drawRoundedOutline(context, getLeft(), getTop(), getWidth(), getHeight(), radius, borderWidth, borderColor);
        } else {
            Render2DUtils.drawOutline(context, getLeft(), getTop(), getWidth(), getHeight(), borderWidth, borderColor, true);
        }
        lastBorderColor = borderColor;
        lastBorderWidth = borderWidth;
    }

    private void drawOverlayBorder(DrawContext context) {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        Float borderWidth = animatedOverlayBorderWidth != null ? animatedOverlayBorderWidth : stylesheet.get(this, LayoutStyleProperties.OVERLAY_BORDER_WIDTH, 0.0f);
        if (borderWidth == null || borderWidth <= 0f) return;
        Color borderColor = animatedOverlayBorderColor != null ? animatedOverlayBorderColor : stylesheet.get(this, LayoutStyleProperties.OVERLAY_BORDER_COLOR, null);
        if (borderColor == null) return;

        Float radius = animatedOverlayBorderRadius != null ? animatedOverlayBorderRadius : stylesheet.get(this, LayoutStyleProperties.OVERLAY_BORDER_RADIUS, 0.0f);
        if (radius != null && radius > 0f) {
            Render2DUtils.drawRoundedOutline(context, getLeft(), getTop(), getWidth(), getHeight(), radius, borderWidth, borderColor);
        } else {
            Render2DUtils.drawOutline(context, getLeft(), getTop(), getWidth(), getHeight(), borderWidth, borderColor, true);
        }
        lastOverlayBorderColor = borderColor;
        lastOverlayBorderWidth = borderWidth;
        lastOverlayBorderRadius = radius;
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

    public boolean isManagedByLayout() {
        return this.layoutState.isManaged();
    }

    public T setManagedByLayout(boolean managed) {
        this.layoutState.setManaged(managed);
        invalidateLayout();
        return self();
    }
}
