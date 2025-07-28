package tytoo.weave.event.keyboard;

import tytoo.weave.event.EventType;

public class CharTypeEvent extends KeyEvent {
    public static final EventType<CharTypeEvent> TYPE = new EventType<>();
    private final char character;

    public CharTypeEvent(char character, int modifiers) {
        super(modifiers);
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    @Override
    public EventType<CharTypeEvent> getType() {
        return TYPE;
    }
}