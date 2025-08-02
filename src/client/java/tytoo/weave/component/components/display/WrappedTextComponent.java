package tytoo.weave.component.components.display;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class WrappedTextComponent extends TextComponent {
    protected WrappedTextComponent(Text text) {
        super(text);

        this.getLayoutState().constraints.setHeight((c, parentHeight) -> {
            TextRenderer textRenderer = getEffectiveTextRenderer();

            if (this.getWidth() <= 0 || this.scale <= 0) return 0f;

            Text textToWrap = this.getDrawableText();
            int wrapWidth = (int) (this.getWidth() / this.scale);
            int lines = textRenderer.wrapLines(textToWrap, wrapWidth).size();
            return (float) lines * textRenderer.fontHeight * this.scale;
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