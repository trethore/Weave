package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.EventType;

public class MouseClickEvent extends MouseEvent {
    public static final EventType<MouseClickEvent> TYPE = new EventType<>();
    private final int button;

    public MouseClickEvent(Component<?> target, float x, float y, int button) {
        super(target, x, y);
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    @Override
    public EventType<MouseClickEvent> getType() {
        return TYPE;
    }
}