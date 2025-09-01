package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.InteractiveComponent.StyleProps;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.style.*;
import tytoo.weave.style.renderer.ParentStyledColorRenderer;
import tytoo.weave.style.renderer.RoundedRectangleRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;
import tytoo.weave.style.renderer.StyledColorRenderer;
import tytoo.weave.style.selector.StyleSelector;
import tytoo.weave.ui.CursorType;
import tytoo.weave.utils.McUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultTheme implements Theme {
    private final Stylesheet stylesheet;

    public DefaultTheme() {
        this.stylesheet = new Stylesheet();
        populateDefaultStyles();
    }

    private void populateDefaultStyles() {
        stylesheet.clearRules();

        // Helper for creating selectors
        StyleSelector s;

        // General
        s = new StyleSelector(Window.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.BASE_RENDERER, new SolidColorRenderer(new Color(20, 20, 20, 220))),
                Map.entry(Window.StyleProps.DEFAULT_WIDTH, 400f),
                Map.entry(Window.StyleProps.DEFAULT_HEIGHT, 300f)
        )));

        s = new StyleSelector(Separator.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.BASE_RENDERER, new StyledColorRenderer(Separator.StyleProps.COLOR, new Color(128, 128, 128))),
                Map.entry(Separator.StyleProps.THICKNESS, 1f),
                Map.entry(Separator.StyleProps.LABEL_GAP, 6f),
                Map.entry(Separator.StyleProps.LABEL_TEXT_SCALE, 1.0f),
                Map.entry(Separator.StyleProps.COLOR, new Color(128, 128, 128)),
                Map.entry(Separator.StyleProps.SMALL_LINE_RATIO, 0.15f)
        )));

        // When a separator has a label, disable its base renderer; lines are drawn by children.
        s = new StyleSelector(Separator.class, null, Set.of("separator-with-label"), null);
        {
            Map<StyleProperty<?>, Object> p = new HashMap<>();
            p.put(ComponentStyle.StyleProps.BASE_RENDERER, null);
            stylesheet.addRule(new StyleRule(s, p));
        }

        // Style for the line segments used when a separator has a label
        s = new StyleSelector(Panel.class, null, Set.of("separator-line"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.BASE_RENDERER, new ParentStyledColorRenderer(Separator.StyleProps.COLOR, new Color(128, 128, 128))),
                Map.entry(Separator.StyleProps.COLOR, new Color(128, 128, 128))
        )));

        // --- TextComponent ---
        s = new StyleSelector(TextComponent.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(TextComponent.StyleProps.TEXT_COLOR, Color.WHITE),
                Map.entry(TextComponent.StyleProps.SHADOW, true)
        )));

        s = new StyleSelector(TextComponent.class, null, Set.of("test-gui-title"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(TextComponent.StyleProps.COLOR_WAVE, new ColorWave(ColorWave.createRainbow(36), 2f))
        )));

        // --- Interactive Components ---
        s = new StyleSelector(InteractiveComponent.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(100, 100, 100))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(120, 120, 120))),
                Map.entry(ComponentStyle.StyleProps.ACTIVE_RENDERER, new SolidColorRenderer(new Color(60, 60, 60))),
                Map.entry(ComponentStyle.StyleProps.SELECTED_RENDERER, new SolidColorRenderer(new Color(140, 140, 140))),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 150))),
                Map.entry(StyleProps.ANIMATION_DURATION, 150L)
        )));

        s = new StyleSelector(InteractiveComponent.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR, CursorType.HAND)
        )));

        // --- Button ---
        s = new StyleSelector(Button.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(Button.StyleProps.MIN_WIDTH, 20f),
                Map.entry(Button.StyleProps.MIN_HEIGHT, 20f),
                Map.entry(Button.StyleProps.PADDING, 5f)
        )));

        // --- ImageButton ---
        s = new StyleSelector(ImageButton.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(100, 100, 100, 180))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(120, 120, 120, 180))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(140, 140, 140, 180))),
                Map.entry(ComponentStyle.StyleProps.ACTIVE_RENDERER, new SolidColorRenderer(new Color(80, 80, 80, 180))),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 120))),
                Map.entry(ImageButton.StyleProps.IMAGE_BUTTON_PADDING, 5f),
                Map.entry(ImageButton.StyleProps.IMAGE_BUTTON_GAP, 4f)
        )));

        // --- BaseTextInput (applies to both TextField and TextArea) ---
        s = new StyleSelector(BaseTextInput.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.ACTIVE_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new SolidColorRenderer(new Color(15, 15, 15, 150))),
                Map.entry(BaseTextInput.StyleProps.SELECTION_COLOR, new Color(50, 100, 200, 128)),
                Map.entry(BaseTextInput.StyleProps.BORDER_COLOR_VALID, new Color(0, 180, 0)),
                Map.entry(BaseTextInput.StyleProps.BORDER_COLOR_INVALID, new Color(180, 0, 0)),
                Map.entry(BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160)),
                Map.entry(BaseTextInput.StyleProps.BORDER_COLOR_HOVERED, new Color(120, 120, 120)),
                Map.entry(BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80)),
                Map.entry(BaseTextInput.StyleProps.PLACEHOLDER_COLOR, new Color(150, 150, 150)),
                Map.entry(BaseTextInput.StyleProps.CURSOR_COLOR, Color.LIGHT_GRAY),
                Map.entry(BaseTextInput.StyleProps.CURSOR_BLINK_INTERVAL, 500L),
                Map.entry(BaseTextInput.StyleProps.DEFAULT_WIDTH, 150f),
                Map.entry(BaseTextInput.StyleProps.DEFAULT_HEIGHT, 20f)
        )));

        s = new StyleSelector(BaseTextInput.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR, CursorType.I_BEAM)
        )));

        // --- CheckBox ---
        s = new StyleSelector(CheckBox.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CheckBox.StyleProps.CHECK_COLOR, Color.WHITE),
                Map.entry(CheckBox.StyleProps.BOX_SIZE, 12f),
                Map.entry(CheckBox.StyleProps.GAP, 4f),
                Map.entry(CheckBox.StyleProps.CHECK_THICKNESS, 2f)
        )));

        s = new StyleSelector(Panel.class, null, Set.of("checkbox-box"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(100, 100, 100))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(120, 120, 120))),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 150)))
        )));

        // --- Slider ---
        s = new StyleSelector(Slider.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(Slider.StyleProps.TRACK_PADDING, 2f),
                Map.entry(Slider.StyleProps.DEFAULT_WIDTH, 150f),
                Map.entry(Slider.StyleProps.DEFAULT_HEIGHT, 20f),
                Map.entry(Slider.StyleProps.THUMB_SIZE, 8f),
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(40, 40, 40))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
                Map.entry(ComponentStyle.StyleProps.ACTIVE_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new SolidColorRenderer(new Color(30, 30, 30, 150)))
        )));

        s = new StyleSelector(Slider.class, null, Set.of("slider-horizontal"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR, CursorType.EW_RESIZE)
        )));

        s = new StyleSelector(Slider.class, null, Set.of("slider-vertical"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR, CursorType.NS_RESIZE)
        )));

        s = new StyleSelector(Panel.class, null, Set.of("slider-thumb"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(160, 160, 160))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(180, 180, 180))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(200, 200, 200)))
        )));

        // --- RadioButton ---
        s = new StyleSelector(RadioButton.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(RadioButton.StyleProps.GAP, 5f),
                Map.entry(RadioButton.StyleProps.OUTLINE_SIZE, 12f),
                Map.entry(RadioButton.StyleProps.DOT_SIZE, 6f)
        )));

        s = new StyleSelector(Panel.class, null, Set.of("radio-button-outline"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new RoundedRectangleRenderer(new Color(50, 50, 50), 6f)),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new RoundedRectangleRenderer(new Color(80, 80, 80), 6f))
        )));

        s = new StyleSelector(Panel.class, null, Set.of("radio-button-background"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new RoundedRectangleRenderer(new Color(110, 110, 110), 5f)),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new RoundedRectangleRenderer(new Color(140, 140, 140), 5f)),
                Map.entry(ComponentStyle.StyleProps.SELECTED_RENDERER, new RoundedRectangleRenderer(new Color(160, 160, 160), 5f)),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new RoundedRectangleRenderer(new Color(80, 80, 80), 5f))
        )));

        s = new StyleSelector(Panel.class, null, Set.of("radio-button-dot"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.BASE_RENDERER, new RoundedRectangleRenderer(new Color(40, 160, 220), 3f))
        )));

        // --- ComboBox ---
        s = new StyleSelector(ComboBox.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComboBox.StyleProps.DEFAULT_WIDTH, 150f),
                Map.entry(ComboBox.StyleProps.DEFAULT_HEIGHT, 20f),
                Map.entry(ComboBox.StyleProps.DROPDOWN_MAX_HEIGHT, 100f)
        )));

        // The main combo box visual style, using the same background as text inputs
        s = new StyleSelector(ComboBox.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.FOCUSED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.ACTIVE_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.StyleProps.DISABLED_RENDERER, new SolidColorRenderer(new Color(15, 15, 15, 150))),
                Map.entry(ComboBox.StyleProps.BORDER_COLOR_HOVERED, new Color(120, 120, 120))
        )));

        // The dropdown panel itself
        s = new StyleSelector(Panel.class, null, Set.of("combo-box-dropdown"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.BASE_RENDERER, new SolidColorRenderer(new Color(30, 30, 30, 240)))
        )));

        // Buttons for each option in the dropdown
        s = new StyleSelector(Button.class, null, Set.of("combo-box-option"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(60, 60, 60))),
                Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))),
                Map.entry(ComponentStyle.StyleProps.SELECTED_RENDERER, new SolidColorRenderer(new Color(90, 90, 90))),
                Map.entry(Button.StyleProps.PADDING, 3f)
        )));
    }

    @Override
    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return McUtils.getMc().map(mc -> mc.textRenderer).orElse(null);
    }
}
