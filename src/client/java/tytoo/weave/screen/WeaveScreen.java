package tytoo.weave.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.event.Event;
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.*;

import java.util.function.BiConsumer;

public abstract class WeaveScreen extends Screen {
    private static WeaveScreen currentScreen;

    protected final Window window = new Window();

    private Component<?> hoveredComponent;
    private Component<?> clickedComponent;
    private Component<?> focusedComponent;

    protected WeaveScreen(Text title) {
        super(title);
        currentScreen = this;
    }

    @Nullable
    public static WeaveScreen getCurrentScreen() {
        return currentScreen;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        window.draw(context);
    }

    public Window getWindow() {
        return window;
    }

    @Override
    protected void init() {
        super.init();
        if (client != null) {
            this.mouseMoved(client.mouse.getX() / client.getWindow().getScaleFactor(), client.mouse.getY() / client.getWindow().getScaleFactor());
        }
    }

    @Override
    public void close() {
        super.close();
        currentScreen = null;
    }

    private <E extends Event> void bubbleEvent(Component<?> start, E event, BiConsumer<Component<?>, E> dispatcher) {
        for (Component<?> c = start; c != null; c = c.getParent()) {
            if (event.isCancelled()) {
                break;
            }
            dispatcher.accept(c, event);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        Component<?> newHovered = window.isPointInside((float) mouseX, (float) mouseY)
                ? window.hitTest((float) mouseX, (float) mouseY)
                : null;

        if (newHovered != hoveredComponent) {
            if (hoveredComponent != null) {
                bubbleEvent(hoveredComponent, new MouseLeaveEvent((float) mouseX, (float) mouseY), Component::fireMouseLeave);
            }
            if (newHovered != null) {
                bubbleEvent(newHovered, new MouseEnterEvent((float) mouseX, (float) mouseY), Component::fireMouseEnter);
            }
            hoveredComponent = newHovered;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        Component<?> target = window.isPointInside((float) mouseX, (float) mouseY)
                ? window.hitTest((float) mouseX, (float) mouseY)
                : null;

        if (target != null) {
            clickedComponent = target;
            bubbleEvent(target, new MouseClickEvent((float) mouseX, (float) mouseY, button), Component::fireMouseClick);

            Component<?> componentToFocus = target;
            while (componentToFocus != null && !componentToFocus.isFocusable()) {
                componentToFocus = componentToFocus.getParent();
            }
            setFocusedComponent(componentToFocus);
        } else {
            setFocusedComponent(null);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        clickedComponent = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (clickedComponent != null) {
            bubbleEvent(clickedComponent, new MouseDragEvent((float) mouseX, (float) mouseY, deltaX, deltaY, button), Component::fireMouseDrag);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (hoveredComponent != null) {
            bubbleEvent(hoveredComponent, new MouseScrollEvent((float) mouseX, (float) mouseY, horizontalAmount, verticalAmount), Component::fireMouseScroll);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null) {
            bubbleEvent(focusedComponent, new KeyPressEvent(keyCode, scanCode, modifiers), Component::fireKeyPress);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focusedComponent != null) {
            bubbleEvent(focusedComponent, new CharTypeEvent(chr, modifiers), Component::fireCharTyped);
        }
        return super.charTyped(chr, modifiers);
    }

    @Nullable
    public Component<?> getFocusedComponent() {
        return focusedComponent;
    }

    public void setFocusedComponent(@Nullable Component<?> component) {
        if (focusedComponent == component) return;

        if (focusedComponent != null) {
            bubbleEvent(focusedComponent, new FocusLostEvent(), Component::fireFocusLost);
        }

        focusedComponent = component;

        if (focusedComponent != null) {
            bubbleEvent(focusedComponent, new FocusGainedEvent(), Component::fireFocusGained);
        }
    }

    @Nullable
    public Component<?> getHoveredComponent() {
        return hoveredComponent;
    }
}