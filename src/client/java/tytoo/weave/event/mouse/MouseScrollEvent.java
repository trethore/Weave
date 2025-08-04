package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.EventType;

public class MouseScrollEvent extends MouseEvent {
    public static final EventType<MouseScrollEvent> TYPE = new EventType<>();
    private final double scrollX;
    private final double scrollY;

    public MouseScrollEvent(Component<?> target, float x, float y, double scrollX, double scrollY) {
        super(target, x, y);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    @Override
    public EventType<MouseScrollEvent> getType() {
        return TYPE;
    }
}