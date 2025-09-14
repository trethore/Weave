package tytoo.weave.style.effects;

import tytoo.weave.effects.Effect;
import tytoo.weave.effects.Effects;

public record AntialiasingSpec(int segmentsPer90) implements EffectSpec {
    @Override
    public Effect toEffect() {
        return Effects.antialiasing(Math.max(1, segmentsPer90));
    }
}

