package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;

public class ScissorEffect implements Effect {
    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        int x1 = (int) Math.floor(component.getLeft());
        int y1 = (int) Math.floor(component.getTop());
        int x2 = (int) Math.ceil(component.getLeft() + component.getWidth());
        int y2 = (int) Math.ceil(component.getTop() + component.getHeight());
        context.enableScissor(x1, y1, x2, y2);
    }

    @Override
    public void afterDraw(DrawContext context, Component<?> component) {
        context.disableScissor();
    }
}
