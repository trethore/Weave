package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.style.Styling;
import tytoo.weave.utils.McUtils;

import java.awt.*;

public class DefaultTheme implements Theme {
    private static final Styling DEFAULT_TEXT_STYLE = Styling.create().color(Color.WHITE).shadow(true);

    @Override
    public TextRenderer getTextRenderer() {
        return McUtils.getMc().map(mc -> mc.textRenderer).orElse(null);
    }

    @Override
    public Styling getDefaultTextStyle() {
        return DEFAULT_TEXT_STYLE;
    }
}