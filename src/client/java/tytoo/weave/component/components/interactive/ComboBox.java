package tytoo.weave.component.components.interactive;

import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.NamedPart;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.OutlineEffect;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.utils.McUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ComboBox<T> extends InteractiveComponent<ComboBox<T>> {

    @NamedPart
    protected final Panel displayPanel;
    @NamedPart
    protected final TextComponent<?> selectedLabel;
    @NamedPart
    protected final TextComponent<?> arrowIcon;
    private final State<T> valueState;
    private final List<Option<T>> options = new ArrayList<>();
    private final OutlineEffect outlineEffect;
    private boolean isUpdatingFromState = false;
    @Nullable
    private String placeholder;
    @Nullable
    private Panel dropdownPanel;
    private boolean expanded = false;

    public ComboBox(State<T> valueState) {
        this.valueState = valueState;
        this.valueState.addListener(v -> updateSelectedLabel());

        var stylesheet = ThemeManager.getStylesheet();
        float defaultWidth = stylesheet.get(this, StyleProps.DEFAULT_WIDTH, 150f);
        float defaultHeight = stylesheet.get(this, StyleProps.DEFAULT_HEIGHT, 20f);

        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight(Constraints.pixels(defaultHeight));
        this.addStyleClass("interactive-visual");
        this.addStyleState(StyleState.NORMAL);

        this.displayPanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setPadding(2, 5)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER))
                .addStyleClass("combo-box-display")
                .addEffect(Effects.scissor());

        this.selectedLabel = SimpleTextComponent.of("")
                .setLayoutData(LinearLayout.Data.grow(1));

        this.arrowIcon = SimpleTextComponent.of("▼");

        this.displayPanel.addChildren(this.selectedLabel, this.arrowIcon);
        this.addChild(this.displayPanel);

        this.outlineEffect = (OutlineEffect) Effects.outline(Color.BLACK, 1.0f);
        this.addEffect(this.outlineEffect);

        this.onClick(e -> toggleDropdown());
        this.onFocusLost(e -> closeDropdown());

        updateSelectedLabel();
        updateVisualState(0L);
    }

    public static <T> ComboBox<T> create(State<T> valueState) {
        return new ComboBox<>(valueState);
    }

    @Override
    protected void updateVisualState(long duration) {
        super.updateVisualState(duration);
        if (this.outlineEffect == null) return;

        var stylesheet = ThemeManager.getStylesheet();
        Color outlineColor;
        if (isFocused() || expanded) {
            outlineColor = stylesheet.get(this, BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
        } else {
            outlineColor = stylesheet.get(this, BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
        }
        this.outlineEffect.setColor(outlineColor);
    }

    private void toggleDropdown() {
        if (this.expanded) {
            closeDropdown();
        } else {
            openDropdown();
        }
    }

    private void openDropdown() {
        if (this.expanded || options.isEmpty()) return;
        this.expanded = true;
        addStyleState(StyleState.ACTIVE);

        var stylesheet = ThemeManager.getStylesheet();
        float dropdownMaxHeight = stylesheet.get(this, StyleProps.DROPDOWN_MAX_HEIGHT, 100f);

        McUtils.getMc().map(mc -> mc.currentScreen).flatMap(UIManager::getState).ifPresent(uiState -> {
            Component<?> root = uiState.getRoot();
            if (root != null) {
                float absLeft = this.getLeft();
                float absBottom = this.getTop() + this.getHeight();
                this.dropdownPanel = Panel.create()
                        .setManagedByLayout(false)
                        .setX(Constraints.pixels(absLeft - root.getInnerLeft()))
                        .setY(Constraints.pixels(absBottom - root.getInnerTop()))
                        .setWidth(Constraints.pixels(this.getWidth()))
                        .addStyleClass("combo-box-dropdown");

                ScrollPanel scrollPanel = new ScrollPanel();
                this.dropdownPanel.addChild(scrollPanel);

                for (Option<T> option : this.options) {
                    Button optionButton = Button.of(option.label())
                            .setWidth(Constraints.relative(1.0f))
                            .onClick(e -> {
                                this.setValue(option.value());
                                closeDropdown();
                            });
                    optionButton.addStyleClass("combo-box-option");
                    scrollPanel.addChild(optionButton);
                }

                scrollPanel.getContentPanel().measure(this.getWidth(), Float.MAX_VALUE);
                float contentHeight = scrollPanel.getContentPanel().getMeasuredHeight();
                this.dropdownPanel.setHeight(Constraints.pixels(Math.min(contentHeight, dropdownMaxHeight)));

                root.addChild(this.dropdownPanel);
                this.dropdownPanel.bringToFront();
            }
        });
    }

    private void closeDropdown() {
        if (!this.expanded) return;
        this.expanded = false;
        removeStyleState(StyleState.ACTIVE);

        if (this.dropdownPanel != null) {
            Optional<Screen> screen = McUtils.getMc().map(mc -> mc.currentScreen);
            screen.flatMap(UIManager::getState).ifPresent(uiState -> {
                Component<?> root = uiState.getRoot();
                if (root != null) {
                    root.removeChild(this.dropdownPanel);
                }
            });
            this.dropdownPanel = null;
        }
    }

    private void updateSelectedLabel() {
        T currentValue = this.valueState.get();
        Optional<Option<T>> selectedOption = this.options.stream()
                .filter(o -> Objects.equals(o.value(), currentValue))
                .findFirst();

        String labelText = selectedOption.map(Option::label)
                .orElse(Objects.requireNonNullElse(this.placeholder, ""));
        this.selectedLabel.setText(labelText);
    }

    public ComboBox<T> addOption(String label, T value) {
        this.options.add(new Option<>(label, value));
        updateSelectedLabel();
        return this;
    }

    public ComboBox<T> setOptions(List<Option<T>> options) {
        this.options.clear();
        this.options.addAll(options);
        updateSelectedLabel();
        return this;
    }

    public ComboBox<T> clearOptions() {
        this.options.clear();
        updateSelectedLabel();
        return this;
    }

    public T getValue() {
        return this.valueState.get();
    }

    public ComboBox<T> setValue(T value) {
        this.valueState.set(value);
        return this;
    }

    public State<T> getValueState() {
        return this.valueState;
    }

    public ComboBox<T> bindValue(State<T> state) {
        this.valueState.addListener(newValue -> {
            if (isUpdatingFromState) return;
            if (!Objects.equals(state.get(), newValue)) {
                state.set(newValue);
            }
        });
        state.bind(newValue -> {
            try {
                isUpdatingFromState = true;
                setValue(newValue);
            } finally {
                isUpdatingFromState = false;
            }
        });
        return this;
    }

    @Nullable
    public String getPlaceholder() {
        return this.placeholder;
    }

    public ComboBox<T> setPlaceholder(@Nullable String placeholder) {
        this.placeholder = placeholder;
        updateSelectedLabel();
        return this;
    }

    public record Option<T>(String label, T value) {
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> DEFAULT_WIDTH = new StyleProperty<>("combo-box.default-width", Float.class);
        public static final StyleProperty<Float> DEFAULT_HEIGHT = new StyleProperty<>("combo-box.default-height", Float.class);
        public static final StyleProperty<Float> DROPDOWN_MAX_HEIGHT = new StyleProperty<>("combo-box.dropdown-max-height", Float.class);
    }
}