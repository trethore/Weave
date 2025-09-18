package tytoo.weave.style.contract;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.BaseTextInput;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.CheckBox;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.component.components.interactive.ImageButton;
import tytoo.weave.component.components.interactive.InteractiveComponent;
import tytoo.weave.component.components.interactive.RadioButton;
import tytoo.weave.component.components.interactive.Slider;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.layout.GridLayout;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;

import java.awt.Color;

public final class ComponentStyleProperties {
    private ComponentStyleProperties() {
    }

    public static final class InteractiveStyles {
        public static final StyleProperty<Long> ANIMATION_DURATION;
        public static final StyleProperty<Color> COLOR_NORMAL;
        public static final StyleProperty<Color> COLOR_HOVERED;
        public static final StyleProperty<Color> COLOR_FOCUSED;
        public static final StyleProperty<Color> COLOR_ACTIVE;
        public static final StyleProperty<Color> COLOR_DISABLED;

        static {
            @SuppressWarnings("unchecked")
            ComponentStyleRegistry.Builder<InteractiveComponent<?>> builder = (ComponentStyleRegistry.Builder<InteractiveComponent<?>>) (ComponentStyleRegistry.Builder<?>) ComponentStyleRegistry.component(InteractiveComponent.class, "interactive");
            ANIMATION_DURATION = builder.optionalId("interactive.animation-duration", Long.class);
            COLOR_NORMAL = builder.optionalId("interactive.color.normal", Color.class);
            COLOR_HOVERED = builder.optionalId("interactive.color.hovered", Color.class);
            COLOR_FOCUSED = builder.optionalId("interactive.color.focused", Color.class);
            COLOR_ACTIVE = builder.optionalId("interactive.color.active", Color.class);
            COLOR_DISABLED = builder.optionalId("interactive.color.disabled", Color.class);
            builder.register();
        }

        private InteractiveStyles() {
        }
    }

    public static final class ButtonStyles {
        public static final StyleProperty<Float> MIN_WIDTH;
        public static final StyleProperty<Float> MIN_HEIGHT;
        public static final StyleProperty<Float> PADDING;

        static {
            ComponentStyleRegistry.Builder<Button> builder = ComponentStyleRegistry.component(Button.class, "button");
            MIN_WIDTH = builder.optional("min-width", Float.class);
            MIN_HEIGHT = builder.optional("min-height", Float.class);
            PADDING = builder.optional("padding", Float.class);
            builder.register();
        }

        private ButtonStyles() {
        }
    }

    public static final class ImageButtonStyles {
        public static final StyleProperty<Float> PADDING;
        public static final StyleProperty<Float> GAP;

        static {
            ComponentStyleRegistry.Builder<ImageButton> builder = ComponentStyleRegistry.component(ImageButton.class, "imageButton");
            PADDING = builder.optional("padding", Float.class);
            GAP = builder.optional("gap", Float.class);
            builder.register();
        }

        private ImageButtonStyles() {
        }
    }

    public static final class CheckBoxStyles {
        public static final StyleProperty<Color> CHECK_COLOR;
        public static final StyleProperty<Float> BOX_SIZE;
        public static final StyleProperty<Float> GAP;
        public static final StyleProperty<Float> CHECK_THICKNESS;

        static {
            ComponentStyleRegistry.Builder<CheckBox> builder = ComponentStyleRegistry.component(CheckBox.class, "checkbox");
            CHECK_COLOR = builder.optional("check.color", Color.class);
            BOX_SIZE = builder.optional("box.size", Float.class);
            GAP = builder.optional("gap", Float.class);
            CHECK_THICKNESS = builder.optional("check.thickness", Float.class);
            builder.register();
        }

        private CheckBoxStyles() {
        }
    }

    public static final class RadioButtonStyles {
        public static final StyleProperty<Float> GAP;
        public static final StyleProperty<Float> OUTLINE_SIZE;
        public static final StyleProperty<Float> DOT_SIZE;

        static {
            @SuppressWarnings("unchecked")
            ComponentStyleRegistry.Builder<RadioButton<?>> builder = (ComponentStyleRegistry.Builder<RadioButton<?>>) (ComponentStyleRegistry.Builder<?>) ComponentStyleRegistry.component(RadioButton.class, "radio");
            GAP = builder.optional("gap", Float.class);
            OUTLINE_SIZE = builder.optional("outline.size", Float.class);
            DOT_SIZE = builder.optional("dot.size", Float.class);
            builder.register();
        }

