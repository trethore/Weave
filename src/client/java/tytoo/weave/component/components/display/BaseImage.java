package tytoo.weave.component.components.display;

import net.minecraft.util.Identifier;
import tytoo.weave.component.Component;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class BaseImage<T extends BaseImage<T>> extends Component<T> {
    private Identifier imageId;
    private Color color = Color.WHITE;
    private int imageRotation = 0;
    private boolean parity = false;

    protected BaseImage(Identifier imageId) {
        this.imageId = imageId;
        getStyle().setBaseRenderer((context, component) -> Render2DUtils.drawImage(
                context, this.getImageId(),
                component.getLeft(), component.getTop(),
                component.getLeft() + component.getWidth(), component.getTop() + component.getHeight(),
                this.getImageRotation(), this.isParity(), this.getColor()
        ));
    }

    public static BaseImage<?> of(Identifier imageId) {
        return new BaseImage<>(imageId);
    }

    public Identifier getImageId() {
        return imageId;
    }

    public Color getColor() {
        return color;
    }

    public T setColor(Color color) {
        this.color = color;
        invalidateLayout();
        return self();
    }

    public int getImageRotation() {
        return imageRotation;
    }

    public T setImageRotation(int rotation) {
        this.imageRotation = rotation;
        return self();
    }

    public boolean isParity() {
        return parity;
    }

    public T setParity(boolean parity) {
        this.parity = parity;
        return self();
    }

    public T setImage(Identifier imageId) {
        this.imageId = imageId;
        invalidateLayout();
        return self();
    }
}