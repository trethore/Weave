package tytoo.weave.component.components.display;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class WrappedTextComponent extends TextComponent<WrappedTextComponent> {

    protected WrappedTextComponent(Text text) {
        super(text);

        this.getLayoutState().constraints.setHeight((c, parentHeight) -> {
            TextRenderer textRenderer = getEffectiveTextRenderer();

            if (c.getMeasuredWidth() <= 0) {
                return 0f;
            }

            Text textToWrap = this.getDrawableText();

            int wrapWidth = (int) (c.getMeasuredWidth() / c.getScaleX());
            int lines = textRenderer.wrapLines(textToWrap, wrapWidth).size();
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
    protected void drawScaledContent(DrawContext context, Text text, boolean shadow) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        Text drawableText = getDrawableText();
        int wrapWidth = (int) (getMeasuredWidth() / getScaleX());

        if (wrapWidth <= 0) {
            return;
        }

        float drawX = getLeft();
        float drawY = getTop();

        List<OrderedText> lines = textRenderer.wrapLines(drawableText, wrapWidth);

        float yOffset = drawY;
        for (OrderedText line : lines) {
            context.drawText(
                    textRenderer,
                    line,
                    (int) drawX,
                    (int) yOffset,
                    -1,
                    shadow
            );
            yOffset += textRenderer.fontHeight;
        }
    }

}