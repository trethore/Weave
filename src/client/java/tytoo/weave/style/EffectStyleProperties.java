package tytoo.weave.style;

import tytoo.weave.style.contract.StyleSlot;

import java.util.List;

public final class EffectStyleProperties {
    public static final StyleSlot EFFECTS = StyleSlot.forRoot("effects", List.class);

    private EffectStyleProperties() {
    }
}
