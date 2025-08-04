package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.EventType;

public class MouseEnterEvent extends MouseEvent {
    public static final EventType<MouseEnterEvent> TYPE = new EventType<>();

    public MouseEnterEvent(Component<?> target, float x, float y) {
        super(target, x, y);
    }

    @Override
    public EventType<MouseEnterEvent> getType() {
        return TYPE;
    }
}