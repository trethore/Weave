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
        Animator.getInstance().update();
        getState(screen).flatMap(state -> Optional.ofNullable(state.getRoot())).ifPresent(root -> root.draw(context));
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

        Component<?> target = root.isPointInside((float) mouseX, (float) mouseY) ? root.hitTest((float) mouseX, (float) mouseY) : null;
        if (target != null) {
            state.setClickedComponent(target);
            bubbleEvent(target, new MouseClickEvent((float) mouseX, (float) mouseY, button), Component::fireEvent);

            Component<?> componentToFocus = target;
            while (componentToFocus != null && !componentToFocus.isFocusable()) {
                componentToFocus = componentToFocus.getParent();
            }
            setFocusedComponent(state, componentToFocus);
            return true;
        } else {
            setFocusedComponent(state, null);
            return false;
        }
    }

    public static boolean onMouseReleased(Screen screen) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty()) return false;
        boolean hadClicked = stateOpt.get().getClickedComponent() != null;
        stateOpt.get().setClickedComponent(null);
        return hadClicked;
    }

    public static boolean onMouseDragged(Screen screen, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty() || stateOpt.get().getClickedComponent() == null) return false;
        bubbleEvent(stateOpt.get().getClickedComponent(), new MouseDragEvent((float) mouseX, (float) mouseY, deltaX, deltaY, button), Component::fireEvent);
        return true;
    }

    public static boolean onMouseScrolled(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Optional<UIState> stateOpt = getState(screen);
        if (stateOpt.isEmpty() || stateOpt.get().getHoveredComponent() == null) return false;
        bubbleEvent(stateOpt.get().getHoveredComponent(), new MouseScrollEvent((float) mouseX, (float) mouseY, horizontalAmount, verticalAmount), Component::fireEvent);
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
            Component<?> newHovered = state.getRoot().isPointInside((float) mouseX, (float) mouseY) ? state.getRoot().hitTest((float) mouseX, (float) mouseY) : null;
            if (newHovered != state.getHoveredComponent()) {
                if (state.getHoveredComponent() != null) {
                    bubbleEvent(state.getHoveredComponent(), new MouseLeaveEvent((float) mouseX, (float) mouseY), Component::fireEvent);
                }
                if (newHovered != null) {
                    bubbleEvent(newHovered, new MouseEnterEvent((float) mouseX, (float) mouseY), Component::fireEvent);
                }
                state.setHoveredComponent(newHovered);
            }
        });
    }

    private static void setFocusedComponent(UIState state, @Nullable Component<?> component) {
        if (state.getFocusedComponent() == component) return;
        if (state.getFocusedComponent() != null) {
            bubbleEvent(state.getFocusedComponent(), new FocusLostEvent(), Component::fireEvent);
        }
        state.setFocusedComponent(component);
        if (state.getFocusedComponent() != null) {
            bubbleEvent(state.getFocusedComponent(), new FocusGainedEvent(), Component::fireEvent);
        }
    }

    private static <E extends Event> void bubbleEvent(Component<?> start, E event, BiConsumer<Component<?>, E> dispatcher) {
        for (Component<?> c = start; c != null; c = c.getParent()) {
            if (event.isCancelled()) break;
            dispatcher.accept(c, event);
        }
    }
}