package tytoo.weave.style.contract;

import tytoo.weave.component.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StyleContractRegistry {
    private static final Map<Class<? extends Component<?>>, List<ComponentThemeContract>> contributions = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Component<?>>, ResolvedComponentContract> resolvedCache = new ConcurrentHashMap<>();

    private StyleContractRegistry() {
    }

    public static synchronized void register(ComponentThemeContract contract) {
        contributions.computeIfAbsent(contract.componentType(), k -> new ArrayList<>()).add(contract);
        resolvedCache.clear();
    }

    public static synchronized void clear() {
        contributions.clear();
        resolvedCache.clear();
    }

    public static ResolvedComponentContract resolve(Class<? extends Component<?>> componentType) {
        return resolvedCache.computeIfAbsent(componentType, StyleContractRegistry::buildContractFor);
    }

    private static ResolvedComponentContract buildContractFor(Class<? extends Component<?>> componentType) {
        Map<StyleSlot, StyleSlotDefinition> definitions = new LinkedHashMap<>();
        Class<?> current = componentType;
        while (current != null && Component.class.isAssignableFrom(current)) {
            Class<? extends Component<?>> cast = castComponentClass(current);
            List<ComponentThemeContract> list = contributions.get(cast);
            if (list != null) {
                for (ComponentThemeContract contract : list) {
                    for (StyleSlotDefinition definition : contract.definitions()) {
                        StyleSlot slot = definition.slot();
                        if (!slot.componentType().isAssignableFrom(componentType)) {
                            continue;
                        }
                        definitions.putIfAbsent(slot, definition);
                    }
                }
            }
            current = current.getSuperclass();
        }
        return new ResolvedComponentContract(componentType, definitions);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Component<?>> castComponentClass(Class<?> type) {
        return (Class<? extends Component<?>>) type;
    }
}
