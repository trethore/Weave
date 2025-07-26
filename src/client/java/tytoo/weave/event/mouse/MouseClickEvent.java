package tytoo.weave.event.mouse;

public class MouseClickEvent extends MouseEvent {
    private final int button;

    public MouseClickEvent(float x, float y, int button) {
        super(x, y);
        this.button = button;
    }

    public int getButton() {
        return button;
    }
}