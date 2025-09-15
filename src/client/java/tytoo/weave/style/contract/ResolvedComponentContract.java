package tytoo.weave.style.contract;

import tytoo.weave.component.Component;

import java.util.Map;
import java.util.Objects;

public final class ResolvedComponentContract {
    private final Class<? extends Component<?>> componentType;
    private final Map<StyleSlot, StyleSlotDefinition> definitions;

    ResolvedComponentContract(Class<? extends Component<?>> componentType, Map<StyleSlot, StyleSlotDefinition> definitions) {
        this.componentType = Objects.requireNonNull(componentType, "componentType");
        this.definitions = Map.copyOf(definitions);
    }

    public Class<? extends Component<?>> componentType() {
        return componentType;
    }

    public Map<StyleSlot, StyleSlotDefinition> definitions() {
        return definitions;
    }

    public StyleSlotDefinition definition(StyleSlot slot) {
        return definitions.get(slot);
    }

    public SlotRequirement requirement(StyleSlot slot) {
        StyleSlotDefinition definition = definitions.get(slot);
        if (definition == null) {
            return SlotRequirement.OPTIONAL;
        }
        return definition.requirement();
    }

    public Object defaultValue(StyleSlot slot, Component<?> component) {
        StyleSlotDefinition definition = definitions.get(slot);
        if (definition == null || definition.defaultProvider() == null) {
            return null;
        }
        return definition.defaultProvider().provide(component);
    }
}
