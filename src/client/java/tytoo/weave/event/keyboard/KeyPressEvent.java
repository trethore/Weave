package tytoo.weave.event.keyboard;

import tytoo.weave.event.EventType;

public class KeyPressEvent extends KeyEvent {
    public static final EventType<KeyPressEvent> TYPE = new EventType<>();
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

    @Override
    public EventType<KeyPressEvent> getType() {
        return TYPE;
    }
}