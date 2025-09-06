package tytoo.weave.ui.popup;

import org.jetbrains.annotations.NotNull;
import tytoo.weave.component.Component;

public record Anchor(Component<?> target, Side side, Align align, float offsetX, float offsetY, float gap) {
    public Anchor(@NotNull Component<?> target, Side side, Align align, float offsetX, float offsetY, float gap) {
        this.target = target;
        this.side = side == null ? Side.BOTTOM : side;
        this.align = align == null ? Align.START : align;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.gap = gap;
    }

    public static Anchor belowStart(Component<?> target) {
        return new Anchor(target, Side.BOTTOM, Align.START, 0f, 0f, 0f);
    }

    public enum Side {TOP, BOTTOM, LEFT, RIGHT}

    public enum Align {START, CENTER, END}
}

