package tytoo.weave.style;

import tytoo.weave.animation.EasingFunction;
import tytoo.weave.component.Component;
import tytoo.weave.style.contract.ComponentStyleRegistry;
import tytoo.weave.style.contract.StyleProperty;
import tytoo.weave.ui.CursorType;

import java.awt.*;

public final class CommonStyleProperties {
    public static final StyleProperty<CursorType> CURSOR;
    public static final StyleProperty<Color> ACCENT_COLOR;
    public static final StyleProperty<Long> TRANSITION_DURATION;
    public static final StyleProperty<EasingFunction> TRANSITION_EASING;
    public static final StyleProperty<Float> SCROLL_AMOUNT;

    static {
        ComponentStyleRegistry.Builder<Component<?>> builder = ComponentStyleRegistry.root("common");
        CURSOR = builder.optionalId("cursor", CursorType.class);
        ACCENT_COLOR = builder.optionalId("accent-color", Color.class);
        TRANSITION_DURATION = builder.optionalId("transition-duration", Long.class);
        TRANSITION_EASING = builder.optionalId("transition-easing", EasingFunction.class);
        SCROLL_AMOUNT = builder.optionalId("scroll-amount", Float.class);
        builder.register();
    }

    private CommonStyleProperties() {
    }
}
