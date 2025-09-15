package tytoo.weave.theme;

import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.contract.ResolvedComponentContract;
import tytoo.weave.style.contract.SlotRequirement;
import tytoo.weave.style.contract.StyleContractRegistry;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.style.renderer.CloneableRenderer;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.value.StyleValue;

import java.util.*;

public class Stylesheet {
    private final List<StyleRule> rules = new ArrayList<>();
    private final List<StyleRule> sortedRules = new ArrayList<>();
    private final WeakHashMap<Component<?>, Map<Long, Map<StyleSlot, Object>>> valueCache = new WeakHashMap<>();

    private static StyleSlot rendererSlot(StyleState state) {
        return switch (state) {
            case NORMAL -> ComponentStyle.Slots.NORMAL_RENDERER;
            case HOVERED -> ComponentStyle.Slots.HOVERED_RENDERER;
            case FOCUSED -> ComponentStyle.Slots.FOCUSED_RENDERER;
            case ACTIVE -> ComponentStyle.Slots.ACTIVE_RENDERER;
            case SELECTED -> ComponentStyle.Slots.SELECTED_RENDERER;
            case DISABLED -> ComponentStyle.Slots.DISABLED_RENDERER;
            case VALID -> ComponentStyle.Slots.VALID_RENDERER;
            case INVALID -> ComponentStyle.Slots.INVALID_RENDERER;
        };
    }

    private static StyleSlot overlaySlot(StyleState state) {
        return switch (state) {
            case NORMAL -> ComponentStyle.Slots.NORMAL_OVERLAY_RENDERER;
            case HOVERED -> ComponentStyle.Slots.HOVERED_OVERLAY_RENDERER;
            case FOCUSED -> ComponentStyle.Slots.FOCUSED_OVERLAY_RENDERER;
            case ACTIVE -> ComponentStyle.Slots.ACTIVE_OVERLAY_RENDERER;
            case SELECTED -> ComponentStyle.Slots.SELECTED_OVERLAY_RENDERER;
            case DISABLED -> ComponentStyle.Slots.DISABLED_OVERLAY_RENDERER;
            case VALID -> ComponentStyle.Slots.VALID_OVERLAY_RENDERER;
            case INVALID -> ComponentStyle.Slots.INVALID_OVERLAY_RENDERER;
        };
    }

    public void addRule(StyleRule rule) {
        this.rules.add(rule);
        int index = Collections.binarySearch(this.sortedRules, rule, Comparator.comparingInt(StyleRule::getSpecificity));
        if (index < 0) {
            index = -(index + 1);
        }
        this.sortedRules.add(index, rule);
        this.valueCache.clear();
    }

    public void clearRules() {
        this.rules.clear();
        this.sortedRules.clear();
        this.valueCache.clear();
    }

    public List<StyleRule> getRules() {
        return Collections.unmodifiableList(this.rules);
    }

    public void clearCache(Component<?> component) {
        this.valueCache.remove(component);
    }

    public ComponentStyle resolveStyleFor(Component<?> component) {
        ComponentStyle style = new ComponentStyle();
        @SuppressWarnings("unchecked")
        Class<? extends Component<?>> componentClass = (Class<? extends Component<?>>) component.getClass();
        ResolvedComponentContract contract = StyleContractRegistry.resolve(componentClass);
        Map<StyleSlot, Object> values = getValues(component);

        ComponentRenderer baseRenderer = (ComponentRenderer) resolveSlot(component, ComponentStyle.Slots.BASE_RENDERER, contract, values);
        if (baseRenderer != null) {
            if (baseRenderer instanceof CloneableRenderer cloneable) {
                style.setBaseRenderer(cloneable.copy());
            } else {
                style.setBaseRenderer(baseRenderer);
            }
        } else {
            enforceRequirement(component, ComponentStyle.Slots.BASE_RENDERER, contract);
        }

        ComponentRenderer baseOverlay = (ComponentRenderer) resolveSlot(component, ComponentStyle.Slots.BASE_OVERLAY_RENDERER, contract, values);
        if (baseOverlay != null) {
            if (baseOverlay instanceof CloneableRenderer cloneable) {
                style.setBaseOverlayRenderer(cloneable.copy());
            } else {
                style.setBaseOverlayRenderer(baseOverlay);
            }
        } else {
            enforceRequirement(component, ComponentStyle.Slots.BASE_OVERLAY_RENDERER, contract);
        }

        for (StyleState state : StyleState.values()) {
            ComponentRenderer stateRenderer = (ComponentRenderer) resolveSlot(component, rendererSlot(state), contract, values);
            if (stateRenderer instanceof CloneableRenderer cloneableState) {
                style.setRenderer(state, cloneableState.copy());
            } else {
                style.setRenderer(state, stateRenderer);
            }
            if (stateRenderer == null) {
                enforceRequirement(component, rendererSlot(state), contract);
            }
            ComponentRenderer overlayRenderer = (ComponentRenderer) resolveSlot(component, overlaySlot(state), contract, values);
            if (overlayRenderer instanceof CloneableRenderer cloneableOverlay) {
                style.setOverlayRenderer(state, cloneableOverlay.copy());
            } else {
                style.setOverlayRenderer(state, overlayRenderer);
            }
            if (overlayRenderer == null) {
                enforceRequirement(component, overlaySlot(state), contract);
            }
        }
        return style;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Component<?> component, StyleSlot slot, T defaultValue) {
        @SuppressWarnings("unchecked")
        Class<? extends Component<?>> componentClass = (Class<? extends Component<?>>) component.getClass();
        ResolvedComponentContract contract = StyleContractRegistry.resolve(componentClass);
        Map<StyleSlot, Object> values = getValues(component);
        Object resolved = resolveSlot(component, slot, contract, values);
        if (resolved == null) {
            enforceRequirement(component, slot, contract);
            return defaultValue;
        }
        if (!slot.valueType().isInstance(resolved)) {
            enforceRequirement(component, slot, contract);
            return defaultValue;
        }
        return (T) resolved;
    }

