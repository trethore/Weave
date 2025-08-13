package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;

public interface Theme {
    Stylesheet getStylesheet();

    TextRenderer getTextRenderer();
}