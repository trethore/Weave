package tytoo.weave.style.effects;

import tytoo.weave.effects.Effect;
import tytoo.weave.effects.Effects;

import java.awt.*;

public record BoxShadowSpec(Color color, float offsetX, float offsetY, float spread,
                            float cornerRadius) implements EffectSpec {
    @Override
    public Effect toEffect() {
        return Effects.boxShadow(color, offsetX, offsetY, spread, cornerRadius);
    }
}

