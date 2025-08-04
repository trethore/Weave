package tytoo.weave.event.mouse;

import tytoo.weave.component.Component;
import tytoo.weave.event.Event;

public abstract class MouseEvent extends Event {
    private final Component<?> target;
    private final float x;
    private final float y;

    public MouseEvent(Component<?> target, float x, float y) {
        this.target = target;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Component<?> getTarget() {
        return target;
    }
}