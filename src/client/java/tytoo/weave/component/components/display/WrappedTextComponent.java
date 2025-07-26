package tytoo.weave.component.components.display;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import tytoo.weave.theme.ThemeManager;

import java.util.List;

public class WrappedTextComponent extends TextComponent {
    public WrappedTextComponent(Text text) {
        super(text);

        this.constraints.setHeight(component -> {
            TextRenderer textRenderer = ThemeManager.getTheme().getTextRenderer();
            WrappedTextComponent self = (WrappedTextComponent) component;

            if (self.getWidth() <= 0 || self.scale <= 0) return 0f;

            int wrapWidth = (int) (self.getWidth() / self.scale);
            int lines = textRenderer.wrapLines(self.text, wrapWidth).size();
            return (float) lines * textRenderer.fontHeight * self.scale;
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
        Text textToDraw = getDrawableText();
        java.awt.Color drawColor = getDrawableColor();
        boolean shadow = this.hasShadow != null ? this.hasShadow : ThemeManager.getTheme().isTextShadowed();

        int wrapWidth = (int) (this.getWidth() / this.scale);
        if (wrapWidth <= 0) {
            drawChildren(context);
            return;
        }
        List<OrderedText> lines = textRenderer.wrapLines(textToDraw, wrapWidth);

        context.getMatrices().push();
        context.getMatrices().translate(getLeft(), getTop(), 0);
        context.getMatrices().scale(this.scale, this.scale, 1.0f);

        int yOffset = 0;
        for (OrderedText line : lines) {
            context.drawText(
                    textRenderer,
                    line,
                    0,
                    yOffset,
                    drawColor.getRGB(),
                    shadow
            );
            yOffset += textRenderer.fontHeight;
        }

        context.getMatrices().pop();
        drawChildren(context);
    }
}