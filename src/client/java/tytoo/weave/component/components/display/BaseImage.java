package tytoo.weave.component.components.display;

import net.minecraft.util.Identifier;
import tytoo.weave.component.Component;

import java.awt.*;

public class BaseImage<T extends BaseImage<T>> extends Component<T> {
    protected Identifier imageId;
    protected Color color = Color.WHITE;
    protected int rotation = 0;
    protected boolean parity = false;

    public BaseImage(Identifier imageId) {
        this.imageId = imageId;
        this.style.setBaseRenderer((context, component) -> tytoo.weave.utils.render.Render2DUtils.drawImage(
                context, this.imageId,
                component.getLeft(), component.getTop(),
                component.getLeft() + component.getWidth(), component.getTop() + component.getHeight(),
                this.rotation, this.parity, this.color
        ));
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