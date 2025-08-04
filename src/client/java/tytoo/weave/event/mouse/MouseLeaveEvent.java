package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.EventType;

public class MouseLeaveEvent extends MouseEvent {
    public static final EventType<MouseLeaveEvent> TYPE = new EventType<>();

    public MouseLeaveEvent(Component<?> target, float x, float y) {
        super(target, x, y);
    }

    @Override
    public EventType<MouseLeaveEvent> getType() {
        return TYPE;
    }
}