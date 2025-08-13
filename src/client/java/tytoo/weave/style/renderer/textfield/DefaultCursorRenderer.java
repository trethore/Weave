package tytoo.weave.style.renderer.textfield;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.components.interactive.BaseTextInput;
import tytoo.weave.component.components.interactive.TextArea;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.List;

public class DefaultCursorRenderer implements CursorRenderer {
    @Override
    public void render(DrawContext context, BaseTextInput<?> textInput) {
        if (!textInput.isFocused()) return;

        long cursorBlinkInterval = ThemeManager.getStylesheet().get(textInput, BaseTextInput.StyleProps.CURSOR_BLINK_INTERVAL, 500L);
        if (textInput.getSelectionAnchor() != textInput.getCursorPos()) return;

        long timeSinceLastAction = System.currentTimeMillis() - textInput.getLastActionTime();
        boolean shouldDrawCursor = (timeSinceLastAction < cursorBlinkInterval) || (System.currentTimeMillis() / cursorBlinkInterval) % 2 == 0;

        if (shouldDrawCursor) {
            if (textInput instanceof TextField textField) {
                drawTextFieldCursor(context, textField);
            } else if (textInput instanceof TextArea textArea) {
                drawTextAreaCursor(context, textArea);
            }
        }
    }

    private void drawTextFieldCursor(DrawContext context, TextField textField) {
        TextRenderer textRenderer = textField.getEffectiveTextRenderer();
        float textY = textField.getInnerTop() + (textField.getInnerHeight() - (textRenderer.fontHeight - 1)) / 2.0f + 1f;
        String visibleText = textRenderer.trimToWidth(textField.getText().substring(textField.getFirstCharacterIndex()), (int) textField.getInnerWidth());

        int visibleCursorPos = textField.getCursorPos() - textField.getFirstCharacterIndex();
        if (visibleCursorPos < 0 || visibleCursorPos > visibleText.length()) return;

        String textBeforeCursor = visibleText.substring(0, visibleCursorPos);
        float cursorX = textField.getInnerLeft() + textRenderer.getWidth(textBeforeCursor);
        float cursorY = textY - 2;
        float cursorHeight = textRenderer.fontHeight + 1;
        Color cursorColor = ThemeManager.getStylesheet().get(textField, BaseTextInput.StyleProps.CURSOR_COLOR, Color.LIGHT_GRAY);

        if (cursorColor != null) {
            Render2DUtils.drawRect(context, cursorX, cursorY, 1, cursorHeight, cursorColor);
        }
    }

    private void drawTextAreaCursor(DrawContext context, TextArea textArea) {
        Point pos2d = textArea.getCursorPos2D(textArea.getCursorPos());
        TextRenderer textRenderer = textArea.getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;
        float yOffset = textArea.getInnerTop() + textArea.getScrollY() + 1;

        float lineY = yOffset + pos2d.y * lineHeight;

        if (lineY + lineHeight >= textArea.getInnerTop() && lineY <= textArea.getInnerTop() + textArea.getInnerHeight()) {
            List<String> lines = textArea.getLines();
            String lineToCursor = lines.get(pos2d.y).substring(0, pos2d.x);
            float cursorX = textArea.getInnerLeft() + textRenderer.getWidth(lineToCursor);
            Color cursorColor = ThemeManager.getStylesheet().get(textArea, BaseTextInput.StyleProps.CURSOR_COLOR, Color.LIGHT_GRAY);
            Render2DUtils.drawRect(context, cursorX, lineY, 1, lineHeight, cursorColor);
        }
    }
}