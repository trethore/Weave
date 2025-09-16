package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record GaussianShadowEffect(Color color,
                                   float offsetX,
                                   float offsetY,
                                   float blurRadius,
                                   float spread,
                                   float cornerRadius) implements Effect {

    private static final Map<Integer, LayerProfile> PROFILE_CACHE = new ConcurrentHashMap<>();

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

        LayerProfile profile = resolveProfile(blurRadius);

        float baseAlpha = color.getAlpha() / 255f;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        float[] offsets = profile.offsets();
        float[] weights = profile.weights();
        for (int i = offsets.length - 1; i >= 0; i--) {
            float alpha = baseAlpha * weights[i];
            if (alpha <= 0.003f) continue;
            float swell = spread + offsets[i];
            int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
            if (a == 0) continue;
            Color layerColor = new Color(red, green, blue, a);

            Render2DUtils.drawRoundedRect(
                    context,
                    left + offsetX - swell,
                    top + offsetY - swell,
                    width + swell * 2,
                    height + swell * 2,
                    cornerRadius + swell,
                    layerColor
            );
        }
    }

    private static LayerProfile resolveProfile(float blurRadius) {
        int key = Math.max(0, Math.round(blurRadius * 100f));
        LayerProfile cached = PROFILE_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        int layers = Math.min(32, Math.max(8, Math.round(blurRadius * 1.5f)));
        float sigma = Math.max(0.001f, blurRadius * 0.5f);

        List<Float> offsetList = new ArrayList<>(layers);
        List<Float> weightList = new ArrayList<>(layers);
        float total = 0f;
        for (int i = 0; i < layers; i++) {
            float t = i / (float) (layers - 1);
            float distance = t * blurRadius;
            float weight = (float) Math.exp(-(distance * distance) / (2f * sigma * sigma));
            if (weight <= 1e-5f) continue;
            offsetList.add(distance);
            weightList.add(weight);
            total += weight;
        }

        if (offsetList.isEmpty()) {
            offsetList.add(0f);
            weightList.add(1f);
            total = 1f;
        }

        float[] offsets = new float[offsetList.size()];
        float[] weights = new float[weightList.size()];
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = offsetList.get(i);
            weights[i] = weightList.get(i) / total;
        }

        LayerProfile profile = new LayerProfile(blurRadius, offsets, weights);
        PROFILE_CACHE.put(key, profile);
        return profile;
    }

    private record LayerProfile(float blurRadius, float[] offsets, float[] weights) {
    }
}


