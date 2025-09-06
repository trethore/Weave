package tytoo.weave.ui.tooltip;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.animation.Animator;
import tytoo.weave.animation.Easings;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.WrappedTextComponent;
import tytoo.weave.component.components.layout.Panel;

import java.util.Optional;
import java.util.WeakHashMap;

public final class TooltipManager {
    private static final WeakHashMap<Screen, TooltipState> states = new WeakHashMap<>();

    private TooltipManager() {
    }

    private static TooltipState state(Screen screen) {
        return states.computeIfAbsent(screen, s -> new TooltipState());
    }

    public static void onClose(Screen screen) {
        states.remove(screen);
    }

    public static void onMouseMoved(Screen screen, double mouseX, double mouseY) {
        TooltipState s = state(screen);
        s.mouseX = (float) mouseX;
        s.mouseY = (float) mouseY;
        if (s.visible && s.followMouse && s.view != null) {
            positionTooltip(s, screen);
        }
    }

    public static void onHoverChanged(Screen screen, @Nullable Component<?> newHovered) {
        TooltipState s = state(screen);
        s.hoveredOwner = newHovered;
        scheduleForCurrentOwner(screen, s, false);
    }

    public static void onFocusChanged(Screen screen, @Nullable Component<?> newFocused) {
        TooltipState s = state(screen);
        s.focusedOwner = newFocused;
        scheduleForCurrentOwner(screen, s, true);
    }

    private static void scheduleForCurrentOwner(Screen screen, TooltipState s, boolean fromFocus) {
        Component<?> owner = fromFocus ? s.focusedOwner : s.hoveredOwner;
        if (owner == null) {
            if (!s.pinned) hide(s);
            return;
        }

        Component<?> effective = ownerWithAttachment(owner);
        TooltipAttachment attachment = effective != null ? findAttachment(effective) : null;
        if (attachment == null) {
            if (!s.pinned) hide(s);
            return;
        }

        if (s.activeOwner == effective) {
            if (s.visible) {
                positionTooltip(s, screen);
            }
            return;
        }

        s.activeOwner = effective;
        s.options = attachment.options();
        s.followMouse = s.options.isFollowMouse();
        s.nextShowAt = System.currentTimeMillis() + s.options.getDelayMs();
        s.pendingShow = true;
        s.pendingHide = false;

        if (s.view == null) {
            s.view = TooltipView.create();
        }
        buildContent(s, attachment);
        positionTooltip(s, screen);
    }

    private static void buildContent(TooltipState s, TooltipAttachment attachment) {
        s.view.removeAllChildren();
        Component<?> content = attachment.content();
        TooltipOptions opts = s.options != null ? s.options : new TooltipOptions();
        if (content instanceof WrappedTextComponent wtc) {
            wtc.setMaxWidth(opts.getMaxWidth());
        } else if (content != null && !(content instanceof Panel)) {
            content.setMaxWidth(opts.getMaxWidth());
        }
        s.view.addChild(content);
        s.view.invalidateLayout();
    }

    private static void show(Screen screen, TooltipState s) {
        if (s.visible) return;
        s.visible = true;
        s.view.setOpacity(0f);
        long fadeIn = s.options != null ? s.options.getFadeInMs() : new TooltipOptions().getFadeInMs();
        Animator.getBuilderFor(s.view)
                .duration(fadeIn)
                .easing(Easings.EASE_OUT_SINE)
                .opacity(1f);
        positionTooltip(s, screen);
    }

    private static void hide(TooltipState s) {
        if (!s.visible) return;
        s.pendingHide = false;
        Animator.getBuilderFor(s.view)
                .duration(s.options != null ? s.options.getFadeOutMs() : 100)
                .easing(Easings.EASE_OUT_SINE)
                .opacity(0f)
                .then(() -> hideNow(s));
    }

    private static void hideNow(TooltipState s) {
        s.visible = false;
        s.pendingShow = false;
        s.activeOwner = null;
        s.options = null;
        s.pinned = false;
    }

