package tytoo.weave.effects;

import tytoo.weave.effects.implementations.*;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;

import java.awt.*;
import java.util.List;

@SuppressWarnings("unused")
public final class Effects {
    private Effects() {
    }

    public static Effect scissor() {
        return new ScissorEffect();
    }

    public static Effect outline(Color color, float width) {
        return new OutlineEffect(color, width, true);
    }

    public static Effect outline(Color color, float width, boolean inside) {
        return new OutlineEffect(color, width, inside);
    }

    public static Effect outline(Color color, float width, boolean inside, OutlineSides sides) {
        return new OutlineEffect(color, width, inside, sides);
    }

    public static Effect boxShadow(Color color, float offsetX, float offsetY, float spread, float cornerRadius) {
        return new BoxShadowEffect(color, offsetX, offsetY, spread, cornerRadius);
    }

    public static Effect shadow(Color color, float offsetX, float offsetY, float blurRadius, float cornerRadius) {
        return new GaussianShadowEffect(color, offsetX, offsetY, blurRadius, 0f, cornerRadius);
    }

    public static Effect shadow(Color color, float offsetX, float offsetY, float blurRadius, float spread, float cornerRadius) {
        return new GaussianShadowEffect(color, offsetX, offsetY, blurRadius, spread, cornerRadius);
    }

    public static Effect gradientOutline(List<Color> colors, float width) {
        return new GradientOutlineEffect(colors, width);
    }

    public static Effect gradientOutline(List<Color> colors, float width, boolean inside, GradientOutlineEffect.Direction direction) {
        return new GradientOutlineEffect(colors, width, inside, direction);
    }

    public static Effect gradientOutline(ColorWave wave, float width, boolean inside, GradientOutlineEffect.Direction direction) {
        return new GradientOutlineEffect(wave, width, inside, direction);
    }

    public static Effect gradientOutline(ColorWave wave, float width, boolean inside, GradientOutlineEffect.Direction direction, OutlineSides sides) {
        return new GradientOutlineEffect(wave, width, inside, direction, sides);
    }

    public static Effect antialiasing(int segmentsPer90) {
        return new AntialiasingEffect(segmentsPer90);
    }

}
