package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import tytoo.weave.component.Component;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;

public class TextComponent extends Component<TextComponent> {
    protected Text text;
    protected Color color;
    protected Boolean hasShadow;

    public TextComponent(Text text) {
        this.text = text;

        this.constraints.setWidth(component ->
                ThemeManager.getTheme().getTextRenderer().getWidth(text)
        );
        this.constraints.setHeight(component ->
                (float) ThemeManager.getTheme().getTextRenderer().fontHeight
        );
    }

    public static TextComponent of(String text) {
        return new TextComponent(Text.of(text));
    }

    public static TextComponent of(Text text) {
        return new TextComponent(text);
    }

    @Override
    public void draw(DrawContext context) {
        Color drawColor = this.color != null ? this.color : ThemeManager.getTheme().getTextColor();
        boolean shadow = this.hasShadow != null ? this.hasShadow : ThemeManager.getTheme().isTextShadowed();

        context.drawText(
                ThemeManager.getTheme().getTextRenderer(),
                this.text,
                (int) getLeft(),
                (int) getTop(),
                drawColor.getRGB(),
                shadow
        );
        drawChildren(context);
    }

    public TextComponent setText(Text text) {
        this.text = text;
        return this;
    }

    public TextComponent setColor(Color color) {
        this.color = color;
        return this;
    }

    public TextComponent setShadow(boolean shadow) {
        this.hasShadow = shadow;
        return this;
    }
}