package tytoo.weave.event.mouse;

public class MouseScrollEvent extends MouseEvent {
    private final double scrollX;
    private final double scrollY;

    public MouseScrollEvent(float x, float y, double scrollX, double scrollY) {
        super(x, y);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }
}
