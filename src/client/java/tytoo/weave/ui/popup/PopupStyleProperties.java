package tytoo.weave.ui.popup;

import tytoo.weave.component.Component;
import tytoo.weave.style.contract.ComponentStyleRegistry;
import tytoo.weave.style.contract.StyleProperty;

import java.awt.*;

public final class PopupStyleProperties {
    public static final StyleProperty<Color> BACKDROP_COLOR;
    public static final StyleProperty<Float> BACKDROP_OPACITY;
    public static final StyleProperty<Boolean> BACKDROP_CLICK_THROUGH;
    public static final StyleProperty<Float> BACKDROP_BLUR_RADIUS;

    static {
        ComponentStyleRegistry.Builder<Component<?>> builder = ComponentStyleRegistry.root("popup.backdrop");
        BACKDROP_COLOR = builder.optional("color", Color.class);
        BACKDROP_OPACITY = builder.optional("opacity", Float.class);
        BACKDROP_CLICK_THROUGH = builder.optional("clickThrough", Boolean.class);
        BACKDROP_BLUR_RADIUS = builder.optional("blurRadius", Float.class);
        builder.register();
    }

    private PopupStyleProperties() {
    }
}
