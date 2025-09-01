package tytoo.weave.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.animation.Animator;
import tytoo.weave.component.Component;
import tytoo.weave.event.Event;
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.*;
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.McUtils;

import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class UIManager {
    private static final WeakHashMap<Screen, UIState> screenStates = new WeakHashMap<>();

    public static UIState getOrCreateState(Screen screen) {
        return screenStates.computeIfAbsent(screen, s -> new UIState());
    }

    public static Optional<UIState> getState(Screen screen) {
        return Optional.ofNullable(screenStates.get(screen));
    }

    public static void setRoot(Screen screen, Component<?> root) {
        getOrCreateState(screen).setRoot(root);
    }

    public static void onRender(Screen screen, DrawContext context) {
        getState(screen).ifPresent(state -> {
            Component<?> root = state.getRoot();
            if (root == null) return;

            if (root.isLayoutDirty()) {
                McUtils.getMc().ifPresent(client -> {
                    float screenWidth = client.getWindow().getScaledWidth();
                    float screenHeight = client.getWindow().getScaledHeight();

                    root.measure(screenWidth, screenHeight);
                    float widthWithMargin = root.getMeasuredWidth() + root.getMargin().left() + root.getMargin().right();
                    float heightWithMargin = root.getMeasuredHeight() + root.getMargin().top() + root.getMargin().bottom();
                    float rootX = root.getConstraints().getXConstraint().calculateX(root, screenWidth, widthWithMargin);
                    float rootY = root.getConstraints().getYConstraint().calculateY(root, screenHeight, heightWithMargin);
                    root.arrange(rootX, rootY);
                });
            }

            Animator.getInstance().update();
            root.draw(context);
        });
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
            return true;
        } else {
            setFocusedComponent(state, null);
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
        if (stateOpt.isEmpty() || stateOpt.get().getFocusedComponent() == null) return false;
        KeyPressEvent event = new KeyPressEvent(keyCode, scanCode, modifiers);
        bubbleEvent(stateOpt.get().getFocusedComponent(), event, Component::fireEvent);
        return event.isCancelled();
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
            }
        });
    }

    private static void updateCursor(@Nullable Component<?> component) {
        Component<?> current = component;
        while (current != null) {
            if (current.getActiveStyleStates().contains(tytoo.weave.style.StyleState.DISABLED)) {
                CursorManager.setCursor(CursorType.NOT_ALLOWED);
                return;
            }
            current = current.getParent();
        }

        current = component;
        while (current != null) {
            CursorType cursorType = ThemeManager.getStylesheet().get(current, CommonStyleProperties.CURSOR, null);
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

    private static <E extends Event> void bubbleEvent(Component<?> start, E event, BiConsumer<Component<?>, E> dispatcher) {
        for (Component<?> c = start; c != null; c = c.getParent()) {
            if (event.isCancelled()) break;
            dispatcher.accept(c, event);
        }
    }

    public static void onClose(Screen screen) {
        if (screenStates.remove(screen) != null) {
            CursorManager.setCursor(CursorType.ARROW);
        }
    }

    public static void invalidateAllStyles() {
        for (UIState state : screenStates.values()) {
            Component<?> root = state.getRoot();
            if (root != null) {
                root.invalidateSubtreeStyleCache();
            }
        }
    }
}
