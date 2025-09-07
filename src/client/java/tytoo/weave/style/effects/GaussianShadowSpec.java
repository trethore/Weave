package tytoo.weave.style.effects;

import tytoo.weave.effects.Effect;
import tytoo.weave.effects.Effects;

import java.awt.*;

public record GaussianShadowSpec(Color color,
                                 float offsetX,
                                 float offsetY,
                                 float blurRadius,
                                 float spread,
                                 float cornerRadius) implements EffectSpec {
    @Override
    public Effect toEffect() {
        return Effects.shadow(color, offsetX, offsetY, blurRadius, spread, cornerRadius);
    }
}

