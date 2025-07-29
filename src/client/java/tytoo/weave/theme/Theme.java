package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.style.Styling;

public interface Theme {
    TextRenderer getTextRenderer();

    Styling getDefaultTextStyle();
}