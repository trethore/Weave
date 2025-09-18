package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.style.*;
import tytoo.weave.style.contract.ComponentStyleProperties;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.style.effects.AntialiasingSpec;
import tytoo.weave.style.renderer.*;
import tytoo.weave.style.selector.StyleSelector;
import tytoo.weave.style.value.StyleVariable;
import tytoo.weave.style.value.Var;
import tytoo.weave.ui.CursorType;
import tytoo.weave.ui.popup.PopupStyleProperties;
import tytoo.weave.ui.tooltip.TooltipView;
import tytoo.weave.utils.McUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultTheme implements Theme {
    private static final StyleVariable<Color> PRIMARY_ACCENT = new StyleVariable<>("weave.color.primary", new Color(40, 160, 220));
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
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(20, 20, 20, 220))),
                Map.entry(ComponentStyleProperties.WindowStyles.DEFAULT_WIDTH.slot(), 400f),
                Map.entry(ComponentStyleProperties.WindowStyles.DEFAULT_HEIGHT.slot(), 300f)
        )));

        // Global defaults
        s = new StyleSelector(tytoo.weave.component.Component.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.SCROLL_AMOUNT.slot(), 10f)
        )));

        s = new StyleSelector(Separator.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new StyledColorRenderer(ComponentStyleProperties.SeparatorStyles.COLOR.slot(), new Color(128, 128, 128))),
                Map.entry(ComponentStyleProperties.SeparatorStyles.THICKNESS.slot(), 1f),
                Map.entry(ComponentStyleProperties.SeparatorStyles.LABEL_GAP.slot(), 6f),
                Map.entry(ComponentStyleProperties.SeparatorStyles.LABEL_TEXT_SCALE.slot(), 1.0f),
                Map.entry(ComponentStyleProperties.SeparatorStyles.COLOR.slot(), new Color(128, 128, 128)),
                Map.entry(ComponentStyleProperties.SeparatorStyles.SMALL_LINE_RATIO.slot(), 0.15f)
        )));

        // When a separator has a label, disable its base renderer; lines are drawn by children.
        s = new StyleSelector(Separator.class, null, Set.of("separator-with-label"), null);
        {
            Map<StyleSlot, Object> p = new HashMap<>();
            p.put(ComponentStyle.Slots.BASE_RENDERER, null);
            stylesheet.addRule(new StyleRule(s, p));
        }

        // Style for the line segments used when a separator has a label
        s = StyleSelector.part(Separator.class, "leftLine", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new ParentStyledColorRenderer(ComponentStyleProperties.SeparatorStyles.COLOR.slot(), new Color(128, 128, 128))),
                Map.entry(ComponentStyleProperties.SeparatorStyles.COLOR.slot(), new Color(128, 128, 128))
        )));
        s = StyleSelector.part(Separator.class, "rightLine", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new ParentStyledColorRenderer(ComponentStyleProperties.SeparatorStyles.COLOR.slot(), new Color(128, 128, 128))),
                Map.entry(ComponentStyleProperties.SeparatorStyles.COLOR.slot(), new Color(128, 128, 128))
        )));

        // --- TextComponent ---
        s = new StyleSelector(TextComponent.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.TextComponentStyles.TEXT_COLOR.slot(), Color.WHITE),
                Map.entry(ComponentStyleProperties.TextComponentStyles.SHADOW.slot(), true)
        )));

        s = new StyleSelector(TextComponent.class, null, null, Set.of(StyleState.DISABLED));
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.TextComponentStyles.TEXT_COLOR.slot(), new Color(120, 120, 120))
        )));

        // --- ProgressBar ---
        s = new StyleSelector(ProgressBar.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new ProgressBarRenderer()),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.THICKNESS.slot(), 8f),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.DEFAULT_WIDTH.slot(), 150f),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.VALUE_COLOR.slot(), new Color(40, 160, 220)),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.BACKGROUND_COLOR.slot(), new Color(80, 80, 80)),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.ANIMATION_DURATION.slot(), 250L),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.FILL_POLICY.slot(), ProgressBar.FillPolicy.LEFT_TO_RIGHT),
                Map.entry(EffectStyleProperties.EFFECTS.slot(), List.of(new AntialiasingSpec(24)))
        )));

        s = new StyleSelector(ProgressBar.class, null, null, Set.of(StyleState.HOVERED));
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.ProgressBarStyles.VALUE_COLOR.slot(), new Color(90, 190, 240)),
                Map.entry(ComponentStyleProperties.ProgressBarStyles.BACKGROUND_COLOR.slot(), new Color(110, 110, 110))
        )));

        // --- Interactive Components ---
        s = new StyleSelector(InteractiveComponent.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(100, 100, 100))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(120, 120, 120))),
                Map.entry(ComponentStyle.Slots.ACTIVE_RENDERER, new SolidColorRenderer(new Color(60, 60, 60))),
                Map.entry(ComponentStyle.Slots.SELECTED_RENDERER, new SolidColorRenderer(new Color(140, 140, 140))),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 150))),
                Map.entry(CommonStyleProperties.TRANSITION_DURATION.slot(), 150L),
                Map.entry(ComponentStyleProperties.InteractiveStyles.ANIMATION_DURATION.slot(), 150L)
        )));

        s = new StyleSelector(InteractiveComponent.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR.slot(), CursorType.HAND)
        )));

        // --- Button ---
        s = new StyleSelector(Button.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.ButtonStyles.MIN_WIDTH.slot(), 20f),
                Map.entry(ComponentStyleProperties.ButtonStyles.MIN_HEIGHT.slot(), 20f),
                Map.entry(ComponentStyleProperties.ButtonStyles.PADDING.slot(), 5f)
        )));

        // --- ImageButton ---
        s = new StyleSelector(ImageButton.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(100, 100, 100, 180))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(120, 120, 120, 180))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(140, 140, 140, 180))),
                Map.entry(ComponentStyle.Slots.ACTIVE_RENDERER, new SolidColorRenderer(new Color(80, 80, 80, 180))),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 120))),
                Map.entry(ComponentStyleProperties.ImageButtonStyles.PADDING.slot(), 5f),
                Map.entry(ComponentStyleProperties.ImageButtonStyles.GAP.slot(), 4f)
        )));

        // --- BaseTextInput (applies to both TextField and TextArea) ---
        s = new StyleSelector(BaseTextInput.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.ACTIVE_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new SolidColorRenderer(new Color(15, 15, 15, 150))),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.SELECTION_COLOR.slot(), new Color(50, 100, 200, 128)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.BORDER_COLOR_VALID.slot(), new Color(0, 180, 0)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.BORDER_COLOR_INVALID.slot(), new Color(180, 0, 0)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.BORDER_COLOR_FOCUSED.slot(), new Color(160, 160, 160)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.BORDER_COLOR_HOVERED.slot(), new Color(120, 120, 120)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.BORDER_COLOR_UNFOCUSED.slot(), new Color(80, 80, 80)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.PLACEHOLDER_COLOR.slot(), new Color(150, 150, 150)),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.CURSOR_COLOR.slot(), Color.LIGHT_GRAY),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.CURSOR_BLINK_INTERVAL.slot(), 500L),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.DEFAULT_WIDTH.slot(), 150f),
                Map.entry(ComponentStyleProperties.BaseTextInputStyles.DEFAULT_HEIGHT.slot(), 20f)
        )));

        s = new StyleSelector(BaseTextInput.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR.slot(), CursorType.I_BEAM)
        )));

        // --- CheckBox ---
        s = new StyleSelector(CheckBox.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.CheckBoxStyles.CHECK_COLOR.slot(), Color.WHITE),
                Map.entry(ComponentStyleProperties.CheckBoxStyles.BOX_SIZE.slot(), 12f),
                Map.entry(ComponentStyleProperties.CheckBoxStyles.GAP.slot(), 4f),
                Map.entry(ComponentStyleProperties.CheckBoxStyles.CHECK_THICKNESS.slot(), 2f)
        )));

        s = StyleSelector.part(CheckBox.class, "box", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(100, 100, 100))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(120, 120, 120))),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 150)))
        )));

        // --- Slider ---
        s = new StyleSelector(Slider.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.SliderStyles.TRACK_PADDING.slot(), 2f),
                Map.entry(ComponentStyleProperties.SliderStyles.DEFAULT_WIDTH.slot(), 150f),
                Map.entry(ComponentStyleProperties.SliderStyles.DEFAULT_HEIGHT.slot(), 20f),
                Map.entry(ComponentStyleProperties.SliderStyles.THUMB_SIZE.slot(), 8f),
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(40, 40, 40))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
                Map.entry(ComponentStyle.Slots.ACTIVE_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new SolidColorRenderer(new Color(30, 30, 30, 150)))
        )));

        s = new StyleSelector(Slider.class, null, Set.of("slider-horizontal"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR.slot(), CursorType.EW_RESIZE)
        )));

        s = new StyleSelector(Slider.class, null, Set.of("slider-vertical"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.CURSOR.slot(), CursorType.NS_RESIZE)
        )));

        s = StyleSelector.part(Slider.class, "thumb", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(160, 160, 160))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(180, 180, 180))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(200, 200, 200)))
        )));

        // --- RadioButton ---
        s = new StyleSelector(RadioButton.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.RadioButtonStyles.GAP.slot(), 5f),
                Map.entry(ComponentStyleProperties.RadioButtonStyles.OUTLINE_SIZE.slot(), 12f),
                Map.entry(ComponentStyleProperties.RadioButtonStyles.DOT_SIZE.slot(), 6f),
                Map.entry(EffectStyleProperties.EFFECTS.slot(), List.of(new AntialiasingSpec(24)))
        )));

        s = StyleSelector.part(RadioButton.class, "outline", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new RoundedRectangleRenderer(new Color(50, 50, 50), 6f)),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new RoundedRectangleRenderer(new Color(80, 80, 80), 6f))
        )));

        s = StyleSelector.part(RadioButton.class, "background", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new RoundedRectangleRenderer(new Color(110, 110, 110), 5f)),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new RoundedRectangleRenderer(new Color(140, 140, 140), 5f)),
                Map.entry(ComponentStyle.Slots.SELECTED_RENDERER, new RoundedRectangleRenderer(new Color(160, 160, 160), 5f)),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new RoundedRectangleRenderer(new Color(80, 80, 80), 5f))
        )));

        s = StyleSelector.part(RadioButton.class, "dot", Panel.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(CommonStyleProperties.ACCENT_COLOR.slot(), new Var<>(PRIMARY_ACCENT)),
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new StyledCircleRenderer(CommonStyleProperties.ACCENT_COLOR.slot(), new Color(40, 160, 220)))
        )));

        // --- ComboBox ---
        s = new StyleSelector(ComboBox.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.ComboBoxStyles.DEFAULT_WIDTH.slot(), 150f),
                Map.entry(ComponentStyleProperties.ComboBoxStyles.DEFAULT_HEIGHT.slot(), 20f),
                Map.entry(ComponentStyleProperties.ComboBoxStyles.DROPDOWN_MAX_HEIGHT.slot(), 100f)
        )));

        // The main combo box visual style, using the same background as text inputs
        s = new StyleSelector(ComboBox.class, null, Set.of("interactive-visual"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.FOCUSED_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.ACTIVE_RENDERER, new SolidColorRenderer(new Color(0, 0, 0, 200))),
                Map.entry(ComponentStyle.Slots.DISABLED_RENDERER, new SolidColorRenderer(new Color(15, 15, 15, 150))),
                Map.entry(ComponentStyleProperties.ComboBoxStyles.BORDER_COLOR_HOVERED.slot(), new Color(120, 120, 120))
        )));

        // The dropdown panel itself
        s = new StyleSelector(Panel.class, null, Set.of("combo-box-dropdown"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(30, 30, 30, 240)))
        )));

        // Buttons for each option in the dropdown
        s = new StyleSelector(Button.class, null, Set.of("combo-box-option"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(60, 60, 60))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))),
                Map.entry(ComponentStyle.Slots.SELECTED_RENDERER, new SolidColorRenderer(new Color(100, 100, 100)))
        )));

        // --- Tooltip ---
        s = new StyleSelector(TooltipView.class, null, null, null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(25, 25, 25, 235))),
                Map.entry(LayoutStyleProperties.PADDING.slot(), new EdgeInsets(6f, 8f)),
                Map.entry(LayoutStyleProperties.BORDER_RADIUS.slot(), 0f),
                Map.entry(LayoutStyleProperties.BORDER_WIDTH.slot(), 1f),
                Map.entry(LayoutStyleProperties.BORDER_COLOR.slot(), new Color(80, 80, 80, 220))
        )));

        s = new StyleSelector(TextComponent.class, null, null, null, TooltipView.class, false);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyleProperties.TextComponentStyles.TEXT_COLOR.slot(), Color.WHITE)
        )));

        // --- Popup backdrop ---
        s = new StyleSelector(Panel.class, null, Set.of("popup-backdrop"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new StyledColorRenderer(PopupStyleProperties.BACKDROP_COLOR.slot(), new Color(0, 0, 0))),
                Map.entry(PopupStyleProperties.BACKDROP_COLOR.slot(), new Color(0, 0, 0)),
                Map.entry(PopupStyleProperties.BACKDROP_OPACITY.slot(), 0.4f),
                Map.entry(PopupStyleProperties.BACKDROP_CLICK_THROUGH.slot(), false)
        )));

        // --- Context Menu ---
        s = new StyleSelector(Panel.class, null, Set.of("context-menu"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(30, 30, 30, 240))),
                Map.entry(LayoutStyleProperties.BORDER_COLOR.slot(), new Color(70, 70, 70, 240)),
                Map.entry(LayoutStyleProperties.BORDER_WIDTH.slot(), 1f)
        )));

        s = new StyleSelector(Button.class, null, Set.of("context-menu-item"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(50, 50, 50, 240))),
                Map.entry(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(70, 70, 70, 240))),
                Map.entry(ComponentStyle.Slots.ACTIVE_RENDERER, new SolidColorRenderer(new Color(60, 60, 60, 240))),
                Map.entry(ComponentStyleProperties.ButtonStyles.MIN_WIDTH.slot(), 100f),
                Map.entry(ComponentStyleProperties.ButtonStyles.MIN_HEIGHT.slot(), 16f),
                Map.entry(ComponentStyleProperties.ButtonStyles.PADDING.slot(), 4f)
        )));

        s = new StyleSelector(Panel.class, null, Set.of("context-menu-separator"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(80, 80, 80))))
        ));

        // --- Toast ---
        s = new StyleSelector(Panel.class, null, Set.of("toast"), null);
        stylesheet.addRule(new StyleRule(s, Map.ofEntries(
                Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(25, 25, 25, 235))),
                Map.entry(LayoutStyleProperties.BORDER_COLOR.slot(), new Color(80, 80, 80, 220)),
                Map.entry(LayoutStyleProperties.BORDER_WIDTH.slot(), 1f)
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
