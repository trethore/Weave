package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

public class Window extends Component<Window> {
    public Window() {
        this.parent = this;
    }

    @Override
    public void draw(DrawContext context) {
        for (Component child : children) {
            child.draw(context);
        }
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