package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public record BoxShadowEffect(Color color, float offsetX, float offsetY, float spread,
                              float cornerRadius) implements Effect {

    public BoxShadowEffect {
        spread = Math.max(0f, spread);
    }

    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        float swell = spread;

        Render2DUtils.drawRoundedRect(
                context,
                component.getLeft() + offsetX - swell,
                component.getTop() + offsetY - swell,
                component.getWidth() + swell * 2,
                component.getHeight() + swell * 2,
                cornerRadius,
                color
        );
    }
}