        private RadioButtonStyles() {
        }
    }

    public static final class SliderStyles {
        public static final StyleProperty<Float> TRACK_PADDING;
        public static final StyleProperty<Float> DEFAULT_WIDTH;
        public static final StyleProperty<Float> DEFAULT_HEIGHT;
        public static final StyleProperty<Float> THUMB_SIZE;

        static {
            @SuppressWarnings("unchecked")
            ComponentStyleRegistry.Builder<Slider<?>> builder = (ComponentStyleRegistry.Builder<Slider<?>>) (ComponentStyleRegistry.Builder<?>) ComponentStyleRegistry.component(Slider.class, "slider");
            TRACK_PADDING = builder.optional("track.padding", Float.class);
            DEFAULT_WIDTH = builder.optional("default-width", Float.class);
            DEFAULT_HEIGHT = builder.optional("default-height", Float.class);
            THUMB_SIZE = builder.optional("thumb.size", Float.class);
            builder.register();
        }

        private SliderStyles() {
        }
    }

    public static final class ComboBoxStyles {
        public static final StyleProperty<Float> DEFAULT_WIDTH;
        public static final StyleProperty<Float> DEFAULT_HEIGHT;
        public static final StyleProperty<Float> DROPDOWN_MAX_HEIGHT;
        public static final StyleProperty<Color> BORDER_COLOR_HOVERED;

        static {
            @SuppressWarnings("unchecked")
            ComponentStyleRegistry.Builder<ComboBox<?>> builder = (ComponentStyleRegistry.Builder<ComboBox<?>>) (ComponentStyleRegistry.Builder<?>) ComponentStyleRegistry.component(ComboBox.class, "combo-box");
            DEFAULT_WIDTH = builder.optional("default-width", Float.class);
            DEFAULT_HEIGHT = builder.optional("default-height", Float.class);
            DROPDOWN_MAX_HEIGHT = builder.optional("dropdown-max-height", Float.class);
            BORDER_COLOR_HOVERED = builder.optional("border-color.hovered", Color.class);
            builder.register();
        }

        private ComboBoxStyles() {
        }
    }

    public static final class ProgressBarStyles {
        public static final StyleProperty<Float> THICKNESS;
        public static final StyleProperty<Float> DEFAULT_WIDTH;
        public static final StyleProperty<Color> VALUE_COLOR;
        public static final StyleProperty<Color> BACKGROUND_COLOR;
        public static final StyleProperty<Long> ANIMATION_DURATION;
        public static final StyleProperty<ProgressBar.FillPolicy> FILL_POLICY;

        static {
            ComponentStyleRegistry.Builder<ProgressBar> builder = ComponentStyleRegistry.component(ProgressBar.class, "progress");
            THICKNESS = builder.optional("thickness", Float.class);
            DEFAULT_WIDTH = builder.optional("default-width", Float.class);
            VALUE_COLOR = builder.optional("value.color", Color.class);
            BACKGROUND_COLOR = builder.optional("background.color", Color.class);
            ANIMATION_DURATION = builder.optional("animation-duration", Long.class);
            FILL_POLICY = builder.optional("fill-policy", ProgressBar.FillPolicy.class);
            builder.register();
        }

        private ProgressBarStyles() {
        }
    }

    public static final class TextComponentStyles {
        public static final StyleProperty<Color> TEXT_COLOR;
        public static final StyleProperty<Boolean> BOLD;
        public static final StyleProperty<Boolean> ITALIC;
        public static final StyleProperty<Boolean> UNDERLINE;
        public static final StyleProperty<Boolean> STRIKETHROUGH;
        public static final StyleProperty<Boolean> OBFUSCATED;
        public static final StyleProperty<Boolean> SHADOW;
        public static final StyleProperty<Color> SHADOW_COLOR;
        public static final StyleProperty<ColorWave> COLOR_WAVE;
        public static final StyleProperty<TextRenderer> FONT;
        public static final StyleProperty<Float> LETTER_SPACING;
        public static final StyleProperty<Float> LINE_HEIGHT_MULTIPLIER;
        public static final StyleProperty<Float> TEXT_SCALE;

