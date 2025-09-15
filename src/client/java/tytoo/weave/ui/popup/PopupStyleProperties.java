package tytoo.weave.ui.popup;

import tytoo.weave.style.contract.StyleSlot;

import java.awt.*;

public final class PopupStyleProperties {
    public static final StyleSlot BACKDROP_COLOR = StyleSlot.forRoot("popup.backdrop.color", Color.class);
    public static final StyleSlot BACKDROP_OPACITY = StyleSlot.forRoot("popup.backdrop.opacity", Float.class);
    public static final StyleSlot BACKDROP_CLICK_THROUGH = StyleSlot.forRoot("popup.backdrop.clickThrough", Boolean.class);
    public static final StyleSlot BACKDROP_BLUR_RADIUS = StyleSlot.forRoot("popup.backdrop.blurRadius", Float.class);

    private PopupStyleProperties() {
    }
}
