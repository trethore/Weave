package tytoo.weave.style;

import tytoo.weave.style.effects.EffectSpec;

import java.util.List;

public final class EffectStyleProperties {
    public static final StyleProperty<List<EffectSpec>> EFFECTS = new StyleProperty<>("effects", List.class);

    private EffectStyleProperties() {
    }
}
