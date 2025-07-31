package tytoo.weave.style;

public record EdgeInsets(float top, float right, float bottom, float left) {
    public EdgeInsets(float vertical, float horizontal) {
        this(vertical, horizontal, vertical, horizontal);
    }

    public EdgeInsets(float all) {
        this(all, all, all, all);
    }

    public static EdgeInsets zero() {
        return new EdgeInsets(0);
    }
}