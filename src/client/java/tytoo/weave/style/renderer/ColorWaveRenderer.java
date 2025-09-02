package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.ColorWave;
import tytoo.weave.utils.render.Render2DUtils;

public record ColorWaveRenderer(ColorWave wave, float angle) implements ComponentRenderer {

    @Override
    public void render(DrawContext context, Component<?> component) {
        double timeCycle = ((System.currentTimeMillis() / 1000.0) * wave.speed()) % 1.0;
        Render2DUtils.drawGradientRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), wave, (float) timeCycle, angle);
    }
}
