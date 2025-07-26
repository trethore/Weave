package tytoo.weave.event.keyboard;

public class CharTypeEvent extends KeyEvent {
    private final char character;

    public CharTypeEvent(char character, int modifiers) {
        super(modifiers);
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}