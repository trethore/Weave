package tytoo.weave.effects;

import tytoo.weave.effects.implementations.OutlineEffect;
import tytoo.weave.effects.implementations.ScissorEffect;
import tytoo.weave.effects.implementations.ShadowEffect;

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

    public static Effect shadow(Color color, float offsetX, float offsetY, float size, float cornerRadius) {
        return new ShadowEffect(color, offsetX, offsetY, size, cornerRadius);
    }

}