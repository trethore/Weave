package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class GradientRenderer implements ComponentRenderer {
    private final Color startColor;
    private final Color endColor;
    private final float angle;

    public GradientRenderer(Color startColor, Color endColor, float angle) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.angle = angle;
    }

    @Override
    public void render(DrawContext context, Component<?> component) {
        Render2DUtils.drawGradientRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), startColor, endColor, angle);
    }
}