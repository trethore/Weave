package tytoo.weave.style;

import tytoo.weave.component.Component;
import tytoo.weave.style.contract.ComponentStyleRegistry;
import tytoo.weave.style.contract.StyleProperty;

import java.awt.*;

public final class LayoutStyleProperties {
    public static final StyleProperty<EdgeInsets> PADDING;
    public static final StyleProperty<EdgeInsets> MARGIN;

    public static final StyleProperty<Float> BORDER_WIDTH;
    public static final StyleProperty<Color> BORDER_COLOR;
    public static final StyleProperty<Float> BORDER_RADIUS;
    public static final StyleProperty<Float> OVERLAY_BORDER_WIDTH;
    public static final StyleProperty<Color> OVERLAY_BORDER_COLOR;
    public static final StyleProperty<Float> OVERLAY_BORDER_RADIUS;

    public static final StyleProperty<Float> WIDTH;
    public static final StyleProperty<Float> HEIGHT;
    public static final StyleProperty<Float> MIN_WIDTH;
    public static final StyleProperty<Float> MAX_WIDTH;
    public static final StyleProperty<Float> MIN_HEIGHT;
    public static final StyleProperty<Float> MAX_HEIGHT;

    static {
        ComponentStyleRegistry.Builder<Component<?>> builder = ComponentStyleRegistry.root("layout");
        PADDING = builder.optionalId("padding", EdgeInsets.class);
        MARGIN = builder.optionalId("margin", EdgeInsets.class);

        BORDER_WIDTH = builder.optionalId("border-width", Float.class);
        BORDER_COLOR = builder.optionalId("border-color", Color.class);
        BORDER_RADIUS = builder.optionalId("border-radius", Float.class);
        OVERLAY_BORDER_WIDTH = builder.optionalId("overlay-border-width", Float.class);
        OVERLAY_BORDER_COLOR = builder.optionalId("overlay-border-color", Color.class);
        OVERLAY_BORDER_RADIUS = builder.optionalId("overlay-border-radius", Float.class);

        WIDTH = builder.optionalId("width", Float.class);
        HEIGHT = builder.optionalId("height", Float.class);
        MIN_WIDTH = builder.optionalId("min-width", Float.class);
        MAX_WIDTH = builder.optionalId("max-width", Float.class);
        MIN_HEIGHT = builder.optionalId("min-height", Float.class);
        MAX_HEIGHT = builder.optionalId("max-height", Float.class);
        builder.register();
    }

    private LayoutStyleProperties() {
    }
}
