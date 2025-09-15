package tytoo.weave.style;

import tytoo.weave.style.contract.StyleSlot;

import java.awt.*;

public final class LayoutStyleProperties {
    public static final StyleSlot PADDING = StyleSlot.forRoot("padding", EdgeInsets.class);
    public static final StyleSlot MARGIN = StyleSlot.forRoot("margin", EdgeInsets.class);

    public static final StyleSlot BORDER_WIDTH = StyleSlot.forRoot("border-width", Float.class);
    public static final StyleSlot BORDER_COLOR = StyleSlot.forRoot("border-color", Color.class);
    public static final StyleSlot BORDER_RADIUS = StyleSlot.forRoot("border-radius", Float.class);
    public static final StyleSlot OVERLAY_BORDER_WIDTH = StyleSlot.forRoot("overlay-border-width", Float.class);
    public static final StyleSlot OVERLAY_BORDER_COLOR = StyleSlot.forRoot("overlay-border-color", Color.class);
    public static final StyleSlot OVERLAY_BORDER_RADIUS = StyleSlot.forRoot("overlay-border-radius", Float.class);

    public static final StyleSlot WIDTH = StyleSlot.forRoot("width", Float.class);
    public static final StyleSlot HEIGHT = StyleSlot.forRoot("height", Float.class);
    public static final StyleSlot MIN_WIDTH = StyleSlot.forRoot("min-width", Float.class);
    public static final StyleSlot MAX_WIDTH = StyleSlot.forRoot("max-width", Float.class);
    public static final StyleSlot MIN_HEIGHT = StyleSlot.forRoot("min-height", Float.class);
    public static final StyleSlot MAX_HEIGHT = StyleSlot.forRoot("max-height", Float.class);

    private LayoutStyleProperties() {
    }
}
