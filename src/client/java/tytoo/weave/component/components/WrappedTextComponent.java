package tytoo.weave.component.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.List;

public class WrappedTextComponent extends TextComponent {
    public WrappedTextComponent(Text text) {
        super(text);

        this.constraints.setHeight(component -> {
            TextRenderer textRenderer = ThemeManager.getTheme().getTextRenderer();
            int lines = textRenderer.wrapLines(text, (int) component.getWidth()).size();
            return (float) lines * textRenderer.fontHeight;
        });
    }

    public static WrappedTextComponent of(String text) {
        return new WrappedTextComponent(Text.of(text));
    }

    public static WrappedTextComponent of(Text text) {
        return new WrappedTextComponent(text);
    }

    @Override
    public void draw(DrawContext context) {
        TextRenderer textRenderer = ThemeManager.getTheme().getTextRenderer();
        List<OrderedText> lines = textRenderer.wrapLines(this.text, (int) this.getWidth());

        Color drawColor = this.color != null ? this.color : ThemeManager.getTheme().getTextColor();
        boolean shadow = this.hasShadow != null ? this.hasShadow : ThemeManager.getTheme().isTextShadowed();

        int yOffset = 0;
        for (OrderedText line : lines) {
            context.drawText(
                    textRenderer,
                    line,
                    (int) getLeft(),
                    (int) getTop() + yOffset,
                    drawColor.getRGB(),
                    shadow
            );
            yOffset += textRenderer.fontHeight;
        }
        drawChildren(context);
    }
}