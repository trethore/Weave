package tytoo.weave.component.components.display;

import net.minecraft.util.Identifier;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class BaseImage<T extends BaseImage<T>> extends Component<T> {
    protected Identifier imageId;
    protected Color color = Color.WHITE;
    protected int rotation = 0;
    protected boolean parity = false;

    protected BaseImage(Identifier imageId) {
        this.imageId = imageId;
        this.style.setBaseRenderer((context, component) -> Render2DUtils.drawImage(
                context, this.imageId,
                component.getLeft(), component.getTop(),
                component.getLeft() + component.getWidth(), component.getTop() + component.getHeight(),
                this.rotation, this.parity, this.color
        ));
    }

    public static BaseImage<?> of(Identifier imageId) {
        return new BaseImage<>(imageId);
    }

    @SuppressWarnings("unchecked")
    public T setColor(Color color) {
        this.color = color;
        invalidateLayout();
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
        invalidateLayout();
        return (T) this;
    }
}