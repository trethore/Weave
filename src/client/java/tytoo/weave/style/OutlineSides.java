package tytoo.weave.style;

public record OutlineSides(boolean top, boolean right, boolean bottom, boolean left) {
    public static OutlineSides all() {
        return new OutlineSides(true, true, true, true);
    }

    public static OutlineSides none() {
        return new OutlineSides(false, false, false, false);
    }

    public static OutlineSides vertical() {
        return new OutlineSides(true, false, true, false);
    }

    public static OutlineSides horizontal() {
        return new OutlineSides(false, true, false, true);
    }

    public static OutlineSides onlyTop() {
        return new OutlineSides(true, false, false, false);
    }

    public static OutlineSides onlyRight() {
        return new OutlineSides(false, true, false, false);
    }

    public static OutlineSides onlyBottom() {
        return new OutlineSides(false, false, true, false);
    }

    public static OutlineSides onlyLeft() {
        return new OutlineSides(false, false, false, true);
    }
}
