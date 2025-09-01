package tytoo.weave.component.components.display;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.state.State;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.Styling;
import tytoo.weave.style.TextSegment;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.RenderTextUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TextComponent<T extends TextComponent<T>> extends Component<T> {
    private final State<Color> colorOverride = new State<>(null);
    private List<TextSegment> segments = new ArrayList<>();
    private transient Text cachedText = null;
    private transient int lastHoverState = -1; // -1: initial, 0: not hovered, 1: hovered
    private transient int lastStyleSignature = -1;

    protected TextComponent(Text text) {
        parseText(text);
        this.colorOverride.addListener(c -> invalidateCache());

        this.getConstraints().setWidth((component, parentWidth) -> {
            Stylesheet stylesheet = ThemeManager.getStylesheet();
            Float letterSpacing = stylesheet.get(this, StyleProps.LETTER_SPACING, null);
            return RenderTextUtils.getStyledTextWidth(getEffectiveTextRenderer(), getDrawableText(), letterSpacing);
        });
        this.getConstraints().setHeight((component, parentHeight) -> {
            TextRenderer textRenderer = getEffectiveTextRenderer();
            Stylesheet stylesheet = ThemeManager.getStylesheet();
            Float lineHeightMultiplier = stylesheet.get(this, StyleProps.LINE_HEIGHT_MULTIPLIER, 1.0f);
            return (float) textRenderer.fontHeight * (lineHeightMultiplier != null ? lineHeightMultiplier : 1.0f);
        });
    }

    private void parseText(Text text) {
        this.segments.clear();
        text.visit((style, string) -> {
            segments.add(new TextSegment(string, Styling.fromMinecraftStyle(style)));
            return Optional.empty();
        }, net.minecraft.text.Style.EMPTY);
        invalidateCache();
        invalidateLayout();
    }

    private void invalidateCache() {
        this.cachedText = null;
    }

    public Text getDrawableText() {
        boolean isHovered = isHovered();
        int currentHoverState = isHovered ? 1 : 0;

        int currentStyleSignature = getStyleSignature();
        if (cachedText != null && lastHoverState == currentHoverState && lastStyleSignature == currentStyleSignature) {
            return cachedText;
        }

        MutableText composedText = Text.empty();
        Styling baseStylingFromStylesheet = getBaseStylingFromStylesheet();

        for (TextSegment segment : segments) {
            Styling finalStyle = baseStylingFromStylesheet.mergeWith(segment.getFormatting());

            if (isHovered) {
                if (segment.getHoverStyling() != null) finalStyle = finalStyle.mergeWith(segment.getHoverStyling());
            }

            if (this.colorOverride.get() != null) {
                finalStyle = finalStyle.color(this.colorOverride.get());
            }

            Color c = finalStyle.getColor();
            if (c == null) {
                c = Color.WHITE;
            }

            int finalAlpha = (int) (c.getAlpha() * getOpacity());
            finalStyle = finalStyle.color(new Color(c.getRed(), c.getGreen(), c.getBlue(), finalAlpha));

            composedText.append(Text.literal(segment.getText()).setStyle(finalStyle.toMinecraftStyle()));
        }

        this.cachedText = composedText;
        this.lastHoverState = currentHoverState;
        this.lastStyleSignature = currentStyleSignature;
        return composedText;
    }

    @Override
    public void draw(DrawContext context) {
        if (!isVisible() || getOpacity() <= 0.001f) {
            return;
        }

        context.getMatrices().push();
        try {
            applyTransformations(context);

            ComponentRenderer renderer = getStyle().getRenderer(this);
            if (renderer != null) renderer.render(context, this);

            Stylesheet stylesheet = ThemeManager.getStylesheet();
            ColorWave colorWave = stylesheet.get(this, StyleProps.COLOR_WAVE, null);

            if (colorWave != null) {
                StringBuilder sb = new StringBuilder();
                for (TextSegment segment : segments) {
                    sb.append(segment.getText());
                }
                Float letterSpacing = stylesheet.get(this, StyleProps.LETTER_SPACING, null);
                drawWaveText(context, sb.toString(), hasShadow(), colorWave, letterSpacing, getOpacity());
            } else {
                drawScaledContent(context, getDrawableText(), hasShadow());
            }

            drawChildren(context);
        } finally {
            context.getMatrices().pop();
        }
    }

    @Override
    public TextRenderer getEffectiveTextRenderer() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        TextRenderer customFont = stylesheet.get(this, StyleProps.FONT, null);
        if (customFont != null) return customFont;

        return super.getEffectiveTextRenderer();
    }

    private int getStyleSignature() {
        Stylesheet ss = ThemeManager.getStylesheet();
        return Objects.hash(
                ss.get(this, StyleProps.TEXT_COLOR, null),
                ss.get(this, StyleProps.BOLD, null),
                ss.get(this, StyleProps.ITALIC, null),
                ss.get(this, StyleProps.UNDERLINE, null),
                ss.get(this, StyleProps.STRIKETHROUGH, null),
                ss.get(this, StyleProps.OBFUSCATED, null),
                ss.get(this, StyleProps.SHADOW, null),
                ss.get(this, StyleProps.SHADOW_COLOR, null),
                ss.get(this, StyleProps.COLOR_WAVE, null),
                ss.get(this, StyleProps.FONT, null),
                ss.get(this, StyleProps.LETTER_SPACING, null),
                ss.get(this, StyleProps.LINE_HEIGHT_MULTIPLIER, null),
                colorOverride.get(),
                getOpacity()
        );
    }

    private void drawWaveText(DrawContext context, String text, boolean shadow, ColorWave wave, @Nullable Float letterSpacing, float opacity) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        RenderTextUtils.drawWaveText(context, textRenderer, text, getLeft(), getTop(), getWidth(), shadow, wave, letterSpacing, opacity);
    }

    private Styling getBaseStylingFromStylesheet() {
        Stylesheet ss = ThemeManager.getStylesheet();
        return Styling.create()
                .color(ss.get(this, StyleProps.TEXT_COLOR, null))
                .bold(ss.get(this, StyleProps.BOLD, null))
                .italic(ss.get(this, StyleProps.ITALIC, null))
                .underline(ss.get(this, StyleProps.UNDERLINE, null))
                .strikethrough(ss.get(this, StyleProps.STRIKETHROUGH, null))
                .obfuscated(ss.get(this, StyleProps.OBFUSCATED, null))
                .shadow(ss.get(this, StyleProps.SHADOW, null))
                .shadowColor(ss.get(this, StyleProps.SHADOW_COLOR, null))
                .colorWave(ss.get(this, StyleProps.COLOR_WAVE, null))
                .font(ss.get(this, StyleProps.FONT, null))
                .letterSpacing(ss.get(this, StyleProps.LETTER_SPACING, null))
                .lineHeightMultiplier(ss.get(this, StyleProps.LINE_HEIGHT_MULTIPLIER, null));
    }

    protected boolean hasShadow() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        return stylesheet.get(this, StyleProps.SHADOW, false);
    }

    protected void drawScaledContent(DrawContext context, Text text, boolean shadow) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        Float letterSpacing = stylesheet.get(this, StyleProps.LETTER_SPACING, null);
        RenderTextUtils.drawText(context, textRenderer, text, getLeft(), getTop(), shadow, letterSpacing);
    }

    @Override
    public T clone() {
        T clone = super.clone();

        ((TextComponent<?>) clone).segments = new ArrayList<>(this.segments);
        ((TextComponent<?>) clone).colorOverride.set(this.colorOverride.get());
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
        invalidateLayout();
        return self();
    }

    public TextSegment append(String text) {
        return append(text, Styling.create());
    }

    public TextSegment append(String text, Styling styling) {
        TextSegment segment = new TextSegment(text, styling);
        this.segments.add(segment);
        invalidateCache();
        invalidateLayout();
        return segment;
    }

    public TextSegment append(String text, Styling styling, Styling hoverStyling) {
        TextSegment segment = new TextSegment(text, styling, hoverStyling);
        this.segments.add(segment);
        invalidateCache();
        invalidateLayout();
        return segment;
    }

    public List<TextSegment> getSegments() {
        return segments;
    }

    public State<Color> getColorOverrideState() {
        return this.colorOverride;
    }

    public static final class StyleProps {
        public static final StyleProperty<Color> TEXT_COLOR = new StyleProperty<>("text-color", Color.class);
        public static final StyleProperty<Boolean> BOLD = new StyleProperty<>("bold", Boolean.class);
        public static final StyleProperty<Boolean> ITALIC = new StyleProperty<>("italic", Boolean.class);
        public static final StyleProperty<Boolean> UNDERLINE = new StyleProperty<>("underline", Boolean.class);
        public static final StyleProperty<Boolean> STRIKETHROUGH = new StyleProperty<>("strikethrough", Boolean.class);
        public static final StyleProperty<Boolean> OBFUSCATED = new StyleProperty<>("obfuscated", Boolean.class);
        public static final StyleProperty<Boolean> SHADOW = new StyleProperty<>("shadow", Boolean.class);
        public static final StyleProperty<Color> SHADOW_COLOR = new StyleProperty<>("shadow-color", Color.class);
        public static final StyleProperty<ColorWave> COLOR_WAVE = new StyleProperty<>("color-wave", ColorWave.class);
        public static final StyleProperty<TextRenderer> FONT = new StyleProperty<>("font", TextRenderer.class);
        public static final StyleProperty<Float> LETTER_SPACING = new StyleProperty<>("letter-spacing", Float.class);
        public static final StyleProperty<Float> LINE_HEIGHT_MULTIPLIER = new StyleProperty<>("line-height-multiplier", Float.class);

        private StyleProps() {
        }
    }
}
