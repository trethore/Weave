package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.ColorWave;
import tytoo.weave.utils.render.Render2DUtils;

public class ColorWaveRenderer implements ComponentRenderer {
    private final ColorWave wave;
    private final float angle;

    public ColorWaveRenderer(ColorWave wave, float angle) {
        this.wave = wave;
        this.angle = angle;
    }

    @Override
    public void render(DrawContext context, Component<?> component) {
        float offset = (System.currentTimeMillis() / 1000f) * wave.speed();
        Render2DUtils.drawGradientRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), wave, offset, angle);
    }
}