package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

@FunctionalInterface
public interface ComponentRenderer {
    void render(DrawContext context, Component<?> component);
}