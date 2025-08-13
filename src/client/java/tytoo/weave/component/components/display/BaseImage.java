package tytoo.weave.component.components.display;

import net.minecraft.util.Identifier;
import tytoo.weave.component.Component;
import tytoo.weave.state.State;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class BaseImage<T extends BaseImage<T>> extends Component<T> {
    private final State<Color> colorState = new State<>(Color.WHITE);
    private Identifier imageId;
    private int imageRotation = 0;
    private boolean parity = false;

    protected BaseImage(Identifier imageId) {
        this.imageId = imageId;
        getStyle().setBaseRenderer((context, component) -> {
            if (component instanceof BaseImage<?> img) {
                Render2DUtils.drawImage(
                        context, img.getImageId(),
                        img.getLeft(), img.getTop(),
                        img.getLeft() + img.getWidth(), img.getTop() + img.getHeight(),
                        img.getImageRotation(), img.isParity(), img.getColor()
                );
            }
        });
    }

    public static BaseImage<?> of(Identifier imageId) {
        return new BaseImage<>(imageId);
    }

    public Identifier getImageId() {
        return imageId;
    }

    public Color getColor() {
        return colorState.get();
    }

    public T setColor(Color color) {
        this.colorState.set(color);
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

    public State<Color> getColorState() {
        return colorState;
    }
}