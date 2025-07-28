package tytoo.weave.style;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class ComponentStyle implements Cloneable {
    private Map<ComponentState, ComponentRenderer> renderers = new EnumMap<>(ComponentState.class);
    @Nullable
    private ComponentRenderer baseRenderer;

    @Nullable
    public ComponentRenderer getRenderer(Component<?> component) {
        if (component.isFocused() && renderers.containsKey(ComponentState.FOCUSED)) {
            return renderers.get(ComponentState.FOCUSED);
        }
        if (component.isHovered() && renderers.containsKey(ComponentState.HOVERED)) {
            return renderers.get(ComponentState.HOVERED);
        }
        if (renderers.containsKey(ComponentState.NORMAL)) {
            return renderers.get(ComponentState.NORMAL);
        }
        return baseRenderer;
    }

    @Nullable
    public ComponentRenderer getBaseRenderer() {
        return baseRenderer;
    }

    public ComponentStyle setBaseRenderer(@Nullable ComponentRenderer renderer) {
        this.baseRenderer = renderer;
        return this;
    }

    public ComponentStyle setRenderer(ComponentState state, @Nullable ComponentRenderer renderer) {
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

    public ComponentStyle setColor(ComponentState state, Color color) {
        return setRenderer(state, new SolidColorRenderer(color));
    }

    @Override
    public ComponentStyle clone() {
        try {
            ComponentStyle clone = (ComponentStyle) super.clone();
            clone.renderers = new EnumMap<>(this.renderers);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ComponentStyle is Cloneable but clone() failed", e);
        }
    }
}