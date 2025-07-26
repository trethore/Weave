package tytoo.weave.event.keyboard;

public class KeyPressEvent extends KeyEvent {
    private final int keyCode;
    private final int scanCode;

    public KeyPressEvent(int keyCode, int scanCode, int modifiers) {
        super(modifiers);
        this.keyCode = keyCode;
        this.scanCode = scanCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getScanCode() {
        return scanCode;
    }
}