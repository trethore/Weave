package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class OutlineEffect implements Effect {
    private final float width;
    private final boolean inside;
    private Color color;

    public OutlineEffect(Color color, float width, boolean inside) {
        this.color = color;
        this.width = width;
        this.inside = inside;
    }

    public OutlineEffect(Color color, float width) {
        this(color, width, true);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void afterDraw(DrawContext context, Component<?> component) {
        Render2DUtils.drawOutline(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), width, color, inside);
    }
}