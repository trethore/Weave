package tytoo.weave.style;

public class EdgeInsets {
    public float top, right, bottom, left;

    public EdgeInsets(float top, float right, float bottom, float left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

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