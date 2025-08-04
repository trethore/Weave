package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.EventType;

public class MouseDragEvent extends MouseEvent {
    public static final EventType<MouseDragEvent> TYPE = new EventType<>();
    private final double deltaX;
    private final double deltaY;
    private final int button;

    public MouseDragEvent(Component<?> target, float x, float y, double deltaX, double deltaY, int button) {
        super(target, x, y);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.button = button;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public int getButton() {
        return button;
    }

    @Override
    public EventType<MouseDragEvent> getType() {
        return TYPE;
    }
}