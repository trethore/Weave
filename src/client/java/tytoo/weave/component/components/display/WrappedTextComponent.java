package tytoo.weave.component.components.display;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import tytoo.weave.utils.render.RenderTextUtils;

public class WrappedTextComponent extends TextComponent<WrappedTextComponent> {

    private Alignment textAlignment = Alignment.LEFT;

    protected WrappedTextComponent(Text text) {
        super(text);

        this.getConstraints().setHeight((c, parentHeight) -> {
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
        RenderTextUtils.drawWrappedText(context, textRenderer, drawableText, getLeft(), getTop(), wrapWidth, shadow, textAlignment);
    }

    public Alignment getAlignment() {
        return this.textAlignment;
    }

    public WrappedTextComponent setAlignment(Alignment alignment) {
        this.textAlignment = alignment;
        return this;
    }

    @Override
    public WrappedTextComponent clone() {
        WrappedTextComponent clone = super.clone();
        clone.textAlignment = this.textAlignment;
        return clone;
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

}
