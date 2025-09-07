package tytoo.weave.style.effects;

import tytoo.weave.effects.Effect;
import tytoo.weave.effects.implementations.GradientOutlineEffect;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;

import java.awt.*;
import java.util.List;

public final class GradientOutlineSpec implements EffectSpec {
    private final List<Color> colors;
    private final ColorWave wave;
    private final float width;
    private final boolean inside;
    private final GradientOutlineEffect.Direction direction;
    private final OutlineSides sides;

    public GradientOutlineSpec(List<Color> colors, float width, boolean inside, GradientOutlineEffect.Direction direction, OutlineSides sides) {
        this.colors = colors;
        this.wave = null;
        this.width = width;
        this.inside = inside;
        this.direction = direction;
        this.sides = sides;
    }

    public GradientOutlineSpec(ColorWave wave, float width, boolean inside, GradientOutlineEffect.Direction direction, OutlineSides sides) {
        this.colors = null;
        this.wave = wave;
        this.width = width;
        this.inside = inside;
        this.direction = direction;
        this.sides = sides;
    }

    @Override
    public Effect toEffect() {
        if (wave != null) {
            return new GradientOutlineEffect(wave, width, inside, direction, sides);
        }
        GradientOutlineEffect eff = new GradientOutlineEffect(colors, width, inside, direction);
        eff.setSides(sides);
        return eff;
    }
}
