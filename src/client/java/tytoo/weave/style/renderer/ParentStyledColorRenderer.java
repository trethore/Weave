package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public record ParentStyledColorRenderer(StyleProperty<Color> colorProperty,
                                        Color defaultColor) implements ComponentRenderer {

    @Override
    public void render(DrawContext context, Component<?> component) {
        Component<?> parent = component.getParent();
        Color color = ThemeManager.getStylesheet().get(parent != null ? parent : component, colorProperty, defaultColor);
        if (color != null) {
            Render2DUtils.drawRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), color);
        }
    }
}