    public static void onRender(Screen screen, DrawContext context) {
        TooltipState s = state(screen);
        long now = System.currentTimeMillis();
        if (s.pendingShow && now >= s.nextShowAt && !s.visible) {
            show(screen, s);
        }

        if (s.visible && s.view != null) {
            s.view.draw(context);
        }

        Optional.ofNullable(s.activeOwner).ifPresent(owner -> {
            if (!s.pinned) {
                boolean invalid = !owner.isVisible();
                if (invalid) hide(s);
            }
        });
    }

    private static void positionTooltip(TooltipState s, Screen screen) {
        float screenW = (float) screen.width;
        float screenH = (float) screen.height;

        Component<?> owner = s.activeOwner != null ? s.activeOwner : s.hoveredOwner;
        if (owner == null) return;

        // Ensure we have fresh measurements before calculating placement
        s.view.measure(screenW, screenH);

        float tooltipW = s.view.getMeasuredWidth() + s.view.getMargin().left() + s.view.getMargin().right();
        float tooltipH = s.view.getMeasuredHeight() + s.view.getMargin().top() + s.view.getMargin().bottom();

        float x;
        float y;
        float gap = 6f;

        if (s.followMouse) {
            x = s.mouseX + 12f;
            y = s.mouseY + 12f;
        } else {
            TooltipOptions opts = s.options != null ? s.options : new TooltipOptions();
            switch (opts.getPlacement()) {
                case BOTTOM -> {
                    x = owner.getLeft();
                    y = owner.getTop() + owner.getHeight() + gap;
                }
                case LEFT -> {
                    x = owner.getLeft() - tooltipW - gap;
                    y = owner.getTop();
                }
                case RIGHT -> {
                    x = owner.getLeft() + owner.getWidth() + gap;
                    y = owner.getTop();
                }
                case TOP -> {
                    x = owner.getLeft();
                    y = owner.getTop() - tooltipH - gap;
                }
                default -> {
                    x = s.mouseX + 12f;
                    y = s.mouseY + 12f;
                }
            }
        }

        if (x + tooltipW > screenW) x = screenW - tooltipW - 2f;
        if (y + tooltipH > screenH) y = screenH - tooltipH - 2f;
        if (x < 0) x = 2f;
        if (y < 0) y = 2f;

        // Snap to integer pixels to avoid text jitter when following mouse
        x = Math.round(x);
        y = Math.round(y);

        s.view.arrange(x, y);
    }

    public static boolean onKeyPressed(Screen screen, int keyCode, @SuppressWarnings("unused") int modifiers) {
        TooltipState s = state(screen);
        if (!s.visible || s.view == null) return false;
        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            s.pinned = !s.pinned;
            if (!s.pinned) {
                s.pendingShow = false;
                s.pendingHide = true;
                hide(s);
            }
            return true;
        }
        return false;
    }

    public static void attach(Component<?> component, Component<?> content, TooltipOptions options) {
        component.setTooltipAttachment(new TooltipAttachment(content, options));
    }

    public static void attach(Component<?> component, Text text, TooltipOptions options) {
        WrappedTextComponent wrapped = WrappedTextComponent.of(text);
        wrapped.setMaxWidth(options.getMaxWidth());
        component.setTooltipAttachment(new TooltipAttachment(wrapped, options));
    }

    @Nullable
    public static TooltipAttachment getAttachment(Component<?> component) {
        return component.getTooltipAttachment();
    }

    @Nullable
    private static TooltipAttachment findAttachment(@Nullable Component<?> start) {
        for (Component<?> c = start; c != null; c = c.getParent()) {
            TooltipAttachment a = c.getTooltipAttachment();
            if (a != null) return a;
        }
        return null;
    }

    @Nullable
    private static Component<?> ownerWithAttachment(@Nullable Component<?> start) {
        for (Component<?> c = start; c != null; c = c.getParent()) {
            if (c.getTooltipAttachment() != null) return c;
        }
        return start;
    }

    public record TooltipAttachment(Component<?> content, TooltipOptions options) {
    }

    private static final class TooltipState {
        @Nullable Component<?> hoveredOwner;
        @Nullable Component<?> focusedOwner;
        @Nullable Component<?> activeOwner;
        @Nullable TooltipOptions options;
        boolean followMouse;
        boolean visible;
        boolean pinned;
        boolean pendingShow;
        boolean pendingHide;
        long nextShowAt;
        float mouseX;
        float mouseY;
        TooltipView view;
    }
}
