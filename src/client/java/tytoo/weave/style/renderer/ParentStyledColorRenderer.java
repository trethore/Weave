package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public record ParentStyledColorRenderer(StyleSlot colorSlot,
                                        Color defaultColor) implements ComponentRenderer {

    @Override
    public void render(DrawContext context, Component<?> component) {
        Component<?> parent = component.getParent();
        Component<?> target = parent != null ? parent : component;
        Color color = target.getCachedStyleValue(colorSlot, defaultColor);
        if (color != null) {
            Render2DUtils.drawRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), color);
        }
    }
}
