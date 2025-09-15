package tytoo.weave.style;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ComponentStyle implements Cloneable {
    private final List<StyleState> statePriority = new ArrayList<>(Arrays.asList(StyleState.DISABLED, StyleState.ACTIVE, StyleState.SELECTED, StyleState.FOCUSED, StyleState.HOVERED, StyleState.NORMAL));
    private Map<StyleState, ComponentRenderer> renderers = new HashMap<>();
    private Map<StyleState, ComponentRenderer> overlayRenderers = new HashMap<>();
    @Nullable
    private ComponentRenderer baseRenderer;
    @Nullable
    private ComponentRenderer baseOverlayRenderer;

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

    @Nullable
    public ComponentRenderer getOverlayRenderer(Component<?> component) {
        Set<StyleState> activeStates = component.getActiveStyleStates();
        for (StyleState state : statePriority) {
            if (activeStates.contains(state) && overlayRenderers.containsKey(state)) {
                return overlayRenderers.get(state);
            }
        }
        return baseOverlayRenderer;
    }

    @Nullable
    public ComponentRenderer getBaseOverlayRenderer() {
        return baseOverlayRenderer;
    }

    public ComponentStyle setBaseOverlayRenderer(@Nullable ComponentRenderer renderer) {
        this.baseOverlayRenderer = renderer;
        return this;
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

    public ComponentStyle setOverlayRenderer(StyleState state, @Nullable ComponentRenderer renderer) {
        if (renderer == null) {
            overlayRenderers.remove(state);
        } else {
            overlayRenderers.put(state, renderer);
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
            clone.overlayRenderers = new HashMap<>(this.overlayRenderers);
            clone.statePriority.clear();
            clone.statePriority.addAll(this.statePriority);
            clone.baseOverlayRenderer = this.baseOverlayRenderer;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ComponentStyle is Cloneable but clone() failed", e);
        }
    }

    public static final class Slots {
        public static final StyleSlot BASE_RENDERER = StyleSlot.forRoot("component.renderer.base", ComponentRenderer.class);
        public static final StyleSlot NORMAL_RENDERER = StyleSlot.forRoot("component.renderer.normal", ComponentRenderer.class);
        public static final StyleSlot HOVERED_RENDERER = StyleSlot.forRoot("component.renderer.hovered", ComponentRenderer.class);
        public static final StyleSlot FOCUSED_RENDERER = StyleSlot.forRoot("component.renderer.focused", ComponentRenderer.class);
        public static final StyleSlot ACTIVE_RENDERER = StyleSlot.forRoot("component.renderer.active", ComponentRenderer.class);
        public static final StyleSlot SELECTED_RENDERER = StyleSlot.forRoot("component.renderer.selected", ComponentRenderer.class);
        public static final StyleSlot DISABLED_RENDERER = StyleSlot.forRoot("component.renderer.disabled", ComponentRenderer.class);
        public static final StyleSlot VALID_RENDERER = StyleSlot.forRoot("component.renderer.valid", ComponentRenderer.class);
        public static final StyleSlot INVALID_RENDERER = StyleSlot.forRoot("component.renderer.invalid", ComponentRenderer.class);

        public static final StyleSlot BASE_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.base", ComponentRenderer.class);
        public static final StyleSlot NORMAL_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.normal", ComponentRenderer.class);
        public static final StyleSlot HOVERED_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.hovered", ComponentRenderer.class);
        public static final StyleSlot FOCUSED_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.focused", ComponentRenderer.class);
        public static final StyleSlot ACTIVE_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.active", ComponentRenderer.class);
        public static final StyleSlot SELECTED_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.selected", ComponentRenderer.class);
        public static final StyleSlot DISABLED_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.disabled", ComponentRenderer.class);
        public static final StyleSlot VALID_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.valid", ComponentRenderer.class);
        public static final StyleSlot INVALID_OVERLAY_RENDERER = StyleSlot.forRoot("component.overlay.invalid", ComponentRenderer.class);

        private Slots() {
        }
    }
}
