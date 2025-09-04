package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.ColorableEffect;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class OutlineEffect implements Effect, ColorableEffect {
    private float width;
    private boolean inside;
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    @Override
    public void afterDraw(DrawContext context, Component<?> component) {
        Render2DUtils.drawOutline(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), width, color, inside);
    }
}
