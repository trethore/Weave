package tytoo.weave.style;

import tytoo.weave.animation.EasingFunction;
import tytoo.weave.ui.CursorType;

import java.awt.*;

public final class CommonStyleProperties {
    public static final StyleProperty<CursorType> CURSOR = new StyleProperty<>("cursor", CursorType.class);
    public static final StyleProperty<Color> ACCENT_COLOR = new StyleProperty<>("accent-color", Color.class);
    public static final StyleProperty<Long> TRANSITION_DURATION = new StyleProperty<>("transition-duration", Long.class);
    public static final StyleProperty<EasingFunction> TRANSITION_EASING = new StyleProperty<>("transition-easing", EasingFunction.class);
    public static final StyleProperty<Float> SCROLL_AMOUNT = new StyleProperty<>("scroll-amount", Float.class);

    private CommonStyleProperties() {
    }
}
