package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.EventType;

public class MouseReleaseEvent extends MouseEvent {
    public static final EventType<MouseReleaseEvent> TYPE = new EventType<>();
    private final int button;

    public MouseReleaseEvent(Component<?> target, float x, float y, int button) {
        super(target, x, y);
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    @Override
    public EventType<MouseReleaseEvent> getType() {
        return TYPE;
    }
}