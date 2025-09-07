package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public record GaussianShadowEffect(Color color,
                                   float offsetX,
                                   float offsetY,
                                   float blurRadius,
                                   float spread,
                                   float cornerRadius) implements Effect {

    public GaussianShadowEffect {
        blurRadius = Math.max(0f, blurRadius);
        spread = Math.max(0f, spread);
    }

    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        float left = component.getLeft();
        float top = component.getTop();
        float width = component.getWidth();
        float height = component.getHeight();

        if (blurRadius <= 0.0001f) {
            float swell = spread;
            Render2DUtils.drawRoundedRect(
                    context,
                    left + offsetX - swell,
                    top + offsetY - swell,
                    width + swell * 2,
                    height + swell * 2,
                    cornerRadius + swell,
                    color
            );
            return;
        }

        int layers = Math.min(96, Math.max(8, Math.round(blurRadius * 3f)));
        float sigma = Math.max(0.001f, blurRadius * 0.5f);

        float[] weights = new float[layers];
        float total = 0f;
        for (int i = 0; i < layers; i++) {
            float t = (i / (float) (layers - 1)) * blurRadius; // 0..blurRadius
            float w = (float) Math.exp(-(t * t) / (2f * sigma * sigma));
            weights[i] = w;
            total += w;
        }
        if (total <= 0f) total = 1f;

        float baseAlpha = color.getAlpha() / 255f;

        for (int i = layers - 1; i >= 0; i--) {
            float t = (i / (float) (layers - 1)) * blurRadius;
            float swell = spread + t;

            float alpha = baseAlpha * (weights[i] / total);
            int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
            if (a == 0) continue;

            Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), a);

            Render2DUtils.drawRoundedRect(
                    context,
                    left + offsetX - swell,
                    top + offsetY - swell,
                    width + swell * 2,
                    height + swell * 2,
                    cornerRadius + swell,
                    c
            );
        }
    }
}

