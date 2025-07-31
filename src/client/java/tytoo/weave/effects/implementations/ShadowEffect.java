package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class ShadowEffect implements Effect {
    private final Color color;
    private final float offsetX;
    private final float offsetY;
    private final float size;
    private final float cornerRadius;

    public ShadowEffect(Color color, float offsetX, float offsetY, float size, float cornerRadius) {
        this.color = color;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.size = Math.max(1, size);
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        int layers = (int) this.size;
        for (int i = layers; i > 0; i--) {
            float layerRatio = (float) i / layers;
            float alpha = (float) color.getAlpha() / 255f * (1f - layerRatio) * 0.5f;
            if (alpha <= 0) continue;

            Color shadowColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));
            float swell = size * layerRatio;

            Render2DUtils.drawRoundedRect(
                    context,
                    component.getLeft() + offsetX - swell,
                    component.getTop() + offsetY - swell,
                    component.getWidth() + swell * 2,
                    component.getHeight() + swell * 2,
                    cornerRadius + swell,
                    shadowColor
            );
        }
    }
}