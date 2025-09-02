package tytoo.weave.style.renderer;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class ParentStyledColorRenderer implements ComponentRenderer {
    private final StyleProperty<Color> colorProperty;
    private final Color defaultColor;

    public ParentStyledColorRenderer(StyleProperty<Color> colorProperty, Color defaultColor) {
        this.colorProperty = colorProperty;
        this.defaultColor = defaultColor;
    }

    @Override
    public void render(DrawContext context, Component<?> component) {
        Component<?> parent = component.getParent();
        Color color = ThemeManager.getStylesheet().get(parent != null ? parent : component, colorProperty, defaultColor);
        if (color != null) {
            Render2DUtils.drawRect(context, component.getLeft(), component.getTop(), component.getWidth(), component.getHeight(), color);
        }
    }
}