        static {
            @SuppressWarnings("unchecked")
            ComponentStyleRegistry.Builder<TextComponent<?>> builder = (ComponentStyleRegistry.Builder<TextComponent<?>>) (ComponentStyleRegistry.Builder<?>) ComponentStyleRegistry.component(TextComponent.class, "text");
            TEXT_COLOR = builder.optionalId("text.color", Color.class);
            BOLD = builder.optionalId("text.bold", Boolean.class);
            ITALIC = builder.optionalId("text.italic", Boolean.class);
            UNDERLINE = builder.optionalId("text.underline", Boolean.class);
            STRIKETHROUGH = builder.optionalId("text.strikethrough", Boolean.class);
            OBFUSCATED = builder.optionalId("text.obfuscated", Boolean.class);
            SHADOW = builder.optionalId("text.shadow", Boolean.class);
            SHADOW_COLOR = builder.optionalId("text.shadow-color", Color.class);
            COLOR_WAVE = builder.optionalId("text.color-wave", ColorWave.class);
            FONT = builder.optionalId("text.font", TextRenderer.class);
            LETTER_SPACING = builder.optionalId("text.letter-spacing", Float.class);
            LINE_HEIGHT_MULTIPLIER = builder.optionalId("text.line-height-multiplier", Float.class);
            TEXT_SCALE = builder.optionalId("text.text-scale", Float.class);
            builder.register();
        }

        private TextComponentStyles() {
        }
    }

    public static final class BaseTextInputStyles {
        public static final StyleProperty<Long> CURSOR_BLINK_INTERVAL;
        public static final StyleProperty<Long> MULTI_CLICK_INTERVAL;
        public static final StyleProperty<Color> SELECTION_COLOR;
        public static final StyleProperty<Color> BORDER_COLOR_VALID;
        public static final StyleProperty<Color> BORDER_COLOR_INVALID;
        public static final StyleProperty<Color> BORDER_COLOR_FOCUSED;
        public static final StyleProperty<Color> BORDER_COLOR_HOVERED;
        public static final StyleProperty<Color> BORDER_COLOR_UNFOCUSED;
        public static final StyleProperty<Color> PLACEHOLDER_COLOR;
        public static final StyleProperty<Color> CURSOR_COLOR;
        public static final StyleProperty<Float> DEFAULT_WIDTH;
        public static final StyleProperty<Float> DEFAULT_HEIGHT;
        public static final StyleProperty<Float> OUTLINE_WIDTH;
        public static final StyleProperty<Boolean> OUTLINE_INSIDE;
        public static final StyleProperty<OutlineSides> OUTLINE_SIDES;

        static {
            @SuppressWarnings("unchecked")
            ComponentStyleRegistry.Builder<BaseTextInput<?>> builder = (ComponentStyleRegistry.Builder<BaseTextInput<?>>) (ComponentStyleRegistry.Builder<?>) ComponentStyleRegistry.component(BaseTextInput.class, "text-input");
            CURSOR_BLINK_INTERVAL = builder.optionalId("cursor.blink-interval", Long.class);
            MULTI_CLICK_INTERVAL = builder.optionalId("click.multi-interval", Long.class);
            SELECTION_COLOR = builder.optionalId("selectionColor", Color.class);
            BORDER_COLOR_VALID = builder.optionalId("borderColor.valid", Color.class);
            BORDER_COLOR_INVALID = builder.optionalId("borderColor.invalid", Color.class);
            BORDER_COLOR_FOCUSED = builder.optionalId("borderColor.focused", Color.class);
            BORDER_COLOR_HOVERED = builder.optionalId("borderColor.hovered", Color.class);
            BORDER_COLOR_UNFOCUSED = builder.optionalId("borderColor.unfocused", Color.class);
            PLACEHOLDER_COLOR = builder.optionalId("placeholderColor", Color.class);
            CURSOR_COLOR = builder.optionalId("cursorColor", Color.class);
            DEFAULT_WIDTH = builder.optional("default-width", Float.class);
            DEFAULT_HEIGHT = builder.optional("default-height", Float.class);
            OUTLINE_WIDTH = builder.optionalId("outline.width", Float.class);
            OUTLINE_INSIDE = builder.optionalId("outline.inside", Boolean.class);
            OUTLINE_SIDES = builder.optionalId("outline.sides", OutlineSides.class);
            builder.register();
        }

        private BaseTextInputStyles() {
        }
    }

