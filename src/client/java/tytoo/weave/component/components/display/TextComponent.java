package tytoo.weave.component.components.display;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import tytoo.weave.component.Component;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.Styling;
import tytoo.weave.style.TextSegment;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.RenderTextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextComponent<T extends TextComponent<T>> extends Component<T> {
    private List<TextSegment> segments = new ArrayList<>();
    private Styling baseStyle;
    private Styling hoverStyle;

    private transient Text cachedText = null;
    private transient int lastHoverState = -1; // -1: initial, 0: not hovered, 1: hovered

    protected TextComponent(Text text) {
        parseText(text);

        this.getConstraints().setWidth((component, parentWidth) ->
                (float) getEffectiveTextRenderer().getWidth(getDrawableText())
        );
        this.getConstraints().setHeight((component, parentHeight) ->
                (float) getEffectiveTextRenderer().fontHeight
        );
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
        if (!isVisible() || getOpacity() <= 0.001f) {
            return;
        }

        float[] lastColor = RenderSystem.getShaderColor().clone();
        RenderSystem.setShaderColor(lastColor[0], lastColor[1], lastColor[2], lastColor[3] * getOpacity());

        context.getMatrices().push();
        try {
            applyTransformations(context);

            ComponentRenderer renderer = style.getRenderer(this);
            if (renderer != null) renderer.render(context, this);

            Styling activeStyling = getActiveStyling();
            ColorWave colorWave = activeStyling != null ? activeStyling.getColorWave() : null;

            if (colorWave != null) {
                StringBuilder sb = new StringBuilder();
                for (TextSegment segment : segments) {
                    sb.append(segment.getText());
                }
                drawWaveText(context, sb.toString(), hasShadow(), colorWave);
            } else {
                drawScaledContent(context, getDrawableText(), hasShadow());
            }

            drawChildren(context);
        } finally {
            context.getMatrices().pop();
            RenderSystem.setShaderColor(lastColor[0], lastColor[1], lastColor[2], lastColor[3]);
        }
    }

    private void drawWaveText(DrawContext context, String text, boolean shadow, ColorWave wave) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        RenderTextUtils.drawWaveText(context, textRenderer, text, getLeft(), getTop(), getWidth(), shadow, wave);
    }

    private Styling getActiveStyling() {
        Styling finalStyle = ThemeManager.getTheme().getDefaultTextStyle();
        if (baseStyle != null) finalStyle = finalStyle.mergeWith(baseStyle);
        if (isHovered() && hoverStyle != null) finalStyle = finalStyle.mergeWith(hoverStyle);
        return finalStyle;
    }

    protected void drawScaledContent(DrawContext context, Text text, boolean shadow) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        RenderTextUtils.drawText(context, textRenderer, text, getLeft(), getTop(), shadow);
    }

    @Override
    public T clone() {
        T clone = super.clone();

        ((TextComponent<?>) clone).segments = new ArrayList<>(this.segments);
        ((TextComponent<?>) clone).invalidateCache();
        return clone;
    }

    public T setText(Text text) {
        parseText(text);
        return self();
    }

    public T setText(String text) {
        this.segments.clear();
        this.segments.add(new TextSegment(text, Styling.create()));
        invalidateCache();
        return self();
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

    public T setStyle(Styling style) {
        this.baseStyle = style;
        invalidateCache();
        return self();
    }

    public Styling getBaseStyle() {
        return this.baseStyle;
    }

    public List<TextSegment> getSegments() {
        return segments;
    }

    public Styling getHoverStyle() {
        return hoverStyle;
    }

    public T setHoverStyle(Styling style) {
        this.hoverStyle = style;
        invalidateCache();
        return self();
    }
}