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

public class DefaultSelectionRenderer implements SelectionRenderer {
    @Override
    public void render(DrawContext context, BaseTextInput<?> textInput) {
        int selectionStart = Math.min(textInput.getCursorPos(), textInput.getSelectionAnchor());
        int selectionEnd = Math.max(textInput.getCursorPos(), textInput.getSelectionAnchor());

        if (selectionStart == selectionEnd) return;

        if (textInput instanceof TextField textField) {
            drawTextFieldSelection(context, textField, selectionStart, selectionEnd);
        } else if (textInput instanceof TextArea textArea) {
            drawTextAreaSelection(context, textArea, selectionStart, selectionEnd);
        }
    }

    private void drawTextFieldSelection(DrawContext context, TextField textField, int selectionStart, int selectionEnd) {
        TextRenderer textRenderer = textField.getEffectiveTextRenderer();
        String visibleText = textRenderer.trimToWidth(textField.getText().substring(textField.getFirstCharacterIndex()), (int) textField.getInnerWidth());

        int visibleSelectionStart = Math.max(0, selectionStart - textField.getFirstCharacterIndex());
        int visibleSelectionEnd = Math.min(visibleText.length(), selectionEnd - textField.getFirstCharacterIndex());

        if (visibleSelectionStart < visibleSelectionEnd) {
            String preSelection = visibleText.substring(0, visibleSelectionStart);
            float highlightX1 = textField.getInnerLeft() + textRenderer.getWidth(preSelection);

            String selected = visibleText.substring(visibleSelectionStart, visibleSelectionEnd);
            float highlightX2 = highlightX1 + textRenderer.getWidth(selected);
            Color selectionColor = ThemeManager.getStylesheet().get(textField, BaseTextInput.StyleProps.SELECTION_COLOR, new Color(50, 100, 200, 128));

            float textY = textField.getInnerTop() + (textField.getInnerHeight() - (textRenderer.fontHeight - 1)) / 2.0f + 1f;
            float highlightY = textY - 2;
            float highlightHeight = textRenderer.fontHeight + 1;
            if (selectionColor != null) {
                Render2DUtils.drawRect(context, highlightX1, highlightY, highlightX2 - highlightX1, highlightHeight, selectionColor);
            }
        }
    }

    private void drawTextAreaSelection(DrawContext context, TextArea textArea, int selectionStart, int selectionEnd) {
        List<String> lines = textArea.getLines();
        TextRenderer textRenderer = textArea.getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;
        float yOffset = textArea.getInnerTop() + textArea.getScrollY() + 1;
        Color selectionColor = ThemeManager.getStylesheet().get(textArea, BaseTextInput.StyleProps.SELECTION_COLOR, new Color(50, 100, 200, 128));

        int absPos = 0;
        for (int i = 0; i < lines.size(); i++) {
            String lineText = lines.get(i);
            float lineY = yOffset + i * lineHeight;
            if (lineY + lineHeight < textArea.getInnerTop() || lineY > textArea.getInnerTop() + textArea.getInnerHeight()) {
                absPos += lineText.length() + 1;
                continue;
            }

            int lineStartAbs = absPos;
            int lineEndAbs = lineStartAbs + lineText.length();

            int selForLineStart = Math.max(selectionStart, lineStartAbs);
            int selForLineEnd = Math.min(selectionEnd, lineEndAbs);

            if (selForLineStart < selForLineEnd) {
                int colStart = selForLineStart - lineStartAbs;
                int colEnd = selForLineEnd - lineStartAbs;

                String preSelection = lineText.substring(0, colStart);
                float highlightX1 = textArea.getInnerLeft() + textRenderer.getWidth(preSelection);

                String selected = lineText.substring(colStart, colEnd);
                float highlightX2 = highlightX1 + textRenderer.getWidth(selected);

                Render2DUtils.drawRect(context, highlightX1, lineY, highlightX2 - highlightX1, lineHeight, selectionColor);
            }

            if (selectionEnd > lineEndAbs && selectionStart <= lineEndAbs) {
                float highlightX1 = textArea.getInnerLeft() + textRenderer.getWidth(lineText);
                float highlightWidth = 2;
                Render2DUtils.drawRect(context, highlightX1, lineY, highlightWidth, lineHeight, selectionColor);
            }

            absPos += lineText.length() + 1;
        }
    }
}