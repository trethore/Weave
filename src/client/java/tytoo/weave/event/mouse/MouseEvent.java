package tytoo.weave.event.mouse;

import tytoo.weave.event.Event;

public abstract class MouseEvent extends Event {
    private final float x;
    private final float y;

    public MouseEvent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}