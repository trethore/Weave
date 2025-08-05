package tytoo.weave.style.renderer.textfield;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class DefaultCursorRenderer implements CursorRenderer {
    @Override
    public void render(DrawContext context, TextField textField) {
        if (!textField.isFocused()) return;

        long cursorBlinkInterval = ThemeManager.getStylesheet().get(textField, TextField.StyleProps.CURSOR_BLINK_INTERVAL, 500L);
        if (textField.hasSelection()) return;

        long timeSinceLastAction = System.currentTimeMillis() - textField.getLastActionTime();
        boolean shouldDrawCursor = (timeSinceLastAction < cursorBlinkInterval) || (System.currentTimeMillis() / cursorBlinkInterval) % 2 == 0;

        if (shouldDrawCursor) {
            drawCursor(context, textField);
        }
    }

    private void drawCursor(DrawContext context, TextField textField) {
        TextRenderer textRenderer = textField.getEffectiveTextRenderer();
        float textY = textField.getInnerTop() + (textField.getInnerHeight() - (textRenderer.fontHeight - 1)) / 2.0f + 1f;
        String visibleText = textRenderer.trimToWidth(textField.getText().substring(textField.getFirstCharacterIndex()), (int) textField.getInnerWidth());

        int visibleCursorPos = textField.getCursorPos() - textField.getFirstCharacterIndex();
        if (visibleCursorPos < 0 || visibleCursorPos > visibleText.length()) return;

        String textBeforeCursor = visibleText.substring(0, visibleCursorPos);
        float cursorX = textField.getInnerLeft() + textRenderer.getWidth(textBeforeCursor);
        float cursorY = textY - 2;
        float cursorHeight = textRenderer.fontHeight + 1;
        Color cursorColor = ThemeManager.getStylesheet().get(textField, TextField.StyleProps.CURSOR_COLOR, Color.LIGHT_GRAY);

        if (cursorColor != null) {
            Render2DUtils.drawRect(context, cursorX, cursorY, 1, cursorHeight, cursorColor);
        }
    }
}