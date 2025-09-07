package tytoo.weave.ui.toast;

import net.minecraft.client.gui.screen.Screen;
import tytoo.weave.animation.Animator;
import tytoo.weave.animation.Easings;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.UIState;
import tytoo.weave.utils.McUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;

public final class ToastManager {
    private static final WeakHashMap<Screen, List<ToastEntry>> entries = new WeakHashMap<>();

    private ToastManager() {
    }

    public static void show(String message) {
        show(SimpleTextComponent.of(message), new ToastOptions());
    }

    public static void show(String message, ToastOptions options) {
        show(SimpleTextComponent.of(message), options);
    }

    public static void show(Component<?> content) {
        show(content, new ToastOptions());
    }

    public static void show(Component<?> content, ToastOptions options) {
        Optional<Screen> screenOpt = McUtils.getMc().map(mc -> mc.currentScreen);
        if (screenOpt.isEmpty()) return;
        Screen screen = screenOpt.get();
        UIManager.getOrCreateState(screen);

        Panel overlay = UIManager.getState(screen).map(UIState::getOverlayRoot).orElse(null);
        if (overlay == null) return;

        ToastView view = ToastView.create();
        view.setWidth(Constraints.childBased(8f));
        view.setHeight(Constraints.childBased(8f));
        view.setPadding(6f, 8f);
        view.addChild(content);
        view.onMouseClick(e -> dismiss(screen, view, options));

        overlay.addChild(view);

        entries.computeIfAbsent(screen, s -> new ArrayList<>()).add(new ToastEntry(view, options));

        layoutToasts(screen);

        Animator.getBuilderFor(view)
                .duration(options.getFadeInMs())
                .easing(Easings.EASE_OUT_SINE)
                .opacity(1f)
                .then(() -> Animator.getBuilderFor(view)
                        .duration(options.getDurationMs())
                        .easing(Easings.LINEAR)
                        .then(() -> dismiss(screen, view, options))
                );
    }

    private static void dismiss(Screen screen, ToastView view, ToastOptions options) {
        Animator.getBuilderFor(view)
                .duration(options.getFadeOutMs())
                .easing(Easings.EASE_OUT_SINE)
                .opacity(0f)
                .then(() -> removeNow(screen, view));
    }

    private static void removeNow(Screen screen, ToastView view) {
        List<ToastEntry> list = entries.get(screen);
        if (list != null) list.removeIf(te -> te.view == view);
        UIManager.getState(screen).map(UIState::getOverlayRoot).ifPresent(overlay -> overlay.removeChild(view));
        layoutToasts(screen);
    }

    public static void updatePositions(Screen screen) {
        layoutToasts(screen);
    }

    private static void layoutToasts(Screen screen) {
        List<ToastEntry> list = entries.get(screen);
        if (list == null || list.isEmpty()) return;

        Panel overlay = UIManager.getState(screen).map(UIState::getOverlayRoot).orElse(null);
        if (overlay == null) return;

        float overlayOriginX = overlay.getInnerLeft();
        float overlayOriginY = overlay.getInnerTop();
        float screenW = screen.width;
        float screenH = screen.height;

        for (ToastEntry te : list) {
            te.view.measure(screenW, screenH);
        }

        float topRightY = 0f, topLeftY = 0f, bottomRightY = 0f, bottomLeftY = 0f;

        for (ToastEntry te : list) {
            ToastOptions opts = te.options;
            float margin = opts.getMargin();
            float gap = opts.getGap();

            float w = te.view.getMeasuredWidth() + te.view.getMargin().left() + te.view.getMargin().right();
            float h = te.view.getMeasuredHeight() + te.view.getMargin().top() + te.view.getMargin().bottom();

            float x = 0f;
            float y = 0f;

            switch (opts.getPosition()) {
                case TOP_RIGHT -> {
                    x = screenW - margin - w;
                    y = margin + topRightY;
                    topRightY += h + gap;
                }
                case TOP_LEFT -> {
                    x = margin;
                    y = margin + topLeftY;
                    topLeftY += h + gap;
                }
                case BOTTOM_RIGHT -> {
                    x = screenW - margin - w;
                    y = screenH - margin - h - bottomRightY;
                    bottomRightY += h + gap;
                }
                case BOTTOM_LEFT -> {
                    x = margin;
                    y = screenH - margin - h - bottomLeftY;
                    bottomLeftY += h + gap;
                }
            }

            float finalX = Math.round(x);
            float finalY = Math.round(y);
            float relX = finalX - overlayOriginX;
            float relY = finalY - overlayOriginY;

            // Only update constraints if they changed to avoid re-layout churn
            var xc = te.view.getConstraints().getXConstraint();
            var yc = te.view.getConstraints().getYConstraint();
            boolean xChanged = !(xc instanceof tytoo.weave.constraint.constraints.PixelConstraint(
                    float value
            ) && value == relX);
            boolean yChanged = !(yc instanceof tytoo.weave.constraint.constraints.PixelConstraint(
                    float value
            ) && value == relY);
            if (xChanged) te.view.setX(Constraints.pixels(relX));
            if (yChanged) te.view.setY(Constraints.pixels(relY));

            // Ensure toasts draw above other overlay children
            te.view.bringToFront();

            // Snap immediately for this frame even if parent won't re-arrange now
            te.view.arrange(finalX, finalY);
        }
    }

    private record ToastEntry(ToastView view, ToastOptions options) {
    }
}
