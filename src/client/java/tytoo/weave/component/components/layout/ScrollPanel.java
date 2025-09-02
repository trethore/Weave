package tytoo.weave.component.components.layout;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.event.mouse.MouseReleaseEvent;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;
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

    public ScrollPanel() {
        this(createDefaultContentPanel());
        this.contentPanel.setHeight(Constraints.sumOfChildrenHeight(0, this.gap));
        this.contentPanel.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, this.gap));
    }

    public ScrollPanel(BasePanel<?> contentPanel) {
        this.setWidth(Constraints.relative(1.0f));
        this.setHeight(Constraints.relative(1.0f));

        this.addEffect(Effects.scissor());

        this.contentPanel = contentPanel;
        this.contentPanel
                .setX(Constraints.pixels(0))
                .setWidth(Constraints.relative(1.0f));
        super.addChild(this.contentPanel);

        this.contentPanel.setY((c, parentHeight, componentHeight) -> this.scrollY.get());

        this.onMouseScroll(event -> {
            float contentHeight = this.contentPanel.getFinalHeight();
            float viewHeight = this.getInnerHeight();

            if (contentHeight <= viewHeight) return;

            float newScroll = scrollY.get() + (float) (event.getScrollY() * this.scrollSpeed);
            float maxScroll = Math.min(0, -(contentHeight - viewHeight));
            scrollY.set(Math.max(maxScroll, Math.min(0, newScroll)));
            arrangeContent();
        });

        this.onMouseClick(this::handleMouseClick);
        this.onMouseDrag(this::handleMouseDrag);
        this.onMouseRelease(this::handleMouseRelease);
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
        this.scrollY.set(value);
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

            Color trackColor = ThemeManager.getStylesheet().get(this, StyleProps.TRACK_COLOR, this.scrollbarTrackColor);

            boolean thumbHovered = isMouseOverScrollbarThumb();
            Color thumbBase = ThemeManager.getStylesheet().get(this, StyleProps.THUMB_COLOR, this.scrollbarThumbColor);
            Color thumbHover = ThemeManager.getStylesheet().get(this, StyleProps.THUMB_COLOR_HOVERED, this.scrollbarThumbHoverColor);
            Color thumbActive = ThemeManager.getStylesheet().get(this, StyleProps.THUMB_COLOR_ACTIVE, this.scrollbarThumbActiveColor);
            Color thumbColor = this.draggingScrollbarThumb ? thumbActive : (thumbHovered ? thumbHover : thumbBase);

            Render2DUtils.drawRect(context, trackX, trackY, trackW, trackH, trackColor);
            Render2DUtils.drawRect(context, thumbX, thumbY, thumbW, thumbH, thumbColor);
        }
    }

    private boolean shouldShowVerticalScrollbar() {
        if (!this.verticalScrollbarEnabled) return false;
        float contentHeight = this.contentPanel.getFinalHeight();
        float viewHeight = this.getInnerHeight();
        return contentHeight > viewHeight && viewHeight > 0;
    }

    private float[] computeScrollbarGeometry() {
        float viewLeft = this.getInnerLeft();
        float viewTop = this.getInnerTop();
        float viewWidth = this.getInnerWidth();
        float viewHeight = this.getInnerHeight();
        float contentHeight = this.contentPanel.getFinalHeight();
        float effectiveScrollbarWidth = ThemeManager.getStylesheet().get(this, StyleProps.WIDTH, this.scrollbarWidth);
        float trackX = viewLeft + viewWidth - effectiveScrollbarWidth;

        float visibleRatio = Math.max(0f, Math.min(1f, viewHeight / Math.max(1f, contentHeight)));
        float thumbMinH = ThemeManager.getStylesheet().get(this, StyleProps.THUMB_MIN_HEIGHT, this.scrollbarThumbMinHeight);
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
        float viewHeight = this.getInnerHeight();
        float contentHeight = this.contentPanel.getFinalHeight();
        if (contentHeight <= viewHeight || viewHeight <= 0) return;

        float visibleRatio = Math.max(0f, Math.min(1f, viewHeight / Math.max(1f, contentHeight)));
        float thumbMinH = ThemeManager.getStylesheet().get(this, StyleProps.THUMB_MIN_HEIGHT, this.scrollbarThumbMinHeight);
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

    private boolean isMouseOverScrollbarThumb() {
        if (!shouldShowVerticalScrollbar()) return false;
        return McUtils.getMc().map(mc -> {
            double mx = mc.mouse.getX() / mc.getWindow().getScaleFactor();
            double my = mc.mouse.getY() / mc.getWindow().getScaleFactor();
            return isPointInsideScrollbarThumb((float) mx, (float) my);
        }).orElse(false);
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
            float effectiveScrollbarWidth = ThemeManager.getStylesheet().get(this, StyleProps.WIDTH, this.scrollbarWidth);
            float effectiveGap = ThemeManager.getStylesheet().get(this, StyleProps.GAP, this.scrollbarGap);
            float reserved = effectiveScrollbarWidth + effectiveGap;
            if (viewWidth - reserved < 0) reserved = Math.max(0, viewWidth);
            this.contentPanel.measure(viewWidth - reserved, viewHeight);
        }
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> WIDTH = new StyleProperty<>("scroll-panel.scrollbar.width", Float.class);
        public static final StyleProperty<Float> GAP = new StyleProperty<>("scroll-panel.scrollbar.gap", Float.class);
        public static final StyleProperty<Float> THUMB_MIN_HEIGHT = new StyleProperty<>("scroll-panel.scrollbar.thumbMinHeight", Float.class);
        public static final StyleProperty<Color> TRACK_COLOR = new StyleProperty<>("scroll-panel.scrollbar.trackColor", Color.class);
        public static final StyleProperty<Color> THUMB_COLOR = new StyleProperty<>("scroll-panel.scrollbar.thumbColor", Color.class);
        public static final StyleProperty<Color> THUMB_COLOR_HOVERED = new StyleProperty<>("scroll-panel.scrollbar.thumbColor.hovered", Color.class);
        public static final StyleProperty<Color> THUMB_COLOR_ACTIVE = new StyleProperty<>("scroll-panel.scrollbar.thumbColor.active", Color.class);
    }
}
