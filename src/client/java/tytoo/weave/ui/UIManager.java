package tytoo.weave.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.animation.Animator;
import tytoo.weave.component.Component;
import tytoo.weave.component.RenderStage;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.CheckBox;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.component.components.interactive.RadioButton;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effect;
import tytoo.weave.event.Event;
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.*;
import tytoo.weave.profile.FrameProfiler;
import tytoo.weave.profile.FrameProfiler.EffectPhase;
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.popup.Anchor;
import tytoo.weave.ui.popup.PopupCloseEvent;
import tytoo.weave.ui.popup.PopupEntry;
import tytoo.weave.ui.popup.PopupOptions;
import tytoo.weave.ui.popup.PopupStyleProperties;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;
import tytoo.weave.ui.toast.ToastManager;
import tytoo.weave.ui.tooltip.TooltipManager;
import tytoo.weave.utils.McUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UIManager {
    private static final WeakHashMap<Screen, UIState> screenStates = new WeakHashMap<>();
    private static final boolean PERF_DEBUG_ENABLED = resolvePerfEnabled();
    private static final long PERF_LOG_INTERVAL_NANOS = resolveLongProperty("weave.debugPerfIntervalNanos", "WEAVE_DEBUG_PERF_INTERVAL_NANOS", 1_000_000_000L);
    private static final int PERF_LOG_FRAME_WINDOW = (int) resolveLongProperty("weave.debugPerfFrameWindow", "WEAVE_DEBUG_PERF_FRAME_WINDOW", 120L);
    @Nullable
    private static final FrameProfiler FRAME_PROFILER = PERF_DEBUG_ENABLED ? new FrameProfiler(PERF_LOG_FRAME_WINDOW, PERF_LOG_INTERVAL_NANOS) : null;

    static {
        if (PERF_DEBUG_ENABLED) {
            System.out.println("[WeavePerf] profiler enabled (window=" + PERF_LOG_FRAME_WINDOW + ", interval=" + PERF_LOG_INTERVAL_NANOS + "ns)");
        }
    }

    public static UIState getOrCreateState(Screen screen) {
        return screenStates.computeIfAbsent(screen, s -> new UIState());
    }

    public static Optional<UIState> getState(Screen screen) {
        return Optional.ofNullable(screenStates.get(screen));
    }

    public static boolean requestFocus(Screen screen, Component<?> component) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty()) return false;
        setFocusedComponent(stateOpt.get(), component);
        TooltipManager.onFocusChanged(screen, component);
        return true;
    }

    public static void setRoot(Screen screen, Component<?> root) {
        UIState state = getOrCreateState(screen);
        ensureScreenShortcuts(screen, state);
        state.setRoot(root);
        ensureOverlayRoot(state);
    }

    public static void clearFocus(Screen screen) {
        Optional<UIState> stateOpt = getState(screen);
        stateOpt.ifPresent(state -> setFocusedComponent(state, null));
        TooltipManager.onFocusChanged(screen, null);
    }

    public static void onRender(Screen screen, DrawContext context) {
        getState(screen).ifPresent(state -> {
            Stylesheet active = state.getStylesheetOverride() != null ? state.getStylesheetOverride() : ThemeManager.getTheme().getStylesheet();
            ThemeManager.pushActiveStylesheet(active);
            try {
                Component<?> root = state.getRoot();
                if (root == null) return;

                FrameProfiler profiler = FRAME_PROFILER;
                if (profiler != null) profiler.beginFrame();
                try {
                    ensureOverlayRoot(state);

                    if (root.isLayoutDirty()) {
                        McUtils.getMc().ifPresent(client -> {
                            float screenWidth = client.getWindow().getScaledWidth();
                            float screenHeight = client.getWindow().getScaledHeight();

                            long measureStart = profiler != null ? System.nanoTime() : 0L;
                            root.measure(screenWidth, screenHeight);
                            long measureEnd = profiler != null ? System.nanoTime() : 0L;

                            float widthWithMargin = root.getMeasuredWidth() + root.getMargin().left() + root.getMargin().right();
                            float heightWithMargin = root.getMeasuredHeight() + root.getMargin().top() + root.getMargin().bottom();
                            float rootX = root.getConstraints().getXConstraint().calculateX(root, screenWidth, widthWithMargin);
                            float rootY = root.getConstraints().getYConstraint().calculateY(root, screenHeight, heightWithMargin);

                            long arrangeStart = profiler != null ? measureEnd : 0L;
                            root.arrange(rootX, rootY);
                            if (profiler != null) {
                                profiler.recordMeasure(measureEnd - measureStart);
                                profiler.recordArrange(System.nanoTime() - arrangeStart);
                                profiler.incrementLayoutPass();
                            }
                        });
                    }

                    long toastStart = profiler != null ? System.nanoTime() : 0L;
                    ToastManager.updatePositions(screen);
                    if (profiler != null) profiler.recordToast(System.nanoTime() - toastStart);

                    long popupStart = profiler != null ? System.nanoTime() : 0L;
                    updatePopupPositions(screen, state);
                    if (profiler != null) profiler.recordPopup(System.nanoTime() - popupStart);

                    long animatorStart = profiler != null ? System.nanoTime() : 0L;
                    Animator.getInstance().update();
                    if (profiler != null) profiler.recordAnimator(System.nanoTime() - animatorStart);

                    long drawStart = profiler != null ? System.nanoTime() : 0L;
                    root.draw(context);
                    if (profiler != null) profiler.recordDraw(System.nanoTime() - drawStart);

                    long tooltipStart = profiler != null ? System.nanoTime() : 0L;
                    TooltipManager.onRender(screen, context);
                    if (profiler != null) profiler.recordTooltip(System.nanoTime() - tooltipStart);

                    Panel overlay = state.getOverlayRoot();
                    if (overlay != null && overlay.isVisible()) {
                        long overlayStart = profiler != null ? System.nanoTime() : 0L;
                        overlay.draw(context);
                        if (profiler != null) profiler.recordOverlay(System.nanoTime() - overlayStart);
                    }
                } finally {
                    if (profiler != null) profiler.endFrame();
                }
            } finally {
                ThemeManager.popActiveStylesheet();
            }
        });
    }

    public static void onComponentMeasured() {
        if (FRAME_PROFILER != null) {
            FRAME_PROFILER.onComponentMeasure();
        }
    }

    public static Object beginComponentDraw(Component<?> component) {
        FrameProfiler profiler = FRAME_PROFILER;
        if (profiler == null) {
            return null;
        }
        return profiler.beginComponentDraw(component);
    }

    public static void endComponentDraw(Component<?> component, Object token) {
        FrameProfiler profiler = FRAME_PROFILER;
        if (profiler != null) {
            profiler.endComponentDraw(component, token);
        }
    }

    public static boolean isProfilerActive() {
        return FRAME_PROFILER != null;
    }

    public static void recordComponentStage(Component<?> component, RenderStage stage, long nanos) {
        FrameProfiler profiler = FRAME_PROFILER;
        if (profiler != null) {
            profiler.recordComponentStage(component, stage, nanos);
        }
    }

    public static void recordEffect(Component<?> component, Effect effect, EffectPhase phase, long nanos) {
        FrameProfiler profiler = FRAME_PROFILER;
        if (profiler != null) {
            profiler.recordEffect(component, effect, phase, nanos);
        }
    }

    public static void onInit(Screen screen) {
        McUtils.getMc().ifPresent(client -> {
            double mouseX = client.mouse.getX() / client.getWindow().getScaleFactor();
            double mouseY = client.mouse.getY() / client.getWindow().getScaleFactor();
            updateHoveredComponent(screen, mouseX, mouseY);
        });
    }

    public static void onMouseMoved(Screen screen, double mouseX, double mouseY) {
        updateHoveredComponent(screen, mouseX, mouseY);
        TooltipManager.onMouseMoved(screen, mouseX, mouseY);
    }

    public static boolean onMouseClicked(Screen screen, double mouseX, double mouseY, int button) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty() || stateOpt.get().getRoot() == null) return false;
        UIState state = stateOpt.get();
        Component<?> root = state.getRoot();
        Component<?> target = root.hitTest((float) mouseX, (float) mouseY);
        if (target != null) {
            state.setClickedComponent(target);
            bubbleEvent(target, new MouseClickEvent(target, (float) mouseX, (float) mouseY, button), Component::fireEvent);

            Component<?> interactiveComponent = target;
            while (interactiveComponent != null && !interactiveComponent.isFocusable()) {
                interactiveComponent = interactiveComponent.getParent();
            }

            if (interactiveComponent != null && !interactiveComponent.getActiveStyleStates().contains(StyleState.DISABLED)) {
                interactiveComponent.setStyleState(StyleState.ACTIVE, true);
                state.setActiveComponent(interactiveComponent);
            }
            setFocusedComponent(state, interactiveComponent);
            TooltipManager.onFocusChanged(screen, interactiveComponent);
            return true;
        } else {
            setFocusedComponent(state, null);
            TooltipManager.onFocusChanged(screen, null);
            return false;
        }
    }

    public static boolean onMouseReleased(Screen screen, double mouseX, double mouseY, int button) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty()) return false;
        UIState state = stateOpt.get();

        Component<?> clickedComponent = state.getClickedComponent();
        if (clickedComponent != null) {
            bubbleEvent(clickedComponent, new MouseReleaseEvent(clickedComponent, (float) mouseX, (float) mouseY, button), Component::fireEvent);
            state.setClickedComponent(null);
        }

        Component<?> activeComponent = state.getActiveComponent();
        if (activeComponent != null) {
            activeComponent.setStyleState(StyleState.ACTIVE, false);
            state.setActiveComponent(null);
        }
        return clickedComponent != null;
    }

    public static boolean onMouseDragged(Screen screen, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty() || stateOpt.get().getClickedComponent() == null) return false;
        Component<?> target = stateOpt.get().getClickedComponent();
        bubbleEvent(target, new MouseDragEvent(target, (float) mouseX, (float) mouseY, deltaX, deltaY, button), Component::fireEvent);
        return true;
    }

    public static boolean onMouseScrolled(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty() || stateOpt.get().getRoot() == null) return false;

        Component<?> target = stateOpt.get().getRoot().hitTest((float) mouseX, (float) mouseY);
        if (target == null) return false;
        bubbleEvent(target, new MouseScrollEvent(target, (float) mouseX, (float) mouseY, horizontalAmount, verticalAmount), Component::fireEvent);
        updateHoveredComponent(screen, mouseX, mouseY);
        return true;
    }

    public static boolean onKeyPressed(Screen screen, int keyCode, int scanCode, int modifiers) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty()) return false;
        UIState state = stateOpt.get();
        ensureScreenShortcuts(screen, state);
        Component<?> focused = state.getFocusedComponent();
        if (focused != null) {
            KeyPressEvent event = new KeyPressEvent(keyCode, scanCode, modifiers);
            bubbleEvent(focused, event, Component::fireEvent);
            if (event.isCancelled()) return true;
        }

        return ShortcutRegistry.dispatch(screen, state, keyCode, modifiers);
    }

    public static boolean onCharTyped(Screen screen, char chr, int modifiers) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty() || stateOpt.get().getFocusedComponent() == null) return false;
        CharTypeEvent event = new CharTypeEvent(chr, modifiers);
        bubbleEvent(stateOpt.get().getFocusedComponent(), event, Component::fireEvent);
        return event.isCancelled();
    }

    private static void updateHoveredComponent(Screen screen, double mouseX, double mouseY) {
        getState(screen).ifPresent(state -> {
            if (state.getRoot() == null) return;
            Component<?> newHovered = state.getRoot().hitTest((float) mouseX, (float) mouseY);
            Component<?> oldHovered = state.getHoveredComponent();

            if (newHovered != oldHovered) {
                state.setHoveredComponent(newHovered);

                updateCursor(newHovered);
                if (oldHovered != null) {
                    bubbleEvent(oldHovered, new MouseLeaveEvent(oldHovered, (float) mouseX, (float) mouseY), Component::fireEvent);
                }
                if (newHovered != null) {
                    bubbleEvent(newHovered, new MouseEnterEvent(newHovered, (float) mouseX, (float) mouseY), Component::fireEvent);
                }
                TooltipManager.onHoverChanged(screen, newHovered);
            }
        });
    }

    private static void updateCursor(@Nullable Component<?> component) {
        Component<?> current = component;
        while (current != null) {
            if (current.getActiveStyleStates().contains(StyleState.DISABLED)) {
                CursorManager.setCursor(CursorType.NOT_ALLOWED);
                return;
            }
            current = current.getParent();
        }

        current = component;
        while (current != null) {
            CursorType cursorType = current.getCachedStyleValue(CommonStyleProperties.CURSOR, null);
            if (cursorType != null) {
                CursorManager.setCursor(cursorType);
                return;
            }
            current = current.getParent();
        }

        CursorManager.setCursor(CursorType.ARROW);
    }

    private static void setFocusedComponent(UIState state, @Nullable Component<?> component) {
        Component<?> oldFocused = state.getFocusedComponent();
        if (oldFocused == component) return;

        state.setFocusedComponent(component);

        if (oldFocused != null) {
            bubbleEvent(oldFocused, new FocusLostEvent(), Component::fireEvent);
        }
        if (component != null) {
            bubbleEvent(component, new FocusGainedEvent(), Component::fireEvent);
        }
    }

    @Nullable
    private static UIState findStateFor(Component<?> component) {
        if (component == null) return null;
        for (UIState state : screenStates.values()) {
            Component<?> root = state.getRoot();
            if (root == null) continue;
            for (Component<?> cur = component; cur != null; cur = cur.getParent()) {
                if (cur == root) {
                    return state;
                }
            }
        }
        return null;
    }

    private static Component<?> getFocusScopeRoot(Component<?> start, Component<?> fallbackRoot) {
        Component<?> current = start;
        Component<?> lastNonManaged = null;
        while (current != null) {
            if (!current.isManagedByLayout()) {
                lastNonManaged = current;
            }
            current = current.getParent();
        }
        return lastNonManaged != null ? lastNonManaged : fallbackRoot;
    }

    private static boolean isDisabledOrHidden(Component<?> c) {
        if (!c.isVisible()) return true;
        for (Component<?> cur = c; cur != null; cur = cur.getParent()) {
            if (cur.getActiveStyleStates().contains(StyleState.DISABLED)) return true;
        }
        return false;
    }

    private static void collectFocusable(Component<?> root, List<Component<?>> out) {
        if (root.isFocusable() && !isDisabledOrHidden(root)) {
            out.add(root);
        }
        for (Component<?> child : root.getChildren()) {
            if (!child.isVisible()) continue;
            collectFocusable(child, out);
        }
    }

    private static List<Component<?>> resolveFocusOrder(Component<?> scopeRoot) {
        List<Component<?>> all = new ArrayList<>();
        collectFocusable(scopeRoot, all);
        boolean hasPositive = false;
        for (Component<?> c : all) {
            if (c.getTabIndex() > 0) {
                hasPositive = true;
                break;
            }
        }
        if (hasPositive) {
            all.sort(Comparator.comparingInt(Component::getTabIndex));
        }
        return all;
    }

    private static boolean moveFocus(Screen screen, boolean backwards) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty()) return false;
        UIState state = stateOpt.get();
        Component<?> root = state.getRoot();
        if (root == null) return false;

        Component<?> focused = state.getFocusedComponent();
        Component<?> scopeRoot = focused != null ? getFocusScopeRoot(focused, root) : root;
        List<Component<?>> order = resolveFocusOrder(scopeRoot);
        if (order.isEmpty()) return false;

        int nextIndex;
        if (focused == null || !order.contains(focused)) {
            nextIndex = backwards ? order.size() - 1 : 0;
        } else {
            int idx = order.indexOf(focused);
            nextIndex = (idx + (backwards ? -1 : 1) + order.size()) % order.size();
        }

        setFocusedComponent(state, order.get(nextIndex));
        TooltipManager.onFocusChanged(screen, order.get(nextIndex));
        return true;
    }

    private static boolean defaultActivate(Component<?> focused) {
        if (!(focused instanceof Button || focused instanceof CheckBox || focused instanceof RadioButton || focused instanceof ComboBox)) {
            return false;
        }
        float x = focused.getLeft() + focused.getWidth() / 2.0f;
        float y = focused.getTop() + focused.getHeight() / 2.0f;
        MouseClickEvent down = new MouseClickEvent(focused, x, y, 0);
        MouseReleaseEvent up = new MouseReleaseEvent(focused, x, y, 0);
        bubbleEvent(focused, down, Component::fireEvent);
        bubbleEvent(focused, up, Component::fireEvent);
        return true;
    }

    private static void handleEscape(UIState state) {
        Component<?> focused = state.getFocusedComponent();
        Component<?> root = state.getRoot();
        if (focused == null || root == null) return;
        setFocusedComponent(state, null);
    }

    private static void ensureScreenShortcuts(Screen screen, UIState state) {
        if (state.isShortcutsInitialized()) return;
        state.setShortcutsInitialized(true);

        List<ShortcutRegistry.Registration> registrations = state.getShortcutRegistrations();

        registrations.add(ShortcutRegistry.registerForScreen(screen,
                ShortcutRegistry.Shortcut.of(ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_TAB),
                        ctx -> moveFocus(ctx.screen(), false))));

        registrations.add(ShortcutRegistry.registerForScreen(screen,
                ShortcutRegistry.Shortcut.of(ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_TAB)
                                .withModifiers(ShortcutRegistry.KeyChord.Modifier.SHIFT),
                        ctx -> moveFocus(ctx.screen(), true))));

        registerDefaultActivationShortcut(screen, state, GLFW.GLFW_KEY_ENTER);
        registerDefaultActivationShortcut(screen, state, GLFW.GLFW_KEY_KP_ENTER);
        registerDefaultActivationShortcut(screen, state, GLFW.GLFW_KEY_SPACE);

        registerTooltipToggleShortcut(screen, state, GLFW.GLFW_KEY_LEFT_ALT);
        registerTooltipToggleShortcut(screen, state, GLFW.GLFW_KEY_RIGHT_ALT);

        ShortcutRegistry.Shortcut escapeShortcut = ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_ESCAPE).allowingAnyAdditionalModifiers(),
                ctx -> {
                    if (tryCloseTopmostOnEsc(ctx.screen(), ctx.state())) {
                        return true;
                    }
                    handleEscape(ctx.state());
                    TooltipManager.onFocusChanged(ctx.screen(), null);
                    return false;
                }).withPriority(-100);
        registrations.add(ShortcutRegistry.registerForScreen(screen, escapeShortcut));
    }

    private static void registerDefaultActivationShortcut(Screen screen, UIState state, int keyCode) {
        ShortcutRegistry.Shortcut shortcut = ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(keyCode).allowingAnyAdditionalModifiers(),
                ctx -> {
                    Component<?> focused = ctx.state().getFocusedComponent();
                    if (focused == null) {
                        return false;
                    }
                    return defaultActivate(focused);
                }).withPriority(-100);
        state.getShortcutRegistrations().add(ShortcutRegistry.registerForScreen(screen, shortcut));
    }

    private static void registerTooltipToggleShortcut(Screen screen, UIState state, int keyCode) {
        ShortcutRegistry.Shortcut shortcut = ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(keyCode).allowingAnyAdditionalModifiers(),
                ctx -> TooltipManager.onKeyPressed(ctx.screen(), ctx.keyCode(), ctx.modifiers()));
        state.getShortcutRegistrations().add(ShortcutRegistry.registerForScreen(screen, shortcut));
    }

    private static <E extends Event> void bubbleEvent(Component<?> start, E event, BiConsumer<Component<?>, E> dispatcher) {
        for (Component<?> c = start; c != null; c = c.getParent()) {
            if (event.isCancelled()) break;
            dispatcher.accept(c, event);
        }
    }

    public static void onClose(Screen screen) {
        UIState removed = screenStates.remove(screen);
        if (removed != null) {
            removed.clearShortcutRegistrations();
            Component<?> root = removed.getRoot();
            if (root != null) {
                Animator.getInstance().stopAll(root);
            }
            CursorManager.setCursor(CursorType.ARROW);
        }
        TooltipManager.onClose(screen);
    }

    public static void invalidateAllStyles() {
        for (UIState state : screenStates.values()) {
            Component<?> root = state.getRoot();
            if (root != null) {
                root.invalidateSubtreeStyleCache();
            }
        }
    }

    public static PopupHandle openPopup(Component<?> content, Anchor anchor, PopupOptions options) {
        return McUtils.getMc().map(mc -> mc.currentScreen).map(screen -> openPopup(screen, content, anchor, options)).orElse(null);
    }

    public static PopupHandle openPopup(Screen screen, Component<?> content, Anchor anchor, PopupOptions options) {
        UIState state = getOrCreateState(screen);
        ensureOverlayRoot(state);
        Panel overlay = state.getOverlayRoot();
        if (overlay == null) return null;

        Component<?> priorFocus = state.getFocusedComponent();

        Panel finalMount = Panel.create();
        finalMount.setManagedByLayout(false);
        finalMount.setWidth(Constraints.childBased());
        finalMount.setHeight(Constraints.childBased());
        finalMount.addChild(content);
        if (options.isTrapFocus() || options.isCloseOnFocusLoss()) {
            finalMount.setFocusable(true);
        }

        Panel backdrop = null;
        if (options.isModal()) {
            backdrop = Panel.create();
            backdrop.setManagedByLayout(false);
            backdrop.setWidth(Constraints.relative(1.0f));
            backdrop.setHeight(Constraints.relative(1.0f));
            backdrop.addStyleClass("popup-backdrop");

            Stylesheet ss = ThemeManager.getStylesheet();
            Float opacity = ss.get(backdrop, PopupStyleProperties.BACKDROP_OPACITY, 0.4f);
            if (opacity != null) backdrop.setOpacity(Math.max(0.0f, Math.min(1.0f, opacity)));

            boolean clickThrough = Boolean.TRUE.equals(options.getClickThroughBackdrop()) || Boolean.TRUE.equals(ss.get(backdrop, PopupStyleProperties.BACKDROP_CLICK_THROUGH, false));
            backdrop.setHittable(!clickThrough);
            if (options.isCloseOnBackdropClick() && !clickThrough) {
                Panel finalBackdrop = backdrop;
                backdrop.onMouseClick(e -> closeTopmostForBackdrop(state, screen, finalBackdrop));
            }

            overlay.addChild(backdrop);
        }

        overlay.addChild(finalMount);

        PopupEntry entry = new PopupEntry(backdrop, finalMount, content, anchor, options, priorFocus);
        state.getPopups().add(entry);

        if (options.isTrapFocus()) {
            requestFocus(screen, finalMount);
        }

        if (options.isCloseOnFocusLoss()) {
            finalMount.onFocusLost(e -> {
                Component<?> newFocused = state.getFocusedComponent();
                if (newFocused != null) {
                    for (Component<?> cur = newFocused; cur != null; cur = cur.getParent()) {
                        if (cur == finalMount) {
                            return;
                        }
                    }
                }
                closePopup(new PopupHandle(screen, entry));
            });
        }

        return new PopupHandle(screen, entry);
    }

    private static void closeTopmostForBackdrop(UIState state, Screen screen, Panel backdrop) {
        List<PopupEntry> entries = state.getPopups();
        if (entries.isEmpty()) return;
        PopupEntry last = entries.getLast();
        if (last.backdrop() == backdrop) {
            closePopup(new PopupHandle(screen, last));
        }
    }

    public static void closePopup(PopupHandle handle) {
        if (handle == null) return;
        UIState state = screenStates.get(handle.screen);
        if (state == null) return;
        Panel overlay = state.getOverlayRoot();
        if (overlay == null) return;

        PopupEntry entry = handle.entry;
        if (!state.getPopups().contains(entry)) return;

        Panel backdrop = entry.backdrop();
        if (backdrop != null) overlay.removeChild(backdrop);
        overlay.removeChild(entry.mount());
        state.getPopups().remove(entry);

        Consumer<PopupCloseEvent> onClose = entry.options().getOnClose();
        if (onClose != null) {
            onClose.accept(new PopupCloseEvent(entry.content(), entry.anchor(), entry.options()));
        }

        Component<?> prior = entry.priorFocus();
        if (prior != null) {
            requestFocus(handle.screen, prior);
        }
    }

    private static void ensureOverlayRoot(UIState state) {
        Component<?> root = state.getRoot();
        if (root == null) return;
        Panel overlay = state.getOverlayRoot();
        if (overlay == null) {
            overlay = Panel.create();
            overlay.setManagedByLayout(false);
            overlay.setHittable(false);
            overlay.setX(Constraints.pixels(0f));
            overlay.setY(Constraints.pixels(0f));
            overlay.setWidth(Constraints.relative(1.0f));
            overlay.setHeight(Constraints.relative(1.0f));
            overlay.addStyleClass("overlay-root");
            state.setOverlayRoot(overlay);
            root.addChild(overlay);
        } else {
            List<Component<?>> children = root.getChildren();
            if (children.isEmpty() || children.getLast() != overlay) {
                root.removeChild(overlay);
                root.addChild(overlay);
            }
        }
    }

    private static void updatePopupPositions(Screen screen, UIState state) {
        if (state.getPopups().isEmpty()) return;
        Component<?> root = state.getRoot();
        if (root == null) return;

        float overlayOriginX = root.getInnerLeft();
        float overlayOriginY = root.getInnerTop();
        float overlayW = root.getInnerWidth();
        float overlayH = root.getInnerHeight();

        for (PopupEntry entry : state.getPopups()) {
            Panel mount = entry.mount();
            Anchor anchor = entry.anchor();
            PopupOptions opts = entry.options();

            Component<?> owner = anchor.target();
            if (owner == null || !owner.isVisible()) continue;

            mount.measure(overlayW, overlayH);
            float contentW = mount.getMeasuredWidth() + mount.getMargin().left() + mount.getMargin().right();
            float contentH = mount.getMeasuredHeight() + mount.getMargin().top() + mount.getMargin().bottom();

            float targetL = owner.getLeft() - overlayOriginX;
            float targetT = owner.getTop() - overlayOriginY;
            float targetW = owner.getWidth();
            float targetH = owner.getHeight();

            float gap = Math.max(0f, opts.getGap() + anchor.gap());

            float x;
            float y;

            switch (anchor.side()) {
                case BOTTOM -> {
                    y = targetT + targetH + gap;
                    x = switch (anchor.align()) {
                        case START -> targetL;
                        case CENTER -> targetL + (targetW - contentW) / 2.0f;
                        case END -> targetL + (targetW - contentW);
                    };
                    if (opts.isAutoFlip() && y + contentH > overlayH) {
                        y = targetT - contentH - gap;
                    }
                }
                case TOP -> {
                    y = targetT - contentH - gap;
                    x = switch (anchor.align()) {
                        case START -> targetL;
                        case CENTER -> targetL + (targetW - contentW) / 2.0f;
                        case END -> targetL + (targetW - contentW);
                    };
                    if (opts.isAutoFlip() && y < 0) {
                        y = targetT + targetH + gap;
                    }
                }
                case RIGHT -> {
                    x = targetL + targetW + gap;
                    y = switch (anchor.align()) {
                        case START -> targetT;
                        case CENTER -> targetT + (targetH - contentH) / 2.0f;
                        case END -> targetT + (targetH - contentH);
                    };
                    if (opts.isAutoFlip() && x + contentW > overlayW) {
                        x = targetL - contentW - gap;
                    }
                }
                case LEFT -> {
                    x = targetL - contentW - gap;
                    y = switch (anchor.align()) {
                        case START -> targetT;
                        case CENTER -> targetT + (targetH - contentH) / 2.0f;
                        case END -> targetT + (targetH - contentH);
                    };
                    if (opts.isAutoFlip() && x < 0) {
                        x = targetL + targetW + gap;
                    }
                }
                default -> {
                    x = targetL;
                    y = targetT + targetH + gap;
                }
            }

            x += anchor.offsetX();
            y += anchor.offsetY();

            if (x + contentW > overlayW) x = overlayW - contentW - 2f;
            if (y + contentH > overlayH) y = overlayH - contentH - 2f;
            if (x < 0) x = 2f;
            if (y < 0) y = 2f;

            float finalX = Math.round(overlayOriginX + x);
            float finalY = Math.round(overlayOriginY + y);
            mount.arrange(finalX, finalY);
        }
    }

    private static boolean tryCloseTopmostOnEsc(Screen screen, UIState state) {
        List<PopupEntry> entries = state.getPopups();
        if (entries.isEmpty()) return false;
        PopupEntry last = entries.getLast();
        if (last.options().isCloseOnEsc()) {
            closePopup(new PopupHandle(screen, last));
            return true;
        }
        return false;
    }

    public static void setStylesheetOverride(Screen screen, Stylesheet stylesheet) {
        UIState state = getOrCreateState(screen);
        state.setStylesheetOverride(stylesheet);
        invalidateAllStyles();
    }

    private static boolean resolvePerfEnabled() {
        if (Boolean.getBoolean("weave.debugPerf")) {
            return true;
        }
        String envValue = System.getenv("WEAVE_DEBUG_PERF");
        return Boolean.parseBoolean(envValue);
    }

    private static long resolveLongProperty(String propertyKey, String envKey, long defaultValue) {
        Long propertyValue = Long.getLong(propertyKey);
        if (propertyValue != null) {
            return propertyValue;
        }
        String envValue = System.getenv(envKey);
        if (envValue != null) {
            try {
                return Long.parseLong(envValue);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public record PopupHandle(Screen screen, PopupEntry entry) {
    }
}
