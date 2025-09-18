package tytoo.weave.style.contract;

import tytoo.weave.component.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ComponentStyleRegistry {
    private ComponentStyleRegistry() {
    }

    public static <C extends Component<?>> Builder<C> component(Class<C> componentType, String namespace) {
        return new Builder<>(componentType, namespace);
    }

    public static Builder<Component<?>> root(String namespace) {
        @SuppressWarnings("unchecked")
        Class<Component<?>> rootComponentType = (Class<Component<?>>) StyleSlot.rootComponentClass();
        return new Builder<>(rootComponentType, namespace);
    }

    public static final class Builder<C extends Component<?>> {
        private final Class<C> componentType;
        private final String namespace;
        private final ComponentThemeContract.Builder contractBuilder;
        private final Set<String> names = new HashSet<>();
        private final Set<String> ids = new HashSet<>();

        private Builder(Class<C> componentType, String namespace) {
            this.componentType = Objects.requireNonNull(componentType, "componentType");
            this.namespace = Objects.requireNonNull(namespace, "namespace");
            this.contractBuilder = componentType == StyleSlot.rootComponentClass()
                    ? ComponentThemeContract.builderForRoot()
                    : ComponentThemeContract.builder(componentType);
        }

        public <T> StyleProperty<T> optional(String name, Class<T> valueType) {
            return register(name, valueType, SlotRequirement.OPTIONAL, null, null);
        }

        public <T> StyleProperty<T> optional(String name, Class<T> valueType, StyleSlotDefaultProvider defaultProvider) {
            return register(name, valueType, SlotRequirement.OPTIONAL, defaultProvider, null);
        }

        public <T> StyleProperty<T> optional(String name, Class<T> valueType, StyleSlotDefaultProvider defaultProvider, String description) {
            return register(name, valueType, SlotRequirement.OPTIONAL, defaultProvider, description);
        }

        public <T> StyleProperty<T> optional(String name, Class<T> valueType, String description) {
            return register(name, valueType, SlotRequirement.OPTIONAL, null, description);
        }

        public <T> StyleProperty<T> required(String name, Class<T> valueType) {
            return register(name, valueType, SlotRequirement.REQUIRED, null, null);
        }

        public <T> StyleProperty<T> required(String name, Class<T> valueType, StyleSlotDefaultProvider defaultProvider) {
            return register(name, valueType, SlotRequirement.REQUIRED, defaultProvider, null);
        }

        public <T> StyleProperty<T> required(String name, Class<T> valueType, StyleSlotDefaultProvider defaultProvider, String description) {
            return register(name, valueType, SlotRequirement.REQUIRED, defaultProvider, description);
        }

        public <T> StyleProperty<T> required(String name, Class<T> valueType, String description) {
            return register(name, valueType, SlotRequirement.REQUIRED, null, description);
        }

        private <T> StyleProperty<T> register(String name, Class<T> valueType, SlotRequirement requirement, StyleSlotDefaultProvider defaultProvider, String description) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(valueType, "valueType");
            Objects.requireNonNull(requirement, "requirement");
            if (!names.add(name)) {
                throw new IllegalStateException("Duplicate style property name '" + name + "' for namespace '" + namespace + "'");
            }
            String id = namespace.isEmpty() ? name : namespace + "." + name;
            return registerId(id, valueType, requirement, defaultProvider, description);
        }

        public <T> StyleProperty<T> optionalId(String id, Class<T> valueType) {
            return registerId(id, valueType, SlotRequirement.OPTIONAL, null, null);
        }

        public <T> StyleProperty<T> optionalId(String id, Class<T> valueType, StyleSlotDefaultProvider defaultProvider) {
            return registerId(id, valueType, SlotRequirement.OPTIONAL, defaultProvider, null);
        }

        public <T> StyleProperty<T> optionalId(String id, Class<T> valueType, StyleSlotDefaultProvider defaultProvider, String description) {
            return registerId(id, valueType, SlotRequirement.OPTIONAL, defaultProvider, description);
        }

        public <T> StyleProperty<T> optionalId(String id, Class<T> valueType, String description) {
            return registerId(id, valueType, SlotRequirement.OPTIONAL, null, description);
        }

        public <T> StyleProperty<T> requiredId(String id, Class<T> valueType) {
            return registerId(id, valueType, SlotRequirement.REQUIRED, null, null);
        }

        public <T> StyleProperty<T> requiredId(String id, Class<T> valueType, StyleSlotDefaultProvider defaultProvider) {
            return registerId(id, valueType, SlotRequirement.REQUIRED, defaultProvider, null);
        }

        public <T> StyleProperty<T> requiredId(String id, Class<T> valueType, StyleSlotDefaultProvider defaultProvider, String description) {
            return registerId(id, valueType, SlotRequirement.REQUIRED, defaultProvider, description);
        }

        public <T> StyleProperty<T> requiredId(String id, Class<T> valueType, String description) {
            return registerId(id, valueType, SlotRequirement.REQUIRED, null, description);
        }

        private <T> StyleProperty<T> registerId(String id, Class<T> valueType, SlotRequirement requirement, StyleSlotDefaultProvider defaultProvider, String description) {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(valueType, "valueType");
            Objects.requireNonNull(requirement, "requirement");
            StyleSlot slot;
            if (componentType == StyleSlot.rootComponentClass()) {
                slot = description == null ? StyleSlot.forRoot(id, valueType) : StyleSlot.forRoot(id, valueType, description);
            } else {
                slot = description == null ? StyleSlot.of(id, componentType, valueType) : StyleSlot.of(id, componentType, valueType, description);
            }
            if (!ids.add(id)) {
                throw new IllegalStateException("Duplicate style property id '" + id + "'");
            }
            if (defaultProvider == null) {
                contractBuilder.slot(slot, requirement);
            } else {
                contractBuilder.slot(slot, requirement, defaultProvider);
            }
            return new StyleProperty<>(slot, valueType);
        }

        public void register() {
            StyleContractRegistry.register(contractBuilder.build());
        }
    }
}
