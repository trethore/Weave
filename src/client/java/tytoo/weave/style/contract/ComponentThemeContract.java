package tytoo.weave.style.contract;

import tytoo.weave.component.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ComponentThemeContract {
    private final Class<? extends Component<?>> componentType;
    private final Map<StyleSlot, StyleSlotDefinition> definitions;

    private ComponentThemeContract(Class<? extends Component<?>> componentType, Map<StyleSlot, StyleSlotDefinition> definitions) {
        this.componentType = componentType;
        this.definitions = Map.copyOf(definitions);
    }

    public static Builder builder(Class<? extends Component<?>> componentType) {
        return new Builder(componentType);
    }

    public static Builder builderForRoot() {
        return new Builder(StyleSlot.rootComponentClass());
    }

    public Class<? extends Component<?>> componentType() {
        return componentType;
    }

    public Collection<StyleSlotDefinition> definitions() {
        return definitions.values();
    }

    public boolean hasDefinition(StyleSlot slot) {
        return definitions.containsKey(slot);
    }

    public StyleSlotDefinition definition(StyleSlot slot) {
        return definitions.get(slot);
    }

    public static final class Builder {
        private final Class<? extends Component<?>> componentType;
        private final Map<StyleSlot, StyleSlotDefinition> definitions = new LinkedHashMap<>();

        private Builder(Class<? extends Component<?>> componentType) {
            this.componentType = Objects.requireNonNull(componentType, "componentType");
        }

        public Builder slot(StyleSlot slot, SlotRequirement requirement) {
            definitions.put(slot, new StyleSlotDefinition(slot, requirement, null));
            return this;
        }

        public Builder slot(StyleSlot slot, SlotRequirement requirement, StyleSlotDefaultProvider defaultProvider) {
            definitions.put(slot, new StyleSlotDefinition(slot, requirement, defaultProvider));
            return this;
        }

        public ComponentThemeContract build() {
            return new ComponentThemeContract(componentType, definitions);
        }
    }
}
