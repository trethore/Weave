package tytoo.weave.style;

import java.awt.*;

public final class LayoutStyleProperties {
    public static final StyleProperty<EdgeInsets> PADDING = new StyleProperty<>("padding", EdgeInsets.class);
    public static final StyleProperty<EdgeInsets> MARGIN = new StyleProperty<>("margin", EdgeInsets.class);

    public static final StyleProperty<Float> BORDER_WIDTH = new StyleProperty<>("border-width", Float.class);
    public static final StyleProperty<Color> BORDER_COLOR = new StyleProperty<>("border-color", Color.class);
    public static final StyleProperty<Float> BORDER_RADIUS = new StyleProperty<>("border-radius", Float.class);
    public static final StyleProperty<Float> OVERLAY_BORDER_WIDTH = new StyleProperty<>("overlay-border-width", Float.class);
    public static final StyleProperty<Color> OVERLAY_BORDER_COLOR = new StyleProperty<>("overlay-border-color", Color.class);
    public static final StyleProperty<Float> OVERLAY_BORDER_RADIUS = new StyleProperty<>("overlay-border-radius", Float.class);

    public static final StyleProperty<Float> WIDTH = new StyleProperty<>("width", Float.class);
    public static final StyleProperty<Float> HEIGHT = new StyleProperty<>("height", Float.class);
    public static final StyleProperty<Float> MIN_WIDTH = new StyleProperty<>("min-width", Float.class);
    public static final StyleProperty<Float> MAX_WIDTH = new StyleProperty<>("max-width", Float.class);
    public static final StyleProperty<Float> MIN_HEIGHT = new StyleProperty<>("min-height", Float.class);
    public static final StyleProperty<Float> MAX_HEIGHT = new StyleProperty<>("max-height", Float.class);

    private LayoutStyleProperties() {
    }
}
