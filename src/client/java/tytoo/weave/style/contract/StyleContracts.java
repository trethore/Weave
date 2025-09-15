package tytoo.weave.style.contract;

import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.layout.GridLayout;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.EffectStyleProperties;
import tytoo.weave.style.LayoutStyleProperties;
import tytoo.weave.ui.popup.PopupStyleProperties;

public final class StyleContracts {
    private static boolean initialized;

    private StyleContracts() {
    }

    public static synchronized void bootstrap() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerComponentContract();
        registerInteractiveContract();
        registerButtonContract();
        registerImageButtonContract();
        registerSliderContract();
        registerRadioButtonContract();
        registerCheckBoxContract();
        registerComboBoxContract();
        registerBaseTextInputContract();
        registerProgressBarContract();
        registerTextComponentContract();
        registerScrollPanelContract();
        registerSeparatorContract();
        registerWindowContract();
    }

    private static void registerComponentContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builderForRoot();
        builder.slot(ComponentStyle.Slots.BASE_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.NORMAL_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.HOVERED_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.FOCUSED_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.ACTIVE_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.SELECTED_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.DISABLED_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.VALID_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.INVALID_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.BASE_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.NORMAL_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.HOVERED_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.FOCUSED_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.ACTIVE_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.SELECTED_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.DISABLED_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.VALID_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);
        builder.slot(ComponentStyle.Slots.INVALID_OVERLAY_RENDERER, SlotRequirement.OPTIONAL);

        builder.slot(LayoutStyleProperties.PADDING, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.MARGIN, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.BORDER_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.BORDER_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.BORDER_RADIUS, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.OVERLAY_BORDER_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.OVERLAY_BORDER_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.OVERLAY_BORDER_RADIUS, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.MIN_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.MAX_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.MIN_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(LayoutStyleProperties.MAX_HEIGHT, SlotRequirement.OPTIONAL);

        builder.slot(CommonStyleProperties.CURSOR, SlotRequirement.OPTIONAL);
        builder.slot(CommonStyleProperties.ACCENT_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(CommonStyleProperties.TRANSITION_DURATION, SlotRequirement.OPTIONAL);
        builder.slot(CommonStyleProperties.TRANSITION_EASING, SlotRequirement.OPTIONAL);
        builder.slot(CommonStyleProperties.SCROLL_AMOUNT, SlotRequirement.OPTIONAL);

        builder.slot(EffectStyleProperties.EFFECTS, SlotRequirement.OPTIONAL);

        builder.slot(PopupStyleProperties.BACKDROP_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(PopupStyleProperties.BACKDROP_OPACITY, SlotRequirement.OPTIONAL);
        builder.slot(PopupStyleProperties.BACKDROP_CLICK_THROUGH, SlotRequirement.OPTIONAL);
        builder.slot(PopupStyleProperties.BACKDROP_BLUR_RADIUS, SlotRequirement.OPTIONAL);

        builder.slot(LinearLayout.StyleProps.GAP, SlotRequirement.OPTIONAL);
        builder.slot(LinearLayout.StyleProps.ALIGN, SlotRequirement.OPTIONAL);
        builder.slot(LinearLayout.StyleProps.CROSS_ALIGN, SlotRequirement.OPTIONAL);
        builder.slot(LinearLayout.StyleProps.FLEX_GROW, SlotRequirement.OPTIONAL);

        builder.slot(GridLayout.StyleProps.COLUMNS, SlotRequirement.OPTIONAL);
        builder.slot(GridLayout.StyleProps.H_GAP, SlotRequirement.OPTIONAL);
        builder.slot(GridLayout.StyleProps.V_GAP, SlotRequirement.OPTIONAL);
        builder.slot(GridLayout.StyleProps.COL_SPAN, SlotRequirement.OPTIONAL);
        builder.slot(GridLayout.StyleProps.ROW_SPAN, SlotRequirement.OPTIONAL);

        StyleContractRegistry.register(builder.build());
    }

    private static void registerInteractiveContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(StyleSlot.componentType(InteractiveComponent.class));
        builder.slot(InteractiveComponent.StyleProps.ANIMATION_DURATION, SlotRequirement.OPTIONAL);
        builder.slot(InteractiveComponent.StyleProps.COLOR_NORMAL, SlotRequirement.OPTIONAL);
        builder.slot(InteractiveComponent.StyleProps.COLOR_HOVERED, SlotRequirement.OPTIONAL);
        builder.slot(InteractiveComponent.StyleProps.COLOR_FOCUSED, SlotRequirement.OPTIONAL);
        builder.slot(InteractiveComponent.StyleProps.COLOR_ACTIVE, SlotRequirement.OPTIONAL);
        builder.slot(InteractiveComponent.StyleProps.COLOR_DISABLED, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerButtonContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(Button.class);
        builder.slot(Button.StyleProps.MIN_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(Button.StyleProps.MIN_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(Button.StyleProps.PADDING, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerImageButtonContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(ImageButton.class);
        builder.slot(ImageButton.StyleProps.IMAGE_BUTTON_PADDING, SlotRequirement.OPTIONAL);
        builder.slot(ImageButton.StyleProps.IMAGE_BUTTON_GAP, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerSliderContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(StyleSlot.componentType(Slider.class));
        builder.slot(Slider.StyleProps.TRACK_PADDING, SlotRequirement.OPTIONAL);
        builder.slot(Slider.StyleProps.DEFAULT_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(Slider.StyleProps.DEFAULT_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(Slider.StyleProps.THUMB_SIZE, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerRadioButtonContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(StyleSlot.componentType(RadioButton.class));
        builder.slot(RadioButton.StyleProps.GAP, SlotRequirement.OPTIONAL);
        builder.slot(RadioButton.StyleProps.OUTLINE_SIZE, SlotRequirement.OPTIONAL);
        builder.slot(RadioButton.StyleProps.DOT_SIZE, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerCheckBoxContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(CheckBox.class);
        builder.slot(CheckBox.StyleProps.CHECK_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(CheckBox.StyleProps.BOX_SIZE, SlotRequirement.OPTIONAL);
        builder.slot(CheckBox.StyleProps.GAP, SlotRequirement.OPTIONAL);
        builder.slot(CheckBox.StyleProps.CHECK_THICKNESS, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerComboBoxContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(StyleSlot.componentType(ComboBox.class));
        builder.slot(ComboBox.StyleProps.DEFAULT_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(ComboBox.StyleProps.DEFAULT_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(ComboBox.StyleProps.DROPDOWN_MAX_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(ComboBox.StyleProps.BORDER_COLOR_HOVERED, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerBaseTextInputContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(StyleSlot.componentType(BaseTextInput.class));
        builder.slot(BaseTextInput.StyleProps.CURSOR_BLINK_INTERVAL, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.MULTI_CLICK_INTERVAL, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.SELECTION_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.BORDER_COLOR_VALID, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.BORDER_COLOR_INVALID, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.BORDER_COLOR_HOVERED, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.PLACEHOLDER_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.CURSOR_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.DEFAULT_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.DEFAULT_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.OUTLINE_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.OUTLINE_INSIDE, SlotRequirement.OPTIONAL);
        builder.slot(BaseTextInput.StyleProps.OUTLINE_SIDES, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerProgressBarContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(ProgressBar.class);
        builder.slot(ProgressBar.StyleProps.THICKNESS, SlotRequirement.OPTIONAL);
        builder.slot(ProgressBar.StyleProps.DEFAULT_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(ProgressBar.StyleProps.VALUE_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(ProgressBar.StyleProps.BACKGROUND_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(ProgressBar.StyleProps.ANIMATION_DURATION, SlotRequirement.OPTIONAL);
        builder.slot(ProgressBar.StyleProps.FILL_POLICY, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerTextComponentContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(StyleSlot.componentType(TextComponent.class));
        builder.slot(TextComponent.StyleProps.TEXT_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.BOLD, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.ITALIC, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.UNDERLINE, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.STRIKETHROUGH, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.OBFUSCATED, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.SHADOW, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.SHADOW_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.COLOR_WAVE, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.FONT, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.LETTER_SPACING, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.LINE_HEIGHT_MULTIPLIER, SlotRequirement.OPTIONAL);
        builder.slot(TextComponent.StyleProps.TEXT_SCALE, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerScrollPanelContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(ScrollPanel.class);
        builder.slot(ScrollPanel.StyleProps.WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(ScrollPanel.StyleProps.GAP, SlotRequirement.OPTIONAL);
        builder.slot(ScrollPanel.StyleProps.THUMB_MIN_HEIGHT, SlotRequirement.OPTIONAL);
        builder.slot(ScrollPanel.StyleProps.TRACK_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(ScrollPanel.StyleProps.THUMB_COLOR, SlotRequirement.OPTIONAL);
        builder.slot(ScrollPanel.StyleProps.THUMB_COLOR_HOVERED, SlotRequirement.OPTIONAL);
        builder.slot(ScrollPanel.StyleProps.THUMB_COLOR_ACTIVE, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerSeparatorContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(Separator.class);
        builder.slot(Separator.StyleProps.THICKNESS, SlotRequirement.OPTIONAL);
        builder.slot(Separator.StyleProps.LABEL_GAP, SlotRequirement.OPTIONAL);
        builder.slot(Separator.StyleProps.LABEL_TEXT_SCALE, SlotRequirement.OPTIONAL);
        builder.slot(Separator.StyleProps.COLOR, SlotRequirement.OPTIONAL);
        builder.slot(Separator.StyleProps.SMALL_LINE_RATIO, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }

    private static void registerWindowContract() {
        ComponentThemeContract.Builder builder = ComponentThemeContract.builder(Window.class);
        builder.slot(Window.StyleProps.DEFAULT_WIDTH, SlotRequirement.OPTIONAL);
        builder.slot(Window.StyleProps.DEFAULT_HEIGHT, SlotRequirement.OPTIONAL);
        StyleContractRegistry.register(builder.build());
    }
}
