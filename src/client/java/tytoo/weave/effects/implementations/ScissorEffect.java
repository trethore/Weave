package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;

public class ScissorEffect implements Effect {
    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        context.enableScissor(
                (int) component.getLeft(),
                (int) component.getTop(),
                (int) (component.getLeft() + component.getWidth()),
                (int) (component.getTop() + component.getHeight())
        );
    }

    @Override
    public void afterDraw(DrawContext context, Component<?> component) {
        context.disableScissor();
    }
}