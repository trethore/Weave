package tytoo.weave.component.components.display;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class WrappedTextComponent extends TextComponent {
    protected WrappedTextComponent(Text text) {
        super(text);

        this.constraints.setHeight((c, parentHeight) -> {
            TextRenderer textRenderer = getEffectiveTextRenderer();
            WrappedTextComponent self = (WrappedTextComponent) c;

            if (self.getWidth() <= 0 || self.scale <= 0) return 0f;

            Text textToWrap = self.getDrawableText();
            int wrapWidth = (int) (self.getWidth() / self.scale);
            int lines = textRenderer.wrapLines(textToWrap, wrapWidth).size();
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
    protected void drawScaledContent(DrawContext context, Text text, boolean shadow) {
        TextRenderer textRenderer = getEffectiveTextRenderer();

        int wrapWidth = (int) (this.getWidth() / this.scale);
        if (wrapWidth <= 0) return;
        List<OrderedText> lines = textRenderer.wrapLines(text, wrapWidth);

        int yOffset = 0;
        for (OrderedText line : lines) {
            context.drawText(textRenderer,
                    line,
                    0, yOffset, -1, shadow);
            yOffset += textRenderer.fontHeight;
        }
    }
}