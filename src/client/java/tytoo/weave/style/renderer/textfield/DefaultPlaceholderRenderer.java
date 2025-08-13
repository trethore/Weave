package tytoo.weave.style.renderer.textfield;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.components.interactive.BaseTextInput;
import tytoo.weave.component.components.interactive.TextArea;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;

public class DefaultPlaceholderRenderer implements PlaceholderRenderer {
    @Override
    public void render(DrawContext context, BaseTextInput<?> textInput) {
        if (!textInput.getText().isEmpty() || textInput.isFocused() || textInput.getPlaceholder() == null) {
            return;
        }

        TextRenderer textRenderer = textInput.getEffectiveTextRenderer();
        Color placeholderColor = ThemeManager.getStylesheet().get(textInput, BaseTextInput.StyleProps.PLACEHOLDER_COLOR, new Color(150, 150, 150));

        if (placeholderColor == null) return;

        if (textInput instanceof TextField textField) {
            float textY = textField.getInnerTop() + (textField.getInnerHeight() - (textRenderer.fontHeight - 1)) / 2.0f + 1f;
            context.drawText(textRenderer, textField.getPlaceholder(), (int) textField.getInnerLeft(), (int) textY, placeholderColor.getRGB(), true);
        } else if (textInput instanceof TextArea textArea) {
            context.drawText(textRenderer, textArea.getPlaceholder(), (int) textArea.getInnerLeft(), (int) (textArea.getInnerTop() + 3), placeholderColor.getRGB(), true);
        }
    }
}