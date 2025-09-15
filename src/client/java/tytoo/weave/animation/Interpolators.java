package tytoo.weave.animation;

import tytoo.weave.style.EdgeInsets;
import tytoo.weave.utils.Precision;

import java.awt.*;

public final class Interpolators {
    public static final PropertyInterpolator<Float> FLOAT = (start, end, progress) -> {
        double startValue = start;
        double endValue = end;
        double value = startValue + (endValue - startValue) * progress;
        return Precision.toFloat(value);
    };
    public static final PropertyInterpolator<Color> COLOR = (start, end, progress) -> {
        double red = start.getRed() + (end.getRed() - start.getRed()) * progress;
        double green = start.getGreen() + (end.getGreen() - start.getGreen()) * progress;
        double blue = start.getBlue() + (end.getBlue() - start.getBlue()) * progress;
        double alpha = start.getAlpha() + (end.getAlpha() - start.getAlpha()) * progress;
        int r = clamp((int) Math.round(red));
        int g = clamp((int) Math.round(green));
        int b = clamp((int) Math.round(blue));
        int a = clamp((int) Math.round(alpha));
        return new Color(r, g, b, a);
    };

    public static final PropertyInterpolator<EdgeInsets> EDGE_INSETS = (start, end, progress) -> new EdgeInsets(
            Precision.toFloat(start.top() + (end.top() - start.top()) * progress),
            Precision.toFloat(start.right() + (end.right() - start.right()) * progress),
            Precision.toFloat(start.bottom() + (end.bottom() - start.bottom()) * progress),
            Precision.toFloat(start.left() + (end.left() - start.left()) * progress)
    );

    private Interpolators() {
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
