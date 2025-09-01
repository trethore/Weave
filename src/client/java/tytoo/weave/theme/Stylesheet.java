package tytoo.weave.theme;

import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.renderer.ComponentRenderer;

import java.util.*;

public class Stylesheet {
    private final List<StyleRule> rules = new ArrayList<>();
    private final WeakHashMap<Component<?>, Map<Set<StyleState>, Map<StyleProperty<?>, Object>>> propertyCache = new WeakHashMap<>();

    private static StyleProperty<ComponentRenderer> getPropertyForState(StyleState state) {
        return switch (state) {
            case NORMAL -> ComponentStyle.StyleProps.NORMAL_RENDERER;
            case HOVERED -> ComponentStyle.StyleProps.HOVERED_RENDERER;
            case FOCUSED -> ComponentStyle.StyleProps.FOCUSED_RENDERER;
            case ACTIVE -> ComponentStyle.StyleProps.ACTIVE_RENDERER;
            case SELECTED -> ComponentStyle.StyleProps.SELECTED_RENDERER;
            case DISABLED -> ComponentStyle.StyleProps.DISABLED_RENDERER;
            case VALID -> ComponentStyle.StyleProps.VALID_RENDERER;
            case INVALID -> ComponentStyle.StyleProps.INVALID_RENDERER;
        };
    }

    public void addRule(StyleRule rule) {
        this.rules.add(rule);
        this.propertyCache.clear();
    }

    public void clearRules() {
        this.rules.clear();
        this.propertyCache.clear();
    }

    public List<StyleRule> getRules() {
        return Collections.unmodifiableList(this.rules);
    }

    public void clearCache(Component<?> component) {
        this.propertyCache.remove(component);
    }

    public ComponentStyle resolveStyleFor(Component<?> component) {
        ComponentStyle style = new ComponentStyle();
        Map<StyleProperty<?>, Object> properties = getProperties(component);

        ComponentRenderer baseRenderer = (ComponentRenderer) properties.get(ComponentStyle.StyleProps.BASE_RENDERER);
        if (baseRenderer != null) {
            style.setBaseRenderer(baseRenderer);
        }

        for (StyleState state : StyleState.values()) {
            StyleProperty<ComponentRenderer> prop = getPropertyForState(state);
            ComponentRenderer stateRenderer = (ComponentRenderer) properties.get(prop);
            style.setRenderer(state, stateRenderer);
        }
        return style;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Component<?> component, StyleProperty<T> property, T defaultValue) {
        Object value = getProperties(component).get(property);
        if (property.type().isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }

    private Map<StyleProperty<?>, Object> getProperties(Component<?> component) {
        Set<StyleState> currentStates = EnumSet.copyOf(component.getActiveStyleStates());
        Map<StyleProperty<?>, Object> cachedProperties = this.propertyCache
                .computeIfAbsent(component, k -> new HashMap<>())
                .get(currentStates);

        if (cachedProperties != null) {
            return cachedProperties;
        }

        Map<StyleProperty<?>, Object> computedProperties = computePropertiesForState(component);
        this.propertyCache.get(component).put(currentStates, computedProperties);
        return computedProperties;
    }

    private Map<StyleProperty<?>, Object> computePropertiesForState(Component<?> component) {
        Map<StyleProperty<?>, Object> properties = new HashMap<>();
        List<StyleRule> aggregated = collectRulesFor(component);

        List<StyleRule> matchingRules = new ArrayList<>();
        for (StyleRule rule : aggregated) {
            if (rule.getSelector().matches(component)) {
                matchingRules.add(rule);
            }
        }
        matchingRules.sort(Comparator.comparingInt(StyleRule::getSpecificity));
        for (StyleRule rule : matchingRules) {
            properties.putAll(rule.getProperties());
        }
        return properties;
    }

    private List<StyleRule> collectRulesFor(Component<?> component) {
        List<StyleRule> aggregated = new ArrayList<>(this.rules);

        LinkedList<Component<?>> chain = new LinkedList<>();
        for (Component<?> c = component; c != null; c = c.getParent()) {
            chain.addFirst(c);
        }
        for (Component<?> c : chain) {
            aggregated.addAll(c.getLocalStyleRules());
        }
        return aggregated;
    }
}
