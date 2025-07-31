package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.style.Styling;

public interface Theme {
    Stylesheet getStylesheet();

    TextRenderer getTextRenderer();

    Styling getDefaultTextStyle();
}