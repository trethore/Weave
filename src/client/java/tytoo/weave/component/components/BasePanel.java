package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class BasePanel<T extends BasePanel<T>> extends Component<T> {
    protected Color color;

    @Override
    public void draw(DrawContext context) {
        Color drawColor = this.color != null ? this.color : ThemeManager.getTheme().getPanelColor();
        if (drawColor != null) {
            Render2DUtils.drawRect(context, getLeft(), getTop(), getWidth(), getHeight(), drawColor);
        }
        drawChildren(context);
    }

    @SuppressWarnings("unchecked")
    public T setColor(Color color) {
        this.color = color;
        return (T) this;
    }
}