    public static final class ScrollPanelStyles {
        public static final StyleProperty<Float> WIDTH;
        public static final StyleProperty<Float> GAP;
        public static final StyleProperty<Float> THUMB_MIN_HEIGHT;
        public static final StyleProperty<Color> TRACK_COLOR;
        public static final StyleProperty<Color> THUMB_COLOR;
        public static final StyleProperty<Color> THUMB_COLOR_HOVERED;
        public static final StyleProperty<Color> THUMB_COLOR_ACTIVE;

        static {
            ComponentStyleRegistry.Builder<ScrollPanel> builder = ComponentStyleRegistry.component(ScrollPanel.class, "scroll-panel.scrollbar");
            WIDTH = builder.optional("width", Float.class);
            GAP = builder.optional("gap", Float.class);
            THUMB_MIN_HEIGHT = builder.optional("thumbMinHeight", Float.class);
            TRACK_COLOR = builder.optional("trackColor", Color.class);
            THUMB_COLOR = builder.optional("thumbColor", Color.class);
            THUMB_COLOR_HOVERED = builder.optional("thumbColor.hovered", Color.class);
            THUMB_COLOR_ACTIVE = builder.optional("thumbColor.active", Color.class);
            builder.register();
        }

        private ScrollPanelStyles() {
        }
    }

    public static final class SeparatorStyles {
        public static final StyleProperty<Float> THICKNESS;
        public static final StyleProperty<Float> LABEL_GAP;
        public static final StyleProperty<Float> LABEL_TEXT_SCALE;
        public static final StyleProperty<Color> COLOR;
        public static final StyleProperty<Float> SMALL_LINE_RATIO;

        static {
            ComponentStyleRegistry.Builder<Separator> builder = ComponentStyleRegistry.component(Separator.class, "separator");
            THICKNESS = builder.optional("thickness", Float.class);
            LABEL_GAP = builder.optional("label.gap", Float.class);
            LABEL_TEXT_SCALE = builder.optional("label.text-scale", Float.class);
            COLOR = builder.optional("color", Color.class);
            SMALL_LINE_RATIO = builder.optional("small-line-ratio", Float.class);
            builder.register();
        }

        private SeparatorStyles() {
        }
    }

    public static final class WindowStyles {
        public static final StyleProperty<Float> DEFAULT_WIDTH;
        public static final StyleProperty<Float> DEFAULT_HEIGHT;

        static {
            ComponentStyleRegistry.Builder<Window> builder = ComponentStyleRegistry.component(Window.class, "window");
            DEFAULT_WIDTH = builder.optional("default-width", Float.class);
            DEFAULT_HEIGHT = builder.optional("default-height", Float.class);
            builder.register();
        }

        private WindowStyles() {
        }
    }

    public static final class LinearLayoutStyles {
        public static final StyleProperty<Float> GAP;
        public static final StyleProperty<LinearLayout.Alignment> ALIGN;
        public static final StyleProperty<LinearLayout.CrossAxisAlignment> CROSS_ALIGN;
        public static final StyleProperty<Float> FLEX_GROW;

        static {
            ComponentStyleRegistry.Builder<Component<?>> builder = ComponentStyleRegistry.root("linear");
            GAP = builder.optional("gap", Float.class);
            ALIGN = builder.optional("align", LinearLayout.Alignment.class);
            CROSS_ALIGN = builder.optional("cross-align", LinearLayout.CrossAxisAlignment.class);
            FLEX_GROW = builder.optional("grow", Float.class);
            builder.register();
        }

        private LinearLayoutStyles() {
        }
    }

    public static final class GridLayoutStyles {
        public static final StyleProperty<Integer> COLUMNS;
        public static final StyleProperty<Float> H_GAP;
        public static final StyleProperty<Float> V_GAP;
        public static final StyleProperty<Integer> COL_SPAN;
        public static final StyleProperty<Integer> ROW_SPAN;

        static {
            ComponentStyleRegistry.Builder<Component<?>> builder = ComponentStyleRegistry.root("grid");
            COLUMNS = builder.optional("columns", Integer.class);
            H_GAP = builder.optional("h-gap", Float.class);
            V_GAP = builder.optional("v-gap", Float.class);
            COL_SPAN = builder.optional("col-span", Integer.class);
            ROW_SPAN = builder.optional("row-span", Integer.class);
            builder.register();
        }

        private GridLayoutStyles() {
        }
    }
}
