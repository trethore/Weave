package tytoo.weave.style.renderer.textfield;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import tytoo.weave.component.components.interactive.BaseTextInput;
import tytoo.weave.component.components.interactive.TextArea;
import tytoo.weave.component.components.interactive.TextField;
import java.awt.*;

public class DefaultPlaceholderRenderer implements PlaceholderRenderer {
    private static final Color DEFAULT_PLACEHOLDER_COLOR = new Color(150, 150, 150);
    @Override
    public void render(DrawContext context, BaseTextInput<?> textInput) {
        if (!textInput.getText().isEmpty() || textInput.isFocused() || textInput.getPlaceholder() == null) {
            return;
        }

        TextRenderer textRenderer = textInput.getEffectiveTextRenderer();
        Color placeholderColor = textInput.getCachedStyleValue(BaseTextInput.StyleProps.PLACEHOLDER_COLOR, DEFAULT_PLACEHOLDER_COLOR);

        if (placeholderColor == null) return;

        if (textInput instanceof TextField textField) {
            float textY = textField.getInnerTop() + (textField.getInnerHeight() - (textRenderer.fontHeight - 1)) / 2.0f + 1f;

            int maxWidth = (int) textField.getInnerWidth();
            if (maxWidth <= 0) return;

            Text placeholder = textField.getPlaceholder();
            String toDraw = placeholder.getString();
            int textWidth = textRenderer.getWidth(toDraw);
            if (textWidth > maxWidth) {
                String ellipsis = "...";
                int ellipsisWidth = textRenderer.getWidth(ellipsis);
                int available = Math.max(0, maxWidth - ellipsisWidth);
                String trimmed = textRenderer.trimToWidth(toDraw, available);
                toDraw = trimmed + ellipsis;
            }

            context.drawText(textRenderer, Text.of(toDraw), (int) textField.getInnerLeft(), (int) textY, placeholderColor.getRGB(), true);
        } else if (textInput instanceof TextArea textArea) {
            int maxWidth = (int) textArea.getInnerWidth();
            if (maxWidth <= 0) return;

            float x = textArea.getInnerLeft();
            float y = textArea.getInnerTop() + 3;

            for (OrderedText line : textRenderer.wrapLines(textArea.getPlaceholder(), maxWidth)) {
                context.drawText(textRenderer, line, (int) x, (int) y, placeholderColor.getRGB(), true);
                y += textRenderer.fontHeight + 1;
            }
        }
    }
}
