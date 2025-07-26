package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.utils.McUtils;

import java.awt.*;

public class DefaultTheme implements Theme {
    @Override
    public TextRenderer getTextRenderer() {
        return McUtils.getMc().map(mc -> mc.textRenderer).orElse(null);
    }

    @Override
    public Color getPanelColor() {
        return null;
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public boolean isTextShadowed() {
        return false;
    }

    @Override
    public Color getWindowColor() {
        return new Color(0, 0, 0, 100);
    }
}