package tytoo.weave.event.mouse;

import tytoo.weave.event.EventType;

public class MouseEnterEvent extends MouseEvent {
    public static final EventType<MouseEnterEvent> TYPE = new EventType<>();

    public MouseEnterEvent(float x, float y) {
        super(x, y);
    }

    @Override
    public EventType<MouseEnterEvent> getType() {
        return TYPE;
    }
}