package tytoo.weave.style;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ComponentStyle implements Cloneable {
    private final List<StyleState> statePriority = new ArrayList<>(Arrays.asList(StyleState.DISABLED, StyleState.ACTIVE, StyleState.SELECTED, StyleState.FOCUSED, StyleState.HOVERED, StyleState.NORMAL));
    private Map<StyleState, ComponentRenderer> renderers = new HashMap<>();
    @Nullable
    private ComponentRenderer baseRenderer;

    @Nullable
    public ComponentRenderer getRenderer(Component<?> component) {
        Set<StyleState> activeStates = component.getActiveStyleStates();
        for (StyleState state : statePriority) {
            if (activeStates.contains(state) && renderers.containsKey(state)) {
                return renderers.get(state);
            }
        }

        return baseRenderer;
    }

    public ComponentStyle setStatePriority(StyleState... states) {
        this.statePriority.clear();
        Collections.addAll(this.statePriority, states);
        return this;
    }

    @Nullable
    public ComponentRenderer getBaseRenderer() {
        return baseRenderer;
    }

    public ComponentStyle setBaseRenderer(@Nullable ComponentRenderer renderer) {
        this.baseRenderer = renderer;
        return this;
    }

    public ComponentStyle setRenderer(StyleState state, @Nullable ComponentRenderer renderer) {
        if (renderer == null) {
            renderers.remove(state);
        } else {
            renderers.put(state, renderer);
        }
        return this;
    }

    public ComponentStyle setColor(Color color) {
        return setBaseRenderer(new SolidColorRenderer(color));
    }

    public ComponentStyle setColor(StyleState state, Color color) {
        return setRenderer(state, new SolidColorRenderer(color));
    }

    @Override
    public ComponentStyle clone() {
        try {
            ComponentStyle clone = (ComponentStyle) super.clone();
            clone.renderers = new HashMap<>(this.renderers);
            clone.statePriority.clear();
            clone.statePriority.addAll(this.statePriority);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ComponentStyle is Cloneable but clone() failed", e);
        }
    }

    public static final class StyleProps {
        public static final StyleProperty<ComponentRenderer> BASE_RENDERER = new StyleProperty<>("renderer", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> NORMAL_RENDERER = new StyleProperty<>("renderer-normal", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> HOVERED_RENDERER = new StyleProperty<>("renderer-hovered", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> FOCUSED_RENDERER = new StyleProperty<>("renderer-focused", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> ACTIVE_RENDERER = new StyleProperty<>("renderer-active", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> SELECTED_RENDERER = new StyleProperty<>("renderer-selected", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> DISABLED_RENDERER = new StyleProperty<>("renderer-disabled", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> VALID_RENDERER = new StyleProperty<>("renderer-valid", ComponentRenderer.class);
        public static final StyleProperty<ComponentRenderer> INVALID_RENDERER = new StyleProperty<>("renderer-invalid", ComponentRenderer.class);

        private StyleProps() {
        }
    }
}