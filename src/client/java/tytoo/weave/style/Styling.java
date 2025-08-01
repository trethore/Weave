package tytoo.weave.style;

import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.mixin.interfaces.IStyle;

import java.awt.*;

@SuppressWarnings("unused")
public class Styling {
    private final boolean reset;
    private final @Nullable Color color;
    private final @Nullable Boolean bold;
    private final @Nullable Boolean italic;
    private final @Nullable Boolean underline;
    private final @Nullable Boolean strikethrough;
    private final @Nullable Boolean obfuscated;
    private final @Nullable Boolean shadow;
    private final @Nullable Color shadowColor;
    private final @Nullable ColorWave colorWave;

    private Styling(boolean reset, @Nullable Color color, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underline, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable Boolean shadow, @Nullable Color shadowColor, @Nullable ColorWave colorWave) {
        this.reset = reset;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.shadow = shadow;
        this.shadowColor = shadowColor;
        this.colorWave = colorWave;
    }

    public static Styling create() {
        return new Styling(false, null, null, null, null, null, null, null, null, null);
    }

    public static Styling reset() {
        return new Styling(true, null, false, false, false, false, false, false, null, null);
    }

    public static Styling fromMinecraftStyle(Style style) {
        IStyle iStyle = (IStyle) style;
        TextColor textColor = style.getColor();
        Color awtColor = textColor != null ? new Color(textColor.getRgb()) : null;
        Integer shadowRgb = style.getShadowColor();
        Color shadowColor = shadowRgb != null ? new Color(shadowRgb) : null;

        return new Styling(
                false,
                awtColor,
                iStyle.weave$getBoldRaw(),
                iStyle.weave$getItalicRaw(),
                iStyle.weave$getUnderlinedRaw(),
                iStyle.weave$getStrikethroughRaw(),
                iStyle.weave$getObfuscatedRaw(),
                null,
                shadowColor,
                null
        );
    }

    public Styling color(@Nullable Color color) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling bold(@Nullable Boolean bold) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling italic(@Nullable Boolean italic) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling underline(@Nullable Boolean underline) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling strikethrough(@Nullable Boolean strikethrough) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling obfuscated(@Nullable Boolean obfuscated) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling shadow(@Nullable Boolean shadow) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling shadowColor(@Nullable Color shadowColor) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling colorWave(@Nullable ColorWave colorWave) {
        return new Styling(reset, color, bold, italic, underline, strikethrough, obfuscated, shadow, shadowColor, colorWave);
    }

    public Styling mergeWith(@Nullable Styling other) {
        if (other == null) return this;
        if (other.reset) return other;

        return new Styling(
                false,
                other.color != null ? other.color : this.color,
                other.bold != null ? other.bold : this.bold,
                other.italic != null ? other.italic : this.italic,
                other.underline != null ? other.underline : this.underline,
                other.strikethrough != null ? other.strikethrough : this.strikethrough,
                other.obfuscated != null ? other.obfuscated : this.obfuscated,
                other.shadow != null ? other.shadow : this.shadow,
                other.shadowColor != null ? other.shadowColor : this.shadowColor,
                other.colorWave != null ? other.colorWave : this.colorWave
        );
    }

    public Style toMinecraftStyle() {
        Style style = Style.EMPTY;
        if (color != null) style = style.withColor(TextColor.fromRgb(color.getRGB()));
        if (bold != null) style = style.withBold(bold);
        if (italic != null) style = style.withItalic(italic);
        if (underline != null) style = style.withUnderline(underline);
        if (strikethrough != null) style = style.withStrikethrough(strikethrough);
        if (obfuscated != null) style = style.withObfuscated(obfuscated);
        if (shadowColor != null) style = style.withShadowColor(shadowColor.getRGB());
        return style;
    }

    @Nullable
    public Color getColor() {
        return color;
    }

    public boolean isShadowSet() {
        return shadow != null;
    }

    public boolean hasShadow() {
        return shadow != null && shadow;
    }

    @Nullable
    public Color getShadowColor() {
        return shadowColor;
    }

    @Nullable
    public ColorWave getColorWave() {
        return colorWave;
    }
}