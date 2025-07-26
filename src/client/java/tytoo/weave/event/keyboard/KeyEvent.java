package tytoo.weave.event.keyboard;

import tytoo.weave.event.Event;

public abstract class KeyEvent extends Event {
    private final int modifiers;

    public KeyEvent(int modifiers) {
        this.modifiers = modifiers;
    }

    public int getModifiers() {
        return modifiers;
    }
}