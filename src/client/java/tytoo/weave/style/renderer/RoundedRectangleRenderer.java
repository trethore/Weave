package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class RoundedRectangleRenderer implements ComponentRenderer {
    private final Color color;
    private final float radius;

    public RoundedRectangleRenderer(Color color, float radius) {
        this.color = color;
        this.radius = radius;
    }

    @Override
    public void render(DrawContext context, Component<?> component) {
        if (color != null) {
            Render2DUtils.drawRoundedRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), radius, color);
        }
    }
}