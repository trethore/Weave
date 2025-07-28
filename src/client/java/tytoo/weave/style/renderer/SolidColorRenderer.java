package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class SolidColorRenderer implements ComponentRenderer {
    private final Color color;

    public SolidColorRenderer(Color color) {
        this.color = color;
    }

    @Override
    public void render(DrawContext context, Component<?> component) {
        if (color != null) {
            Render2DUtils.drawRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), color);
        }
    }
}