package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public record GradientRenderer(Color startColor, Color endColor, float angle) implements ComponentRenderer {

    @Override
    public void render(DrawContext context, Component<?> component) {
        Render2DUtils.drawGradientRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), startColor, endColor, angle);
    }
}
