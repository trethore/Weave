package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class Panel extends Component<Panel> {
    private Color color = Color.WHITE;

    @Override
    public void draw(DrawContext context) {
        Render2DUtils.drawRect(context, getLeft(), getTop(), getWidth(), getHeight(), color);

        for (Component child : children) {
            child.draw(context);
        }
    }

    public Panel setColor(Color color) {
        this.color = color;
        return this;
    }

    public static Panel create() {
        return new Panel();
    }
}