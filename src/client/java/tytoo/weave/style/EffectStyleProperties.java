package tytoo.weave.style;

import tytoo.weave.component.Component;
import tytoo.weave.style.contract.ComponentStyleRegistry;
import tytoo.weave.style.contract.StyleProperty;

import java.util.List;

public final class EffectStyleProperties {
    public static final StyleProperty<List<?>> EFFECTS;

    static {
        ComponentStyleRegistry.Builder<Component<?>> builder = ComponentStyleRegistry.root("effects");
        @SuppressWarnings("unchecked")
        StyleProperty<List<?>> effects = (StyleProperty<List<?>>) (StyleProperty<?>) builder.optionalId("effects", List.class);
        EFFECTS = effects;
        builder.register();
    }

    private EffectStyleProperties() {
    }
}
