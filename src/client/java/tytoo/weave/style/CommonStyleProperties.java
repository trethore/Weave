package tytoo.weave.style;

import tytoo.weave.animation.EasingFunction;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.ui.CursorType;

import java.awt.*;

public final class CommonStyleProperties {
    public static final StyleSlot CURSOR = StyleSlot.forRoot("cursor", CursorType.class);
    public static final StyleSlot ACCENT_COLOR = StyleSlot.forRoot("accent-color", Color.class);
    public static final StyleSlot TRANSITION_DURATION = StyleSlot.forRoot("transition-duration", Long.class);
    public static final StyleSlot TRANSITION_EASING = StyleSlot.forRoot("transition-easing", EasingFunction.class);
    public static final StyleSlot SCROLL_AMOUNT = StyleSlot.forRoot("scroll-amount", Float.class);

    private CommonStyleProperties() {
    }
}
