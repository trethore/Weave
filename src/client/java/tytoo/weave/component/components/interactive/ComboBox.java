package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.component.Component;
import tytoo.weave.component.NamedPart;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.OutlineEffect;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.UIState;
import tytoo.weave.ui.popup.Anchor;
import tytoo.weave.ui.popup.PopupOptions;
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
    protected final Component<?> arrowIcon;
    @NamedPart
    private final SelectedLabel selectedLabel;
    private final State<T> valueState;
    private final List<Option<T>> options = new ArrayList<>();
    private final OutlineEffect outlineEffect;
    private boolean isUpdatingFromState = false;
    @Nullable
    private String placeholder;
    @Nullable
    private Panel dropdownContent;
    @Nullable
    private UIManager.PopupHandle popupHandle;
    private boolean expanded = false;
    @Nullable
    private Float dropdownMaxHeightOverride = null;
    private boolean includePlaceholderOption = false;
    @Nullable
    private ScrollPanel dropdownScrollPanel;
    private float savedDropdownScrollY = 0f;
    private int dropdownHoverIndex = -1;

    public ComboBox(State<T> valueState) {
        this.valueState = valueState;
        this.valueState.addListener(v -> updateSelectedLabel());

        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float defaultWidth = stylesheet.get(this, StyleProps.DEFAULT_WIDTH, 150f);
        float defaultHeight = stylesheet.get(this, StyleProps.DEFAULT_HEIGHT, 20f);

        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight(Constraints.pixels(defaultHeight));
        this.addStyleClass("interactive-visual");

        this.displayPanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setPadding(2, 5)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER))
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
        this.outlineEffect.setColor(resolveBorderColor());
        this.addStyleState(StyleState.NORMAL);

        this.onClick(e -> toggleDropdown());

        updateSelectedLabel();
        updateVisualState(0L);
    }

    public static <T> ComboBox<T> create(State<T> valueState) {
        return new ComboBox<>(valueState);
    }

    @Override
    protected void updateVisualState(long duration) {
        super.updateVisualState(duration);
    }

    private Color resolveBorderColor() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        if (isFocused() || expanded) {
            return stylesheet.get(this, BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
        } else if (hasStyleState(StyleState.HOVERED)) {
            return stylesheet.get(this, StyleProps.BORDER_COLOR_HOVERED, new Color(120, 120, 120));
        } else {
            return stylesheet.get(this, BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
        }
    }

    public void applyOutlineColor(Color color) {
        if (this.outlineEffect != null) this.outlineEffect.setColor(color);
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
        addStyleState(StyleState.HOVERED);

        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float dropdownMaxHeight = this.dropdownMaxHeightOverride != null
                ? this.dropdownMaxHeightOverride
                : stylesheet.get(this, StyleProps.DROPDOWN_MAX_HEIGHT, 100f);

        // Build popup content (sized to combo width)
        this.dropdownContent = Panel.create()
                .setWidth(Constraints.pixels(this.getWidth()))
                .addStyleClass("combo-box-dropdown");

        Panel bg = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .addStyleClass("combo-box-dropdown-bg");
        this.dropdownContent.addChild(bg);

        ScrollPanel scrollPanel = new ScrollPanel();
        this.dropdownScrollPanel = scrollPanel;
        this.dropdownContent.addChild(scrollPanel);

        if (this.includePlaceholderOption) {
            Button optionButton = Button.create()
                    .setWidth(Constraints.relative(1.0f))
                    .onClick(e -> {
                        this.setValue(null);
                        closeDropdown(true);
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
                        closeDropdown(true);
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
        this.dropdownContent.setHeight(Constraints.pixels(viewHeight));

        int initialIndex = computeSelectedIndexForDropdown();
        float initialScroll = computeInitialScrollForIndex(scrollPanel, initialIndex, viewHeight);
        scrollPanel.setScrollY(initialScroll);

        // Open popup anchored to this combobox
        Anchor anchor = new Anchor(this, Anchor.Side.BOTTOM, Anchor.Align.START, 0f, 0f, 0f);
        PopupOptions opts = new PopupOptions()
                .setGap(0f)
                .setTrapFocus(true)
                .setCloseOnFocusLoss(true)
                .setCloseOnEsc(true);
        this.popupHandle = UIManager.openPopup(this.dropdownContent, anchor, opts);

        // Focus dropdown content to receive keyboard navigation
        McUtils.getMc().map(mc -> mc.currentScreen).ifPresent(screen -> UIManager.requestFocus(screen, this.dropdownContent));

        setInitialDropdownHoverAndFocus(initialIndex);
        attachDropdownKeyNavigation();

        this.dropdownContent.onMouseEnter(e -> this.addStyleState(StyleState.HOVERED));
        this.dropdownContent.onMouseLeave(e -> {
            if (!this.expanded) this.removeStyleState(StyleState.HOVERED);
        });
    }

    private void closeDropdown() {
        closeDropdown(false);
    }

    private void closeDropdown(boolean dueToSelection) {
        if (!this.expanded) return;
        this.expanded = false;
        removeStyleState(StyleState.ACTIVE);

        if (this.dropdownContent != null) {
            if (this.dropdownScrollPanel != null) {
                this.savedDropdownScrollY = this.dropdownScrollPanel.getScrollY();
            }
            UIManager.closePopup(this.popupHandle);
            this.popupHandle = null;
            this.dropdownContent = null;
            this.dropdownScrollPanel = null;
            this.dropdownHoverIndex = -1;
        }

        Optional<UIState> stateOpt = McUtils.getMc().map(mc -> mc.currentScreen).flatMap(UIManager::getState);
        if (stateOpt.isPresent()) {
            if (dueToSelection) {
                McUtils.getMc().map(mc -> mc.currentScreen).ifPresent(UIManager::clearFocus);
            } else {
                Component<?> hovered = stateOpt.get().getHoveredComponent();
                boolean mouseOverCombo = false;
                if (hovered != null) {
                    for (Component<?> cur = hovered; cur != null; cur = cur.getParent()) {
                        if (cur == this) {
                            mouseOverCombo = true;
                            break;
                        }
                    }
                }
                if (!mouseOverCombo) {
                    removeStyleState(StyleState.HOVERED);
                }
            }
        }
    }

    private List<Button> getDropdownOptionButtons() {
        if (this.dropdownScrollPanel == null) return List.of();
        List<Button> buttons = new ArrayList<>();
        for (Component<?> child : this.dropdownScrollPanel.getContentPanel().getChildren()) {
            if (child instanceof Button b) {
                buttons.add(b);
            }
        }
        return buttons;
    }

    private int computeSelectedIndexForDropdown() {
        List<Button> buttons = getDropdownOptionButtons();
        if (buttons.isEmpty()) return -1;

        if (this.includePlaceholderOption && this.valueState.get() == null) {
            return 0;
        }

        int offset = this.includePlaceholderOption ? 1 : 0;
        T currentValue = this.valueState.get();
        int idx = -1;
        for (int i = 0; i < this.options.size(); i++) {
            if (Objects.equals(this.options.get(i).value(), currentValue)) {
                idx = offset + i;
                break;
            }
        }
        if (idx < 0 && !buttons.isEmpty()) idx = 0;
        return idx;
    }

    private float computeInitialScrollForIndex(ScrollPanel scrollPanel, int index, float viewHeight) {
        float contentHeight = scrollPanel.getContentPanel().getMeasuredHeight();
        float maxScroll = Math.min(0, -(contentHeight - viewHeight));
        if (index < 0) {
            return Math.max(maxScroll, Math.min(0, this.savedDropdownScrollY));
        }

        List<Button> buttons = getDropdownOptionButtons();
        if (buttons.isEmpty()) return 0f;

        float gap = 0f;
        if (scrollPanel.getContentPanel().getLayout() instanceof LinearLayout ll) {
            gap = ll.getGap();
        }

        TextRenderer tr = getEffectiveTextRenderer();
        float top = 0f;
        for (int i = 0; i < index; i++) {
            Button b = buttons.get(i);
            float padding = ThemeManager.getStylesheet().get(b, Button.StyleProps.PADDING, 5f);
            float rowH = (tr.fontHeight + 1) + padding * 2f;
            top += rowH;
            top += gap;
        }

        float desired = -top;
        if (desired > 0) desired = 0;
        if (desired < maxScroll) desired = maxScroll;
        return desired;
    }

    private void setInitialDropdownHoverAndFocus(int initialIndex) {
        Optional<Screen> screenOpt = McUtils.getMc().map(mc -> mc.currentScreen);
        if (screenOpt.isPresent() && this.dropdownContent != null) {
            UIManager.requestFocus(screenOpt.get(), this.dropdownContent);
        }

        List<Button> buttons = getDropdownOptionButtons();
        if (buttons.isEmpty()) {
            this.dropdownHoverIndex = -1;
            return;
        }
        int selectedIndex = Math.max(0, initialIndex);
        updateDropdownHoverIndex(selectedIndex, false);
    }

    private void attachDropdownKeyNavigation() {
        if (this.dropdownContent == null) return;
        java.util.function.Consumer<KeyPressEvent> handler = e -> {
            int key = e.getKeyCode();
            if (key == GLFW.GLFW_KEY_UP) {
                moveDropdownHover(-1);
                e.cancel();
                return;
            }
            if (key == GLFW.GLFW_KEY_DOWN) {
                moveDropdownHover(1);
                e.cancel();
                return;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER || key == GLFW.GLFW_KEY_SPACE) {
                activateHoveredOption();
                e.cancel();
                return;
            }
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                closeDropdown(false);
                McUtils.getMc().map(mc -> mc.currentScreen).ifPresent(screen -> UIManager.requestFocus(screen, this));
                e.cancel();
            }
        };
        this.dropdownContent.onEvent(KeyPressEvent.TYPE, handler);
        if (this.dropdownContent.getParent() != null) {
            this.dropdownContent.getParent().onEvent(KeyPressEvent.TYPE, handler);
        }
    }

    private void moveDropdownHover(int delta) {
        List<Button> buttons = getDropdownOptionButtons();
        if (buttons.isEmpty()) return;
        int count = buttons.size();
        int current = Math.max(0, this.dropdownHoverIndex);
        int next = (current + delta) % count;
        if (next < 0) next += count;
        updateDropdownHoverIndex(next, true);
    }

    private void updateDropdownHoverIndex(int newIndex, boolean ensureVisible) {
        List<Button> buttons = getDropdownOptionButtons();
        if (buttons.isEmpty()) {
            this.dropdownHoverIndex = -1;
            return;
        }
        if (newIndex < 0) newIndex = 0;
        if (newIndex >= buttons.size()) newIndex = buttons.size() - 1;

        for (int i = 0; i < buttons.size(); i++) {
            Button b = buttons.get(i);
            if (i == newIndex) {
                b.addStyleState(StyleState.HOVERED);
            } else {
                b.removeStyleState(StyleState.HOVERED);
            }
        }
        this.dropdownHoverIndex = newIndex;

        if (ensureVisible && this.dropdownScrollPanel != null) {
            Button target = buttons.get(newIndex);
            ensureOptionVisible(this.dropdownScrollPanel, target);
        }
    }

    private void ensureOptionVisible(ScrollPanel scroll, Component<?> option) {
        float viewTop = scroll.getInnerTop();
        float viewBottom = viewTop + scroll.getInnerHeight();
        float optTop = option.getTop();
        float optBottom = option.getTop() + option.getHeight();

        float currentScroll = scroll.getScrollY();
        float contentHeight = scroll.getContentPanel().getFinalHeight();
        float viewHeight = scroll.getInnerHeight();
        float maxScroll = Math.min(0, -(contentHeight - viewHeight));

        if (optTop < viewTop) {
            float delta = viewTop - optTop;
            float newScroll = currentScroll + delta;
            if (newScroll > 0) newScroll = 0;
            if (newScroll < maxScroll) newScroll = maxScroll;
            scroll.setScrollY(newScroll);
        } else if (optBottom > viewBottom) {
            float delta = optBottom - viewBottom;
            float newScroll = currentScroll - delta;
            if (newScroll > 0) newScroll = 0;
            if (newScroll < maxScroll) newScroll = maxScroll;
            scroll.setScrollY(newScroll);
        }
    }

    private void activateHoveredOption() {
        List<Button> buttons = getDropdownOptionButtons();
        if (buttons.isEmpty()) return;
        int idx = Math.max(0, this.dropdownHoverIndex);
        if (idx >= buttons.size()) return;

        if (this.includePlaceholderOption && idx == 0) {
            setValue(null);
            closeDropdown(true);
            return;
        }

        int offset = this.includePlaceholderOption ? 1 : 0;
        int optionIndex = idx - offset;
        if (optionIndex >= 0 && optionIndex < this.options.size()) {
            setValue(this.options.get(optionIndex).value());
            closeDropdown(true);
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
