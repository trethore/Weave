package tytoo.weave.event.mouse;

public class MouseDragEvent extends MouseEvent {
    private final double deltaX;
    private final double deltaY;
    private final int button;

    public MouseDragEvent(float x, float y, double deltaX, double deltaY, int button) {
        super(x, y);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.button = button;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public int getButton() {
        return button;
    }
}