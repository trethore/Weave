package tytoo.weave.utils;

public final class Precision {
    private static final double NEAR_ZERO_EPSILON = 1e-5;
    private static final double NEAR_INTEGER_EPSILON = 1e-4;
    private static final double PIXEL_STEP = 1.0 / 256.0;

    private Precision() {
    }

    public static float snapLength(double value) {
        double sanitized = sanitize(value);
        double snapped = Math.round(sanitized / PIXEL_STEP) * PIXEL_STEP;
        if (snapped < 0.0) {
            snapped = 0.0;
        }
        return (float) snapped;
    }

    public static float snapCoordinate(double value) {
        double sanitized = sanitize(value);
        double snapped = Math.round(sanitized / PIXEL_STEP) * PIXEL_STEP;
        if (Math.abs(snapped) < NEAR_ZERO_EPSILON) {
            snapped = 0.0;
        }
        return (float) snapped;
    }

    public static float toFloat(double value) {
        double sanitized = sanitize(value);
        return (float) sanitized;
    }

    private static double sanitize(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        if (Math.abs(value) < NEAR_ZERO_EPSILON) {
            return 0.0;
        }
        double rounded = Math.rint(value);
        if (Math.abs(value - rounded) < NEAR_INTEGER_EPSILON) {
            return rounded;
        }
        return value;
    }
}
