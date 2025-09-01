package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.NamedPart;
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
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.UIState;
import tytoo.weave.utils.McUtils;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ComboBox<T> extends InteractiveComponent<ComboBox<T>> {

    @NamedPart
    protected final Panel displayPanel;
    @NamedPart
    protected final SelectedLabel selectedLabel;
    @NamedPart
    protected final Component<?> arrowIcon;
    private final State<T> valueState;
    private final List<Option<T>> options = new ArrayList<>();
    private final OutlineEffect outlineEffect;
    private boolean isUpdatingFromState = false;
    @Nullable
    private String placeholder;
    @Nullable
    private Panel dropdownPanel;
    private boolean expanded = false;
    @Nullable
    private Float dropdownMaxHeightOverride = null;
    private boolean includePlaceholderOption = false;
    @Nullable
    private ScrollPanel dropdownScrollPanel;
    private float savedDropdownScrollY = 0f;

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

        SelectedLabel label = new SelectedLabel(this);
        label.setLayoutData(LinearLayout.Data.grow(1));
        label.setHeight(Constraints.relative(1.0f));
        this.selectedLabel = label;

        ArrowIcon arrow = new ArrowIcon(this);
        arrow.setWidth(Constraints.pixels(defaultHeight));
        arrow.setHeight(Constraints.relative(1.0f));
        this.arrowIcon = arrow;

        this.displayPanel.addChildren(this.selectedLabel, this.arrowIcon);
        this.addChild(this.displayPanel);

        this.outlineEffect = (OutlineEffect) Effects.outline(Color.BLACK, 1.0f);
        this.addEffect(this.outlineEffect);

        this.onClick(e -> toggleDropdown());
        this.onFocusLost(e -> {
            if (!this.expanded) return;

            Optional<Component<?>> newFocused = McUtils.getMc().map(mc -> mc.currentScreen)
                    .flatMap(UIManager::getState)
                    .map(UIState::getFocusedComponent);

            if (newFocused.isPresent() && this.dropdownPanel != null) {
                Component<?> current = newFocused.get();
                while (current != null) {
                    if (current == this.dropdownPanel) {
                        return;
                    }
                    current = current.getParent();
                }
            }

            closeDropdown();
        });

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

        this.outlineEffect.setColor(resolveBorderColor());
    }

    private Color resolveBorderColor() {
        var stylesheet = ThemeManager.getStylesheet();
        if (isFocused() || expanded) {
            return stylesheet.get(this, BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
        } else if (hasStyleState(StyleState.HOVERED)) {
            return stylesheet.get(this, StyleProps.BORDER_COLOR_HOVERED, new Color(120, 120, 120));
        } else {
            return stylesheet.get(this, BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
        }
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
        float dropdownMaxHeight = this.dropdownMaxHeightOverride != null
                ? this.dropdownMaxHeightOverride
                : stylesheet.get(this, StyleProps.DROPDOWN_MAX_HEIGHT, 100f);

        McUtils.getMc().map(mc -> mc.currentScreen).flatMap(UIManager::getState).ifPresent(uiState -> {
            Component<?> root = uiState.getRoot();
            if (root != null) {
                float absLeft = this.getLeft();
                float absBottom = this.getTop() + this.getHeight();
                this.dropdownPanel = Panel.create()
                        .setManagedByLayout(false)
                        .setFocusable(true)
                        .setWidth(Constraints.pixels(this.getWidth()))
                        .addStyleClass("combo-box-dropdown");

                this.dropdownPanel.onFocusLost(ev -> {
                    Optional<Component<?>> newFocused = McUtils.getMc().map(mc -> mc.currentScreen)
                            .flatMap(UIManager::getState)
                            .map(UIState::getFocusedComponent);
                    if (newFocused.isPresent()) {
                        Component<?> current = newFocused.get();
                        while (current != null) {
                            if (current == this.dropdownPanel) {
                                return;
                            }
                            current = current.getParent();
                        }
                    }
                    closeDropdown();
                });

                this.dropdownPanel.setX((c, parentWidth, componentWidth) -> this.getLeft() - root.getInnerLeft());
                this.dropdownPanel.setY((c, parentHeight, componentHeight) -> (this.getTop() + this.getHeight()) - root.getInnerTop());

                ScrollPanel scrollPanel = new ScrollPanel();
                this.dropdownScrollPanel = scrollPanel;
                this.dropdownPanel.addChild(scrollPanel);

                if (this.includePlaceholderOption) {
                    Button optionButton = Button.create()
                            .setWidth(Constraints.relative(1.0f))
                            .onClick(e -> {
                                this.setValue(null);
                                closeDropdown();
                            });
                    optionButton.addStyleClass("combo-box-option");
                    if (this.valueState.get() == null) {
                        optionButton.addStyleState(StyleState.SELECTED);
                    }

                    TextRenderer textRenderer = getEffectiveTextRenderer();
                    float padding = ThemeManager.getStylesheet().get(optionButton, Button.StyleProps.PADDING, 5f);
                    float rowHeight = (textRenderer.fontHeight + 1) + padding * 2f;
                    optionButton.setHeight(Constraints.pixels(rowHeight));

                    OptionLabel label = new OptionLabel(optionButton, Objects.requireNonNullElse(this.placeholder, ""));
                    label.setWidth(Constraints.relative(1.0f));
                    label.setHeight(Constraints.relative(1.0f));
                    label.setHittable(false);
                    optionButton.addChild(label);

                    scrollPanel.addChild(optionButton);
                }

                for (Option<T> option : this.options) {
                    Button optionButton = Button.create()
                            .setWidth(Constraints.relative(1.0f))
                            .onClick(e -> {
                                this.setValue(option.value());
                                closeDropdown();
                            });
                    optionButton.addStyleClass("combo-box-option");
                    if (Objects.equals(this.valueState.get(), option.value())) {
                        optionButton.addStyleState(StyleState.SELECTED);
                    }

                    TextRenderer textRenderer = getEffectiveTextRenderer();
                    float padding = ThemeManager.getStylesheet().get(optionButton, Button.StyleProps.PADDING, 5f);
                    float rowHeight = (textRenderer.fontHeight + 1) + padding * 2f;
                    optionButton.setHeight(Constraints.pixels(rowHeight));

                    OptionLabel label = new OptionLabel(optionButton, option.label());
                    label.setWidth(Constraints.relative(1.0f));
                    label.setHeight(Constraints.relative(1.0f));
                    label.setHittable(false);
                    optionButton.addChild(label);

                    scrollPanel.addChild(optionButton);
                }

                scrollPanel.getContentPanel().measure(this.getWidth(), Float.MAX_VALUE);
                float contentHeight = scrollPanel.getContentPanel().getMeasuredHeight();
                float viewHeight = Math.min(contentHeight, dropdownMaxHeight);
                this.dropdownPanel.setHeight(Constraints.pixels(viewHeight));

                float maxScroll = Math.min(0, -(contentHeight - viewHeight));
                float clampedScroll = Math.max(maxScroll, Math.min(0, this.savedDropdownScrollY));
                scrollPanel.setScrollY(clampedScroll);

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
            if (this.dropdownScrollPanel != null) {
                this.savedDropdownScrollY = this.dropdownScrollPanel.getScrollY();
            }
            Optional<Screen> screen = McUtils.getMc().map(mc -> mc.currentScreen);
            screen.flatMap(UIManager::getState).ifPresent(uiState -> {
                Component<?> root = uiState.getRoot();
                if (root != null) {
                    root.removeChild(this.dropdownPanel);
                }
            });
            this.dropdownPanel = null;
            this.dropdownScrollPanel = null;
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

    public ComboBox<T> setDropdownMaxHeight(float maxHeight) {
        this.dropdownMaxHeightOverride = maxHeight;
        return this;
    }

    public ComboBox<T> setIncludePlaceholderOption(boolean include) {
        this.includePlaceholderOption = include;
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

    @Override
    public void draw(DrawContext context) {
        super.draw(context);
    }

    public record Option<T>(String label, T value) {
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> DEFAULT_WIDTH = new StyleProperty<>("combo-box.default-width", Float.class);
        public static final StyleProperty<Float> DEFAULT_HEIGHT = new StyleProperty<>("combo-box.default-height", Float.class);
        public static final StyleProperty<Float> DROPDOWN_MAX_HEIGHT = new StyleProperty<>("combo-box.dropdown-max-height", Float.class);
        public static final StyleProperty<Color> BORDER_COLOR_HOVERED = new StyleProperty<>("combo-box.borderColor.hovered", Color.class);
    }

    private static class ArrowIcon extends Component<ArrowIcon> {
        private final ComboBox<?> comboBox;

        public ArrowIcon(ComboBox<?> comboBox) {
            this.comboBox = comboBox;
        }

        @Override
        public void draw(DrawContext context) {
            float x = getLeft();
            float y = getTop();
            float w = getWidth();
            float h = getHeight();

            Color color = this.comboBox.outlineEffect != null ? this.comboBox.outlineEffect.getColor() : this.comboBox.resolveBorderColor();

            boolean pointingUp = this.comboBox.expanded;
            Render2DUtils.drawTriangle(context, x, y, w, h, pointingUp, color);
        }
    }

    private static class SelectedLabel extends Component<SelectedLabel> {
        private final ComboBox<?> comboBox;
        private String text = "";

        public SelectedLabel(ComboBox<?> comboBox) {
            this.comboBox = comboBox;
        }

        public SelectedLabel setText(String text) {
            if (text == null) text = "";
            this.text = text;
            return this;
        }

        @Override
        public void draw(DrawContext context) {
            TextRenderer textRenderer = getEffectiveTextRenderer();

            float x = getInnerLeft();
            float innerHeight = getInnerHeight();
            float y = getInnerTop() + (innerHeight - (textRenderer.fontHeight - 1)) / 2.0f + 1f;

            int maxWidth = (int) getInnerWidth();
            if (maxWidth <= 0) return;

            String toDraw = this.text;
            int textWidth = textRenderer.getWidth(toDraw);
            if (textWidth > maxWidth) {
                String ellipsis = "...";
                int ellipsisWidth = textRenderer.getWidth(ellipsis);
                int available = Math.max(0, maxWidth - ellipsisWidth);
                String trimmed = textRenderer.trimToWidth(toDraw, available);
                toDraw = trimmed + ellipsis;
            }

            Stylesheet stylesheet = ThemeManager.getStylesheet();
            Color color = stylesheet.get(this.comboBox, TextComponent.StyleProps.TEXT_COLOR, Color.WHITE);
            int colorRgb = color != null ? color.getRGB() : -1;
            context.drawText(textRenderer, toDraw, (int) x, (int) y, colorRgb, true);
        }
    }

    private static class OptionLabel extends Component<OptionLabel> {
        private final Component<?> styleContext;
        private final String text;

        public OptionLabel(Component<?> styleContext, String text) {
            this.styleContext = styleContext;
            this.text = text == null ? "" : text;
        }

        @Override
        public void draw(DrawContext context) {
            TextRenderer textRenderer = getEffectiveTextRenderer();

            float x = getInnerLeft();
            float innerHeight = getInnerHeight();
            float y = getInnerTop() + (innerHeight - (textRenderer.fontHeight - 1)) / 2.0f + 1f;

            int maxWidth = (int) getInnerWidth();
            if (maxWidth <= 0) return;

            String toDraw = this.text;
            int textWidth = textRenderer.getWidth(toDraw);
            if (textWidth > maxWidth) {
                String ellipsis = "...";
                int ellipsisWidth = textRenderer.getWidth(ellipsis);
                int available = Math.max(0, maxWidth - ellipsisWidth);
                String trimmed = textRenderer.trimToWidth(toDraw, available);
                toDraw = trimmed + ellipsis;
            }

            Stylesheet stylesheet = ThemeManager.getStylesheet();
            Color color = stylesheet.get(this.styleContext, TextComponent.StyleProps.TEXT_COLOR, Color.WHITE);
            int colorRgb = color != null ? color.getRGB() : -1;
            context.drawText(textRenderer, toDraw, (int) x, (int) y, colorRgb, true);
        }
    }
}
