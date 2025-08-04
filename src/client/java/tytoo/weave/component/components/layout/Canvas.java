package tytoo.weave.component.components.layout;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

import java.util.function.BiConsumer;

public class Canvas extends Component<Canvas> {
    private BiConsumer<DrawContext, Canvas> onDrawCallback;

    protected Canvas() {
    }

    public static Canvas create() {
        return new Canvas();
    }

    public Canvas onDraw(BiConsumer<DrawContext, Canvas> onDrawCallback) {
        this.onDrawCallback = onDrawCallback;
        return this;
    }

    @Override
    public void draw(DrawContext context) {
        if (!this.isVisible()) return;

        for (var effect : getRenderState().getEffects()) {
            effect.beforeDraw(context, this);
        }

        var renderer = getStyle().getRenderer(this);
        if (renderer != null) renderer.render(context, this);

        if (this.onDrawCallback != null) {
            this.onDrawCallback.accept(context, this);
        }

        drawChildren(context);

        for (int i = getRenderState().getEffects().size() - 1; i >= 0; i--) {
            getRenderState().getEffects().get(i).afterDraw(context, this);
        }
    }
}