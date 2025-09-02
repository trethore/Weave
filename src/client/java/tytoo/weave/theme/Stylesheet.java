package tytoo.weave.theme;

import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.renderer.CloneableRenderer;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.value.StyleValue;

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

    private static StyleProperty<ComponentRenderer> getOverlayPropertyForState(StyleState state) {
        return switch (state) {
            case NORMAL -> ComponentStyle.StyleProps.NORMAL_OVERLAY_RENDERER;
            case HOVERED -> ComponentStyle.StyleProps.HOVERED_OVERLAY_RENDERER;
            case FOCUSED -> ComponentStyle.StyleProps.FOCUSED_OVERLAY_RENDERER;
            case ACTIVE -> ComponentStyle.StyleProps.ACTIVE_OVERLAY_RENDERER;
            case SELECTED -> ComponentStyle.StyleProps.SELECTED_OVERLAY_RENDERER;
            case DISABLED -> ComponentStyle.StyleProps.DISABLED_OVERLAY_RENDERER;
            case VALID -> ComponentStyle.StyleProps.VALID_OVERLAY_RENDERER;
            case INVALID -> ComponentStyle.StyleProps.INVALID_OVERLAY_RENDERER;
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
            if (baseRenderer instanceof CloneableRenderer cloneable) {
                style.setBaseRenderer(cloneable.copy());
            } else {
                style.setBaseRenderer(baseRenderer);
            }
        }

        ComponentRenderer baseOverlay = (ComponentRenderer) properties.get(ComponentStyle.StyleProps.BASE_OVERLAY_RENDERER);
        if (baseOverlay != null) {
            if (baseOverlay instanceof CloneableRenderer cloneable) {
                style.setBaseOverlayRenderer(cloneable.copy());
            } else {
                style.setBaseOverlayRenderer(baseOverlay);
            }
        }

        for (StyleState state : StyleState.values()) {
            StyleProperty<ComponentRenderer> prop = getPropertyForState(state);
            ComponentRenderer stateRenderer = (ComponentRenderer) properties.get(prop);
            if (stateRenderer instanceof CloneableRenderer cloneableState) {
                style.setRenderer(state, cloneableState.copy());
            } else {
                style.setRenderer(state, stateRenderer);
            }
            ComponentRenderer overlayRenderer = (ComponentRenderer) properties.get(getOverlayPropertyForState(state));
            if (overlayRenderer != null) {
                if (overlayRenderer instanceof CloneableRenderer cloneableOverlay) {
                    style.setOverlayRenderer(state, cloneableOverlay.copy());
                } else {
                    style.setOverlayRenderer(state, overlayRenderer);
                }
            }
        }
        return style;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Component<?> component, StyleProperty<T> property, T defaultValue) {
        Object value = getProperties(component).get(property);
        Object resolved = resolveValue(component, value);
        if (property.type().isInstance(resolved)) {
            return (T) resolved;
        }
        return defaultValue;
    }

    private Object resolveValue(Component<?> component, Object value) {
        if (value instanceof StyleValue<?> styleValue) {
            return styleValue.resolve(component);
        }
        return value;
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
