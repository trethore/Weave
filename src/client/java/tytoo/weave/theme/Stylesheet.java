package tytoo.weave.theme;

import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.renderer.ComponentRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Stylesheet {
    private final List<StyleRule> rules = new ArrayList<>();

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
    }

    public void clearRules() {
        this.rules.clear();
    }

    public ComponentStyle resolveStyleFor(Component<?> component) {
        ComponentStyle style = new ComponentStyle();

        ComponentRenderer baseRenderer = get(component, ComponentStyle.StyleProps.BASE_RENDERER, null);
        if (baseRenderer != null) {
            style.setBaseRenderer(baseRenderer);
        }

        for (StyleState state : StyleState.values()) {
            StyleProperty<ComponentRenderer> prop = getPropertyForState(state);
            ComponentRenderer stateRenderer = get(component, prop, null);
            style.setRenderer(state, stateRenderer);
        }
        return style;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Component<?> component, StyleProperty<T> property, T defaultValue) {
        List<StyleRule> matchingRules = new ArrayList<>();
        for (StyleRule rule : this.rules) {
            if (rule.getSelector().matches(component) && rule.getProperties().containsKey(property)) {
                matchingRules.add(rule);
            }
        }

        if (matchingRules.isEmpty()) {
            return defaultValue;
        }

        matchingRules.sort(Comparator.comparingInt(StyleRule::getSpecificity).reversed());

        try {
            return (T) matchingRules.getFirst().getProperties().get(property);
        } catch (ClassCastException e) {
            // This indicates a developer error in the stylesheet definition.
            return defaultValue;
        }
    }
}