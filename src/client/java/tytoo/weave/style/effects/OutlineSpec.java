package tytoo.weave.style.effects;

import tytoo.weave.effects.Effect;
import tytoo.weave.effects.Effects;
import tytoo.weave.style.OutlineSides;

import java.awt.*;

public record OutlineSpec(Color color, float width, boolean inside, OutlineSides sides) implements EffectSpec {
    @Override
    public Effect toEffect() {
        return Effects.outline(color, width, inside, sides);
    }
}

