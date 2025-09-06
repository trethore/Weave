package tytoo.weave.ui.popup;

import tytoo.weave.style.StyleProperty;

import java.awt.*;

public final class PopupStyleProperties {
    public static final StyleProperty<Color> BACKDROP_COLOR = new StyleProperty<>("popup.backdrop.color", Color.class);
    public static final StyleProperty<Float> BACKDROP_OPACITY = new StyleProperty<>("popup.backdrop.opacity", Float.class);
    public static final StyleProperty<Boolean> BACKDROP_CLICK_THROUGH = new StyleProperty<>("popup.backdrop.clickThrough", Boolean.class);
    public static final StyleProperty<Float> BACKDROP_BLUR_RADIUS = new StyleProperty<>("popup.backdrop.blurRadius", Float.class);

    private PopupStyleProperties() {
    }
}

