package tytoo.weave.animation;

@SuppressWarnings("unused")
public final class Easings {
    public static final EasingFunction LINEAR = t -> t;
    public static final EasingFunction EASE_IN_SINE = t -> 1 - (float) Math.cos((t * Math.PI) / 2);
    public static final EasingFunction EASE_OUT_SINE = t -> (float) Math.sin((t * Math.PI) / 2);
    public static final EasingFunction EASE_IN_OUT_SINE = t -> -((float) Math.cos(Math.PI * t) - 1) / 2;
    public static final EasingFunction EASE_IN_QUAD = t -> t * t;
    public static final EasingFunction EASE_OUT_QUAD = t -> 1 - (1 - t) * (1 - t);
    public static final EasingFunction EASE_IN_OUT_QUAD = t -> t < 0.5 ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
    public static final EasingFunction EASE_OUT_BACK = t -> {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    };

    private Easings() {
    }
}
