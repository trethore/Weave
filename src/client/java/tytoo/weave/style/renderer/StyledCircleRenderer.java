package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public record StyledCircleRenderer(StyleProperty<Color> colorProperty,
                                   Color defaultColor) implements ComponentRenderer {

    @Override
    public void render(DrawContext context, Component<?> component) {
        Color color = ThemeManager.getStylesheet().get(component, colorProperty, defaultColor);
        if (color == null) return;

        float x = component.getLeft();
        float y = component.getTop();
        float w = component.getWidth();
        float h = component.getHeight();

        float radius = Math.min(w, h) / 2.0f;
        float centerX = x + w / 2.0f;
        float centerY = y + h / 2.0f;

        Render2DUtils.drawCircle(context, centerX, centerY, radius, color);
    }
}

