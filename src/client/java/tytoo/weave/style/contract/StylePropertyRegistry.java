package tytoo.weave.style.contract;

import tytoo.weave.style.ComponentStyle;

public final class StylePropertyRegistry {
    private static boolean initialized;

    private StylePropertyRegistry() {
    }

    public static synchronized void bootstrap() {
        if (initialized) {
            return;
        }
        initialized = true;
        registerComponentBaseContract();
        initializePropertyDefinitions();
    }

    private static void registerComponentBaseContract() {
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
        StyleContractRegistry.register(builder.build());
    }

    private static void initializePropertyDefinitions() {
        load(tytoo.weave.style.CommonStyleProperties.class);
        load(tytoo.weave.style.LayoutStyleProperties.class);
        load(tytoo.weave.style.EffectStyleProperties.class);
        load(tytoo.weave.ui.popup.PopupStyleProperties.class);
        load(ComponentStyleProperties.InteractiveStyles.class);
        load(ComponentStyleProperties.ButtonStyles.class);
        load(ComponentStyleProperties.ImageButtonStyles.class);
        load(ComponentStyleProperties.CheckBoxStyles.class);
        load(ComponentStyleProperties.RadioButtonStyles.class);
        load(ComponentStyleProperties.SliderStyles.class);
        load(ComponentStyleProperties.ComboBoxStyles.class);
        load(ComponentStyleProperties.ProgressBarStyles.class);
        load(ComponentStyleProperties.TextComponentStyles.class);
        load(ComponentStyleProperties.BaseTextInputStyles.class);
        load(ComponentStyleProperties.ScrollPanelStyles.class);
        load(ComponentStyleProperties.SeparatorStyles.class);
        load(ComponentStyleProperties.WindowStyles.class);
        load(ComponentStyleProperties.LinearLayoutStyles.class);
        load(ComponentStyleProperties.GridLayoutStyles.class);
    }

    private static void load(Class<?> type) {
        try {
            Class.forName(type.getName(), true, type.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to initialize style properties for " + type.getName(), e);
        }
    }
}
