package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeRenderer implements ComponentRenderer, CloneableRenderer {
    private final List<ComponentRenderer> renderers = new ArrayList<>();

    public CompositeRenderer add(ComponentRenderer renderer) {
        if (renderer != null) this.renderers.add(renderer);
        return this;
    }

    public List<ComponentRenderer> getRenderers() {
        return Collections.unmodifiableList(renderers);
    }

    @Override
    public void render(DrawContext context, Component<?> component) {
        for (ComponentRenderer renderer : renderers) {
            renderer.render(context, component);
        }
    }

    @Override
    public ComponentRenderer copy() {
        CompositeRenderer clone = new CompositeRenderer();
        for (ComponentRenderer r : renderers) {
            if (r instanceof CloneableRenderer c) {
                clone.add(c.copy());
            } else {
                clone.add(r);
            }
        }
        return clone;
    }
}

