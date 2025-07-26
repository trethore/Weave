package tytoo.weave.component.components.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import tytoo.weave.component.Component;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.Optional;

public class TextComponent extends Component<TextComponent> {
    protected MutableText text;
    protected Color color;
    protected Color hoverColor;
    protected Boolean hasShadow;
    protected float scale = 1.0f;

    public TextComponent(Text text) {
        this.text = text.copy();

        this.constraints.setWidth(component ->
                ThemeManager.getTheme().getTextRenderer().getWidth(this.text) * this.scale
        );
        this.constraints.setHeight(component ->
                (float) ThemeManager.getTheme().getTextRenderer().fontHeight * this.scale
        );
    }

    public static TextComponent of(String text) {
        return new TextComponent(Text.of(text));
    }

    public static TextComponent of(Text text) {
        return new TextComponent(text);
    }

    protected Text getDrawableText() {
        if (isHovered() && this.hoverColor != null) {
            MutableText newText = Text.empty();
            TextColor newColor = TextColor.fromRgb(this.hoverColor.getRGB());
            this.text.visit((style, s) -> {
                newText.append(Text.literal(s).setStyle(style.withColor(newColor)));
                return Optional.empty();
            }, Style.EMPTY);
            return newText;
        }
        return this.text;
    }

    protected Color getDrawableColor() {
        if (isHovered() && this.hoverColor != null) {
            return this.hoverColor;
        }
        return this.color != null ? this.color : ThemeManager.getTheme().getTextColor();
    }

    @Override
    public void draw(DrawContext context) {
        Text textToDraw = getDrawableText();
        Color drawColor = getDrawableColor();
        boolean shadow = this.hasShadow != null ? this.hasShadow : ThemeManager.getTheme().isTextShadowed();

        context.getMatrices().push();
        context.getMatrices().translate(getLeft(), getTop(), 0);
        context.getMatrices().scale(this.scale, this.scale, 1.0f);

        context.drawText(
                ThemeManager.getTheme().getTextRenderer(),
                textToDraw,
                0,
                0,
                drawColor.getRGB(),
                shadow
        );

        context.getMatrices().pop();
        drawChildren(context);
    }

    public TextComponent setText(Text text) {
        this.text = text.copy();
        return this;
    }

    public TextComponent append(Text text) {
        this.text.append(text);
        return this;
    }

    public TextComponent setColor(Color color) {
        this.color = color;
        return this;
    }

    public TextComponent setHoverColor(Color color) {
        this.hoverColor = color;
        return this;
    }

    public TextComponent setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public TextComponent setShadow(boolean shadow) {
        this.hasShadow = shadow;
        return this;
    }
}