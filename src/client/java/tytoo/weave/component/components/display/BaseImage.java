package tytoo.weave.component.components.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class BaseImage<T extends BaseImage<T>> extends Component<T> {
    protected Identifier imageId;
    protected Color color = Color.WHITE;
    protected int rotation = 0;
    protected boolean parity = false;

    public BaseImage(Identifier imageId) {
        this.imageId = imageId;
    }

    @Override
    public void draw(DrawContext context) {
        Render2DUtils.drawImage(
                imageId,
                getLeft(),
                getTop(),
                getLeft() + getWidth(),
                getTop() + getHeight(),
                rotation,
                parity,
                color
        );
        drawChildren(context);
    }

    @SuppressWarnings("unchecked")
    public T setColor(Color color) {
        this.color = color;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setRotation(int rotation) {
        this.rotation = rotation;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setParity(boolean parity) {
        this.parity = parity;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setImage(Identifier imageId) {
        this.imageId = imageId;
        return (T) this;
    }
}