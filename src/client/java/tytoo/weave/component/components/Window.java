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
        // The window itself doesn't draw anything, but it draws its children.
        // We also need to call the super method to handle potential debug outlines or other effects in the future.
        // super.draw(context);

        // For now, let's keep it simple and just draw children.
        // The base Component draw method will be expanded later.

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

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        if (isPointInside(mouseX, mouseY)) {
            Component<?> target = hitTest(mouseX, mouseY);
            target.mouseClick(mouseX, mouseY, button);
        }
    }
}