package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.utils.McUtils;

public class Window extends Component<Window> {
    public Window() {
        this.parent = this;
    }


    @Override
    public void draw(DrawContext context) {
        for (Component<?> child : children) {
            child.draw(context);
        }
    }

    @Override
    public float getLeft() {
        return McUtils.getMc()
                .map(mc -> (mc.getWindow().getScaledWidth() - getWidth()) / 2f)
                .orElse(0f);
    }

    @Override
    public float getTop() {
        return McUtils.getMc()
                .map(mc -> (mc.getWindow().getScaledHeight() - getHeight()) / 2f)
                .orElse(0f);
    }

    @Override
    public float getWidth() {
        return 400;
    }

    @Override
    public float getHeight() {
        return 300;
    }
}