package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.utils.render.Render2DUtils;

public class AntialiasingEffect implements Effect {
    private final int segmentsPer90;

    public AntialiasingEffect(int segmentsPer90) {
        this.segmentsPer90 = Math.max(1, segmentsPer90);
    }

    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        Render2DUtils.pushAntialiasSegmentsPer90(segmentsPer90);
    }

    @Override
    public void afterDraw(DrawContext context, Component<?> component) {
        Render2DUtils.popAntialiasSegmentsPer90();
    }
}
