package tytoo.weave.component.components.layout;

import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.component.Component;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.event.mouse.MouseReleaseEvent;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.style.contract.ComponentStyleProperties;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;
import tytoo.weave.utils.McUtils;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class ScrollPanel extends BasePanel<ScrollPanel> {
    private final BasePanel<?> contentPanel;
    private final State<Float> scrollY = new State<>(0f);
    private final float scrollbarWidth = 6f;
    private final float scrollbarThumbMinHeight = 16f;
    private final Color scrollbarTrackColor = new Color(0, 0, 0, 80);
    private final Color scrollbarThumbColor = new Color(220, 220, 220, 200);
    private final Color scrollbarThumbHoverColor = new Color(235, 235, 235, 220);
    private final Color scrollbarThumbActiveColor = new Color(245, 245, 245, 230);
    private float scrollSpeed = 10f;
    private float gap = 2f;
    private boolean verticalScrollbarEnabled = false;
    private boolean draggingScrollbarThumb = false;
    private float scrollbarGap = 3f;
    private long lastArrowKeyTimeNs = 0L;
    private int arrowKeyStreak = 0;

    public ScrollPanel() {
        this(createDefaultContentPanel());
        this.contentPanel.setHeight(Constraints.sumOfChildrenHeight(0, this.gap));
        this.contentPanel.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, this.gap));
    }

    public ScrollPanel(BasePanel<?> contentPanel) {
        this.setWidth(Constraints.relative(1.0f));
        this.setHeight(Constraints.relative(1.0f));

        this.addEffect(Effects.scissor());
        this.setFocusable(true);

        this.contentPanel = contentPanel;
        this.contentPanel
                .setX(Constraints.pixels(0))
                .setWidth(Constraints.relative(1.0f));
        super.addChild(this.contentPanel);

        this.contentPanel.setY((c, parentHeight, componentHeight) -> this.scrollY.get());

        this.onMouseScroll(event -> {
            float contentHeight = getScrollableContentHeight();
            float viewHeight = getScrollableViewHeight();

            if (contentHeight <= viewHeight) return;

            float amount = ThemeManager.getStylesheet().get(this, CommonStyleProperties.SCROLL_AMOUNT, this.scrollSpeed);
            float newScroll = this.scrollY.get() + (float) (event.getScrollY() * amount);
            setScrollY(newScroll);
        });

        this.onMouseClick(this::handleMouseClick);
        this.onMouseDrag(this::handleMouseDrag);
        this.onMouseRelease(this::handleMouseRelease);

        registerComponentShortcut(panel -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_UP).allowingAnyAdditionalModifiers(),
                ctx -> panel.handleArrowShortcut(1)));

        registerComponentShortcut(panel -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_DOWN).allowingAnyAdditionalModifiers(),
                ctx -> panel.handleArrowShortcut(-1)));

        registerComponentShortcut(panel -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_PAGE_UP).allowingAnyAdditionalModifiers(),
                ctx -> panel.handlePageShortcut(true)));

        registerComponentShortcut(panel -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_PAGE_DOWN).allowingAnyAdditionalModifiers(),
                ctx -> panel.handlePageShortcut(false)));

        registerComponentShortcut(panel -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_HOME).allowingAnyAdditionalModifiers(),
                ctx -> panel.handleHomeShortcut()));

        registerComponentShortcut(panel -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_END).allowingAnyAdditionalModifiers(),
                ctx -> panel.handleEndShortcut()));
    }

    private static Panel createDefaultContentPanel() {
        return Panel.create();
    }

    public BasePanel<?> getContentPanel() {
        return contentPanel;
    }

    public float getScrollY() {
        return this.scrollY.get();
    }

    public ScrollPanel setScrollY(float value) {
        float clamped = clampScrollValue(value);
        this.scrollY.set(clamped);
        arrangeContent();
        return this;
    }

    public ScrollPanel setVerticalScrollbar(boolean enabled) {
        this.verticalScrollbarEnabled = enabled;
        if (!enabled) {
            this.draggingScrollbarThumb = false;
        }
        invalidateLayout();
        return this;
    }

    @Override
    public void addChild(Component<?> child) {
        this.contentPanel.addChild(child);
    }

    @Override
    public void removeChild(Component<?> child) {
        this.contentPanel.removeChild(child);
    }

    @Override
    public ScrollPanel addChildren(Component<?>... components) {
        this.contentPanel.addChildren(components);
        return this;
    }

    public ScrollPanel setGap(float gap) {
        this.gap = gap;
        if (this.contentPanel.getLayout() instanceof LinearLayout) {
            this.contentPanel.setHeight(Constraints.sumOfChildrenHeight(0, this.gap));
            this.contentPanel.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, this.gap));
        }
        return this;
    }

    public ScrollPanel setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        return this;
    }

    public ScrollPanel setScrollbarGap(float gap) {
        this.scrollbarGap = gap;
        invalidateLayout();
        return this;
    }

    @Override
    public Component<?> hitTest(float x, float y) {
        if (!isPointInside(x, y)) {
            return null;
        }

        if (shouldShowVerticalScrollbar()) {
            if (isPointInsideScrollbarThumb(x, y)) {
                return this;
            }
        }
        return super.hitTest(x, y);
    }

    private void arrangeContent() {
        if (this.contentPanel == null) return;
        clampScroll();
        float childWidthWithMargin = this.contentPanel.getMeasuredWidth() + this.contentPanel.getMargin().left() + this.contentPanel.getMargin().right();
        float childX = this.contentPanel.getConstraints().getXConstraint().calculateX(this.contentPanel, this.getInnerWidth(), childWidthWithMargin);
        float childY = this.scrollY.get();
        this.contentPanel.arrange(this.getInnerLeft() + childX, this.getInnerTop() + childY);
    }

    @Override
    public void draw(DrawContext context) {
        arrangeContent();
        super.draw(context);

        if (shouldShowVerticalScrollbar()) {
            float[] geom = computeScrollbarGeometry();
            float trackX = geom[0];
            float trackY = geom[1];
            float trackW = geom[2];
            float trackH = geom[3];
            float thumbX = geom[4];
            float thumbY = geom[5];
            float thumbW = geom[6];
            float thumbH = geom[7];

            Color trackColor = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.TRACK_COLOR, this.scrollbarTrackColor);

            boolean thumbHovered = isMouseOverScrollbarThumb();
            Color thumbBase = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.THUMB_COLOR, this.scrollbarThumbColor);
            Color thumbHover = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.THUMB_COLOR_HOVERED, this.scrollbarThumbHoverColor);
            Color thumbActive = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.THUMB_COLOR_ACTIVE, this.scrollbarThumbActiveColor);
            Color thumbColor = this.draggingScrollbarThumb ? thumbActive : (thumbHovered ? thumbHover : thumbBase);

            Render2DUtils.drawRect(context, trackX, trackY, trackW, trackH, trackColor);
            Render2DUtils.drawRect(context, thumbX, thumbY, thumbW, thumbH, thumbColor);
        }
    }

    private boolean shouldShowVerticalScrollbar() {
        if (!this.verticalScrollbarEnabled) return false;
        float contentHeight = getScrollableContentHeight();
        float viewHeight = getScrollableViewHeight();
        return contentHeight > viewHeight && viewHeight > 0;
    }

    private float[] computeScrollbarGeometry() {
        float viewLeft = this.getInnerLeft();
        float viewTop = this.getInnerTop();
        float viewWidth = this.getInnerWidth();
        float viewHeight = getScrollableViewHeight();
        float contentHeight = getScrollableContentHeight();
        float effectiveScrollbarWidth = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.WIDTH, this.scrollbarWidth);
        float trackX = viewLeft + viewWidth - effectiveScrollbarWidth;

        float visibleRatio = Math.max(0f, Math.min(1f, viewHeight / Math.max(1f, contentHeight)));
        float thumbMinH = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.THUMB_MIN_HEIGHT, this.scrollbarThumbMinHeight);
        float thumbH = Math.max(thumbMinH, viewHeight * visibleRatio);
        if (thumbH > viewHeight) thumbH = viewHeight;

        float maxScroll = Math.min(0, -(contentHeight - viewHeight));
        float range = -maxScroll;
        float progress = range <= 0.0001f ? 0f : (-this.scrollY.get()) / range;
        progress = Math.max(0f, Math.min(1f, progress));

        float thumbY = viewTop + (viewHeight - thumbH) * progress;

        return new float[]{trackX, viewTop, effectiveScrollbarWidth, viewHeight, trackX, thumbY, effectiveScrollbarWidth, thumbH};
    }

    private boolean isPointInsideScrollbarThumb(float x, float y) {
        float[] geom = computeScrollbarGeometry();
        float thumbX = geom[4];
        float thumbY = geom[5];
        float thumbW = geom[6];
        float thumbH = geom[7];
        return x >= thumbX && x <= thumbX + thumbW && y >= thumbY && y <= thumbY + thumbH;
    }

    private void handleMouseClick(MouseClickEvent event) {
        if (!shouldShowVerticalScrollbar()) return;
        if (!isPointInsideScrollbarThumb(event.getX(), event.getY())) return;
        this.draggingScrollbarThumb = true;
        updateScrollFromMouseY(event.getY());
        event.cancel();
    }

    private void handleMouseDrag(MouseDragEvent event) {
        if (!this.draggingScrollbarThumb) return;
        updateScrollFromMouseY(event.getY());
        event.cancel();
    }

    private void handleMouseRelease(MouseReleaseEvent event) {
        if (!this.draggingScrollbarThumb) return;
        this.draggingScrollbarThumb = false;
        event.cancel();
    }

    private void updateScrollFromMouseY(float mouseY) {
        float viewTop = this.getInnerTop();
        float viewHeight = getScrollableViewHeight();
        float contentHeight = getScrollableContentHeight();
        if (contentHeight <= viewHeight || viewHeight <= 0) return;

        float visibleRatio = Math.max(0f, Math.min(1f, viewHeight / Math.max(1f, contentHeight)));
        float thumbMinH = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.THUMB_MIN_HEIGHT, this.scrollbarThumbMinHeight);
        float thumbH = Math.max(thumbMinH, viewHeight * visibleRatio);
        if (thumbH > viewHeight) thumbH = viewHeight;

        float rangePx = Math.max(0f, viewHeight - thumbH);
        float relative = mouseY - viewTop - (thumbH / 2f);
        float progress = rangePx <= 0.0001f ? 0f : (relative / rangePx);
        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        float rangeScroll = contentHeight - viewHeight;
        float newScroll = -progress * rangeScroll;
        float maxScroll = Math.min(0, -(contentHeight - viewHeight));
        if (newScroll < maxScroll) newScroll = maxScroll;
        if (newScroll > 0) newScroll = 0;
        setScrollY(newScroll);
    }

    private float getScrollableContentHeight() {
        float finalHeight = this.contentPanel.getFinalHeight();
        if (finalHeight > 0f) {
            return finalHeight;
        }
        float measuredHeight = this.contentPanel.getMeasuredHeight();
        float topMargin = this.contentPanel.getMargin().top();
        float bottomMargin = this.contentPanel.getMargin().bottom();
        return Math.max(0f, measuredHeight + topMargin + bottomMargin);
    }

    private float getScrollableViewHeight() {
        float innerHeight = this.getInnerHeight();
        if (innerHeight > 0f) {
            return innerHeight;
        }
        float measuredHeight = this.getLayoutState().getMeasuredHeight();
        float paddingTop = this.getLayoutState().getPadding().top();
        float paddingBottom = this.getLayoutState().getPadding().bottom();
        return Math.max(0f, measuredHeight - paddingTop - paddingBottom);
    }

    private float clampScrollValue(float target) {
        float viewHeight = getScrollableViewHeight();
        float contentHeight = getScrollableContentHeight();
        if (viewHeight <= 0f || contentHeight <= viewHeight) {
            return 0f;
        }
        float maxScroll = -(contentHeight - viewHeight);
        if (target < maxScroll) {
            return maxScroll;
        }
        if (target > 0f) {
            return 0f;
        }
        return target;
    }

    private void clampScroll() {
        float clamped = clampScrollValue(this.scrollY.get());
        if (clamped != this.scrollY.get()) {
            this.scrollY.set(clamped);
        }
    }

    private boolean isMouseOverScrollbarThumb() {
        if (!shouldShowVerticalScrollbar()) return false;
        return McUtils.getMc().map(mc -> {
            double mx = mc.mouse.getX() / mc.getWindow().getScaleFactor();
            double my = mc.mouse.getY() / mc.getWindow().getScaleFactor();
            return isPointInsideScrollbarThumb((float) mx, (float) my);
        }).orElse(false);
    }

    private float computeKeyboardStepMagnitude() {
        int steps = computeArrowSteps();
        float base = computeBaseKeyboardStep();
        return base * steps;
    }

    private float computeBaseKeyboardStep() {
        float base = ThemeManager.getStylesheet().get(this, CommonStyleProperties.SCROLL_AMOUNT, this.scrollSpeed);
        if (base <= 0f) {
            base = this.scrollSpeed > 0f ? this.scrollSpeed : 10f;
        }
        return base;
    }

    private boolean scrollByKeyboard(float delta) {
        if (delta == 0f) return false;
        float newScroll = this.scrollY.get() + delta;
        float clamped = clampScrollValue(newScroll);
        if (clamped == this.scrollY.get()) return false;
        this.scrollY.set(clamped);
        arrangeContent();
        return true;
    }

    private int computeArrowSteps() {
        long now = System.nanoTime();
        long deltaNs = now - this.lastArrowKeyTimeNs;
        if (deltaNs <= 300_000_000L) {
            if (this.arrowKeyStreak < 30) {
                this.arrowKeyStreak++;
            }
        } else {
            this.arrowKeyStreak = 0;
        }
        this.lastArrowKeyTimeNs = now;

        int tier = this.arrowKeyStreak / 3;
        if (tier <= 0) return 1;
        return 1 << Math.min(tier, 6);
    }

    private void resetArrowKeyAcceleration() {
        this.arrowKeyStreak = 0;
        this.lastArrowKeyTimeNs = 0L;
    }

    private boolean canScroll() {
        if (!this.verticalScrollbarEnabled) return false;
        float contentHeight = getScrollableContentHeight();
        float viewHeight = getScrollableViewHeight();
        return contentHeight > viewHeight;
    }

    private boolean handleArrowShortcut(int direction) {
        if (!canScroll()) return false;
        float delta = computeKeyboardStepMagnitude() * direction;
        return scrollByKeyboard(delta);
    }

    private boolean handlePageShortcut(boolean upwards) {
        if (!canScroll()) return false;
        resetArrowKeyAcceleration();
        float viewHeight = getScrollableViewHeight();
        float step = Math.max(viewHeight * 0.9f, computeBaseKeyboardStep());
        return scrollByKeyboard(upwards ? step : -step);
    }

    private boolean handleHomeShortcut() {
        if (!canScroll()) return false;
        resetArrowKeyAcceleration();
        if (this.scrollY.get() == 0f) {
            return false;
        }
        setScrollY(0f);
        return true;
    }

    private boolean handleEndShortcut() {
        if (!canScroll()) return false;
        resetArrowKeyAcceleration();
        float contentHeight = getScrollableContentHeight();
        float viewHeight = getScrollableViewHeight();
        float maxScroll = -(contentHeight - viewHeight);
        if (this.scrollY.get() == maxScroll) {
            return false;
        }
        setScrollY(maxScroll);
        return true;
    }

    @Override
    public void measure(float availableWidth, float availableHeight) {
        super.measure(availableWidth, availableHeight);

        if (!this.verticalScrollbarEnabled) return;

        float horizontalPadding = this.getLayoutState().getPadding().left() + this.getLayoutState().getPadding().right();
        float verticalPadding = this.getLayoutState().getPadding().top() + this.getLayoutState().getPadding().bottom();

        float viewWidth = this.getLayoutState().getMeasuredWidth() - horizontalPadding;
        float viewHeight = this.getLayoutState().getMeasuredHeight() - verticalPadding;

        float contentMeasuredHeight = this.contentPanel.getMeasuredHeight() + this.contentPanel.getMargin().top() + this.contentPanel.getMargin().bottom();
        boolean needsScrollbar = contentMeasuredHeight > viewHeight && viewHeight > 0;

        if (needsScrollbar) {
            float effectiveScrollbarWidth = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.WIDTH, this.scrollbarWidth);
            float effectiveGap = ThemeManager.getStylesheet().get(this, ComponentStyleProperties.ScrollPanelStyles.GAP, this.scrollbarGap);
            float reserved = effectiveScrollbarWidth + effectiveGap;
            if (viewWidth - reserved < 0) reserved = Math.max(0, viewWidth);
            this.contentPanel.measure(viewWidth - reserved, viewHeight);
        }
    }

}
