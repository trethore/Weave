package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class ProgressBarRenderer implements ComponentRenderer, CloneableRenderer {
    @Override
    public void render(DrawContext context, Component<?> component) {
        if (!(component instanceof ProgressBar bar)) return;

        Stylesheet ss = ThemeManager.getStylesheet();
        Color bg = ss.get(bar, ProgressBar.StyleProps.BACKGROUND_COLOR, new Color(80, 80, 80));
        Color fg = ss.get(bar, ProgressBar.StyleProps.VALUE_COLOR, new Color(40, 160, 220));

        float x = bar.getLeft();
        float y = bar.getTop();
        float w = bar.getWidth();
        float h = bar.getHeight();

        if (bg != null) {
            Render2DUtils.drawRoundedRect(context, x, y, w, h, Math.min(h, 4f), bg);
        }

        float progress = Math.max(0.0f, Math.min(1.0f, bar.getVisualProgress()));
        if (progress <= 0.001f) return;
        if (fg == null) return;

        ProgressBar.FillPolicy policy = bar.getFillPolicy();
        float fillWidth = Math.max(0.0f, Math.min(w, w * progress));
        float fx;
        switch (policy) {
            case RIGHT_TO_LEFT -> fx = x + (w - fillWidth);
            case CENTER_OUT -> fx = x + (w - fillWidth) * 0.5f;
            default -> fx = x; // LEFT_TO_RIGHT or any unknown value
        }

        Render2DUtils.drawRoundedRect(context, fx, y, fillWidth, h, Math.min(h, 4f), fg);
    }

    @Override
    public ComponentRenderer copy() {
        return new ProgressBarRenderer();
    }
}

