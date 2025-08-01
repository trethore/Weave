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
        double timeCycle = ((System.currentTimeMillis() / 1000.0) * wave.speed()) % 1.0;
        Render2DUtils.drawGradientRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), wave, (float) timeCycle, angle);
    }
}