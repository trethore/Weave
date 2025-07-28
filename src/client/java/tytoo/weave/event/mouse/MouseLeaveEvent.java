package tytoo.weave.event.mouse;

import tytoo.weave.event.EventType;

public class MouseLeaveEvent extends MouseEvent {
    public static final EventType<MouseLeaveEvent> TYPE = new EventType<>();

    public MouseLeaveEvent(float x, float y) {
        super(x, y);
    }

    @Override
    public EventType<MouseLeaveEvent> getType() {
        return TYPE;
    }
}