package tytoo.weave.style;

import tytoo.weave.animation.Easing;
import tytoo.weave.ui.CursorType;

import java.awt.*;

public final class CommonStyleProperties {
    public static final StyleProperty<CursorType> CURSOR = new StyleProperty<>("cursor", CursorType.class);
    public static final StyleProperty<Color> ACCENT_COLOR = new StyleProperty<>("accent-color", Color.class);
    public static final StyleProperty<Long> TRANSITION_DURATION = new StyleProperty<>("transition-duration", Long.class);
    public static final StyleProperty<Easing.EasingFunction> TRANSITION_EASING = new StyleProperty<>("transition-easing", Easing.EasingFunction.class);

    private CommonStyleProperties() {
    }
}
