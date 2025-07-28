package tytoo.weave.effects;

import tytoo.weave.effects.implementations.OutlineEffect;
import tytoo.weave.effects.implementations.ScissorEffect;

import java.awt.*;

@SuppressWarnings("unused")
public final class Effects {
    private Effects() {
    }

    public static Effect scissor() {
        return new ScissorEffect();
    }

    public static Effect outline(Color color, float width) {
        return new OutlineEffect(color, width, true);
    }

    public static Effect outline(Color color, float width, boolean inside) {
        return new OutlineEffect(color, width, inside);
    }
}