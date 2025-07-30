package tytoo.weave.component.components.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import tytoo.weave.component.Component;
import tytoo.weave.style.Styling;
import tytoo.weave.style.TextSegment;
import tytoo.weave.theme.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextComponent extends Component<TextComponent> {
    protected List<TextSegment> segments = new ArrayList<>();
    protected Styling baseStyle;
    protected Styling hoverStyle;
    protected float scale = 1.0f;

    private Text cachedText = null;
    private int lastHoverState = -1; // -1: initial, 0: not hovered, 1: hovered

    public TextComponent(Text text) {
        parseText(text);

        this.constraints.setWidth((component, parentWidth) ->
                getEffectiveTextRenderer().getWidth(getDrawableText()) * scale
        );
        this.constraints.setHeight((component, parentHeight) ->
                (float) getEffectiveTextRenderer().fontHeight * scale
        );
    }

    public static TextComponent of(String text) {
        return new TextComponent(Text.of(text));
    }

    public static TextComponent of(Text text) {
        return new TextComponent(text);
    }

    private void parseText(Text text) {
        this.segments.clear();
        text.visit((style, string) -> {
            segments.add(new TextSegment(string, Styling.fromMinecraftStyle(style)));
            return Optional.empty();
        }, net.minecraft.text.Style.EMPTY);
        invalidateCache();
    }

    private void invalidateCache() {
        this.cachedText = null;
    }

    protected Text getDrawableText() {
        boolean isHovered = isHovered();
        int currentHoverState = isHovered ? 1 : 0;

        if (cachedText != null && lastHoverState == currentHoverState) return cachedText;

        MutableText composedText = Text.empty();
        Styling themeStyle = ThemeManager.getTheme().getDefaultTextStyle();

        for (TextSegment segment : segments) {
            Styling finalStyle = themeStyle.mergeWith(segment.getFormatting());

            if (baseStyle != null) {
                finalStyle = finalStyle.mergeWith(baseStyle);
            }

            if (isHovered) {
                if (hoverStyle != null) finalStyle = finalStyle.mergeWith(hoverStyle);
                if (segment.getHoverStyling() != null) finalStyle = finalStyle.mergeWith(segment.getHoverStyling());
            }
            composedText.append(Text.literal(segment.getText()).setStyle(finalStyle.toMinecraftStyle()));
        }

        this.cachedText = composedText;
        this.lastHoverState = currentHoverState;
        return composedText;
    }

    protected boolean hasShadow() {
        boolean isHovered = isHovered();
        if (isHovered && hoverStyle != null && hoverStyle.isShadowSet()) {
            return hoverStyle.hasShadow();
        }
        if (baseStyle != null && baseStyle.isShadowSet()) {
            return baseStyle.hasShadow();
        }
        return ThemeManager.getTheme().getDefaultTextStyle().hasShadow();
    }

    @Override
    public void draw(DrawContext context) {
        context.getMatrices().push();
        context.getMatrices().translate(getLeft(), getTop(), 0);
        context.getMatrices().scale(this.scale, this.scale, 1.0f);

        drawScaledContent(context, getDrawableText(), hasShadow());

        context.getMatrices().pop();
        drawChildren(context);
    }

    protected void drawScaledContent(DrawContext context, Text text, boolean shadow) {
        context.drawText(
                getEffectiveTextRenderer(),
                text,
                0, 0, -1, shadow
        );
    }

    @Override
    public TextComponent clone() {
        TextComponent clone = super.clone();

        clone.segments = new ArrayList<>(this.segments);
        clone.invalidateCache();
        return clone;
    }

    public TextComponent setText(Text text) {
        parseText(text);
        return this;
    }

    public TextComponent setText(String text) {
        this.segments.clear();
        this.segments.add(new TextSegment(text, Styling.create()));
        invalidateCache();
        return this;
    }

    public TextSegment append(String text) {
        return append(text, Styling.create());
    }

    public TextSegment append(String text, Styling styling) {
        TextSegment segment = new TextSegment(text, styling);
        this.segments.add(segment);
        invalidateCache();
        return segment;
    }

    public TextSegment append(String text, Styling styling, Styling hoverStyling) {
        TextSegment segment = new TextSegment(text, styling, hoverStyling);
        this.segments.add(segment);
        invalidateCache();
        return segment;
    }

    public TextComponent setStyle(Styling style) {
        this.baseStyle = style;
        invalidateCache();
        return this;
    }

    public TextComponent setHoverStyle(Styling style) {
        this.hoverStyle = style;
        invalidateCache();
        return this;
    }

    public TextComponent setScale(float scale) {
        this.scale = scale;
        invalidateLayout();
        return this;
    }
}