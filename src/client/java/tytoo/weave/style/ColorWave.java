package tytoo.weave.style;

import tytoo.weave.animation.Interpolators;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public record ColorWave(List<Color> colors, float speed) {

    public static List<Color> createRainbow(int steps) {
        List<Color> rainbow = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            rainbow.add(Color.getHSBColor((float) i / steps, 0.85f, 1.0f));
        }
        return rainbow;
    }

    public Color getColorAt(float progress) {
        if (colors.isEmpty()) return Color.WHITE;
        if (colors.size() == 1) return colors.getFirst();

        float scaledProgress = progress * colors.size();
        int index1 = ((int) Math.floor(scaledProgress)) % colors.size();
        if (index1 < 0) index1 += colors.size();

        int index2 = (index1 + 1) % colors.size();
        float interp = scaledProgress - (float) Math.floor(scaledProgress);

        return Interpolators.COLOR.interpolate(colors.get(index1), colors.get(index2), interp);
    }
}