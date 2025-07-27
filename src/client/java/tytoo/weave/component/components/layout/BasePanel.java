package tytoo.weave.component.components.layout;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class BasePanel<T extends BasePanel<T>> extends Component<T> {
    protected Color color;
    protected Color normalColor;
    protected Color hoveredColor;
    protected Color focusedColor;

    @Override
    public void draw(DrawContext context) {
        Color drawColor = getDrawColor();
        if (drawColor != null) {
            Render2DUtils.drawRect(context, getLeft(), getTop(), getWidth(), getHeight(), drawColor);
        }
        drawChildren(context);
    }

    protected Color getDrawColor() {
        if (this.color != null) return this.color;

        if (isFocused() && this.focusedColor != null) {
            return this.focusedColor;
        }

        if (isHovered() && this.hoveredColor != null) {
            return this.hoveredColor;
        }

        if (this.normalColor != null) {
            return this.normalColor;
        }

        return ThemeManager.getTheme().getPanelColor();
    }

    @SuppressWarnings("unchecked")
    public T setColor(Color color) {
        this.color = color;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setNormalColor(Color color) {
        this.normalColor = color;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setHoveredColor(Color color) {
        this.hoveredColor = color;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setFocusedColor(Color color) {
        this.focusedColor = color;
        return (T) this;
    }
}