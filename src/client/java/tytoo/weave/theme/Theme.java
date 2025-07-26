package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;

import java.awt.*;

public interface Theme {
    TextRenderer getTextRenderer();

    Color getPanelColor();

    Color getTextColor();

    boolean isTextShadowed();

    Color getWindowColor();
}