    private Object resolveSlot(Component<?> component, StyleSlot slot, ResolvedComponentContract contract, Map<StyleSlot, Object> values) {
        Object value = values.get(slot);
        if (value == null) {
            value = contract.defaultValue(slot, component);
        }
        Object resolved = resolveValue(component, value);
        if (resolved == null) {
            return null;
        }
        if (!slot.valueType().isInstance(resolved)) {
            Object fallback = resolveValue(component, contract.defaultValue(slot, component));
            if (fallback != null && slot.valueType().isInstance(fallback)) {
                return fallback;
            }
            return null;
        }
        return resolved;
    }

    private Object resolveValue(Component<?> component, Object value) {
        if (value instanceof StyleValue<?> styleValue) {
            return styleValue.resolve(component);
        }
        return value;
    }

    private void enforceRequirement(Component<?> component, StyleSlot slot, ResolvedComponentContract contract) {
        SlotRequirement requirement = contract.requirement(slot);
        if (requirement == SlotRequirement.REQUIRED) {
            throw new IllegalStateException("Required style slot missing: " + slot.id() + " for component " + component.getClass().getName());
        }
    }

    private Map<StyleSlot, Object> getValues(Component<?> component) {
        long currentStatesKey = stateKey(component.getActiveStyleStates());
        Map<Long, Map<StyleSlot, Object>> componentCache = this.valueCache.computeIfAbsent(component, k -> new HashMap<>());
        Map<StyleSlot, Object> cachedValues = componentCache.get(currentStatesKey);
        if (cachedValues != null) {
            return cachedValues;
        }

        Map<StyleSlot, Object> computed = computeValuesForState(component);
        componentCache.put(currentStatesKey, computed);
        return computed;
    }

    private Map<StyleSlot, Object> computeValuesForState(Component<?> component) {
        Map<StyleSlot, Object> values = new HashMap<>();
        List<StyleRule> globalMatches = new ArrayList<>();
        for (StyleRule rule : this.sortedRules) {
            if (rule.getSelector().matches(component)) {
                globalMatches.add(rule);
            }
        }

        List<StyleRule> localRules = collectLocalRules(component);
        List<StyleRule> localMatches = new ArrayList<>();
        for (StyleRule rule : localRules) {
            if (rule.getSelector().matches(component)) {
                localMatches.add(rule);
            }
        }
        localMatches.sort(Comparator.comparingInt(StyleRule::getSpecificity));

        List<StyleRule> merged = mergeBySpecificity(globalMatches, localMatches);
        for (StyleRule rule : merged) {
            values.putAll(rule.getValues());
        }
        return values;
    }

    private List<StyleRule> collectLocalRules(Component<?> component) {
        LinkedList<Component<?>> chain = new LinkedList<>();
        for (Component<?> c = component; c != null; c = c.getParent()) {
            chain.addFirst(c);
        }
        List<StyleRule> local = new ArrayList<>();
        for (Component<?> c : chain) {
            local.addAll(c.getLocalStyleRules());
        }
        return local;
    }

    private List<StyleRule> mergeBySpecificity(List<StyleRule> globalMatches, List<StyleRule> localMatches) {
        if (localMatches.isEmpty()) {
            return globalMatches;
        }
        if (globalMatches.isEmpty()) {
            return localMatches;
        }
        List<StyleRule> merged = new ArrayList<>(globalMatches.size() + localMatches.size());
        int gi = 0;
        int li = 0;
        while (gi < globalMatches.size() && li < localMatches.size()) {
            StyleRule g = globalMatches.get(gi);
            StyleRule l = localMatches.get(li);
            if (g.getSpecificity() <= l.getSpecificity()) {
                merged.add(g);
                gi++;
            } else {
                merged.add(l);
                li++;
            }
        }
        while (gi < globalMatches.size()) {
            merged.add(globalMatches.get(gi++));
        }
        while (li < localMatches.size()) {
            merged.add(localMatches.get(li++));
        }
        return merged;
    }

    private long stateKey(Set<StyleState> states) {
        long bits = 0L;
        for (StyleState state : states) {
            bits |= 1L << state.ordinal();
        }
        return bits;
    }
}
