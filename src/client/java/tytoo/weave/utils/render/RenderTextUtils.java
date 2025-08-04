package tytoo.weave.utils.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.components.display.WrappedTextComponent;
import tytoo.weave.style.ColorWave;

import java.awt.*;
import java.util.List;

@SuppressWarnings("unused")
public final class RenderTextUtils {
    private RenderTextUtils() {
    }

    public static float getStyledTextWidth(TextRenderer textRenderer, Text text, @Nullable Float letterSpacing) {
        if (letterSpacing == null || letterSpacing == 0) {
            return textRenderer.getWidth(text);
        }
        final float[] width = {0f};
        text.asOrderedText().accept((index, style, codePoint) -> {
            Text charText = Text.literal(new String(Character.toChars(codePoint))).setStyle(style);
            width[0] += textRenderer.getWidth(charText);
            return true;
        });
        if (!text.getString().isEmpty()) {
            width[0] += letterSpacing * (text.getString().length() - 1);
        }
        return width[0];
    }

    public static void drawText(DrawContext context, TextRenderer textRenderer, Text text, float x, float y, boolean shadow, @Nullable Float letterSpacing) {
        if (letterSpacing == null || letterSpacing == 0) {
            context.drawText(
                    textRenderer,
                    text,
                    (int) x,
                    (int) y,
                    -1,
                    shadow
            );
            return;
        }

        final float[] currentX = {x};
        text.asOrderedText().accept((index, style, codePoint) -> {
            Text charText = Text.literal(new String(Character.toChars(codePoint))).setStyle(style);
            context.drawText(
                    textRenderer,
                    charText,
                    (int) currentX[0],
                    (int) y,
                    -1,
                    shadow
            );
            currentX[0] += textRenderer.getWidth(charText) + letterSpacing;
            return true;
        });
    }

    public static void drawWrappedText(DrawContext context, TextRenderer textRenderer, Text text, float x, float y, float wrapWidth, boolean shadow, WrappedTextComponent.Alignment alignment) {
        if (wrapWidth <= 0) {
            return;
        }

        List<OrderedText> lines = textRenderer.wrapLines(text, (int) wrapWidth);

        float yOffset = y;
        for (OrderedText line : lines) {
            float lineX = x;
            if (alignment != WrappedTextComponent.Alignment.LEFT) {
                int lineWidth = textRenderer.getWidth(line);
                if (alignment == WrappedTextComponent.Alignment.CENTER) {
                    lineX = x + (wrapWidth - lineWidth) / 2f;
                } else if (alignment == WrappedTextComponent.Alignment.RIGHT) {
                    lineX = x + wrapWidth - lineWidth;
                }
            }

            context.drawText(
                    textRenderer,
                    line,
                    (int) lineX,
                    (int) yOffset,
                    -1,
                    shadow
            );
            yOffset += textRenderer.fontHeight;
        }
    }

    public static void drawWaveText(DrawContext context, TextRenderer textRenderer, String text, float x, float y, float width, boolean shadow, ColorWave wave, @Nullable Float letterSpacing) {
        double timeCycle = ((System.currentTimeMillis() / 1000.0) * wave.speed()) % 1.0;

        if (width <= 0) {
            if (!text.isEmpty()) {
                Color color = wave.getColorAt((float) timeCycle);
                context.drawText(textRenderer, text, (int) x, (int) y, color.getRGB(), shadow);
            }
            return;
        }

        float currentX = x;
        for (int i = 0; i < text.length(); ++i) {
            String character = String.valueOf(text.charAt(i));
            float charWidth = textRenderer.getWidth(character);

            float charCenterPos = (currentX - x) + charWidth / 2f;
            float posCycle = charCenterPos / Math.max(1f, width);

            double totalCycle = timeCycle + posCycle;
            float cyclicProgress = (float) (totalCycle % 1.0);

            Color color = wave.getColorAt(cyclicProgress);
            context.drawText(textRenderer, character, (int) currentX, (int) y, color.getRGB(), shadow);
            currentX += charWidth + (letterSpacing != null ? letterSpacing : 0);
        }
    }
}