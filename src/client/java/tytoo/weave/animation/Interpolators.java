package tytoo.weave.animation;

import java.awt.*;

public final class Interpolators {
    public static final PropertyInterpolator<Float> FLOAT = (start, end, progress) -> start + (end - start) * progress;
    public static final PropertyInterpolator<Color> COLOR = (start, end, progress) -> {
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * progress);
        return new Color(r, g, b, a);
    };

    private Interpolators() {
    }
}