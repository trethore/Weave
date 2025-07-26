package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class Window extends BasePanel<Window> {
    public Window() {
        this.parent = null;
        this.setX(Constraints.center());
        this.setY(Constraints.center());
        this.setWidth(Constraints.pixels(400));
        this.setHeight(Constraints.pixels(300));
    }

    @Override
    public void draw(DrawContext context) {
        Color drawColor = this.color != null ? this.color : ThemeManager.getTheme().getWindowColor();
        if (drawColor != null) Render2DUtils.drawRect(context, getLeft(), getTop(), getWidth(), getHeight(), drawColor);
        drawChildren(context);
    }
}