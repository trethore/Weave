package tytoo.weave.component.components.interactive;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;
import tytoo.weave.style.Auto;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CheckBox extends InteractiveComponent<CheckBox> {

    private final State<Boolean> checkedState = new State<>(false);
    private final List<Consumer<Boolean>> checkChangedListeners = new ArrayList<>();
    private final Panel box;
    @Nullable
    private TextComponent<?> prefixLabel;
    @Nullable
    private TextComponent<?> suffixLabel;
    private boolean isUpdatingFromState = false;

    protected CheckBox() {
        var stylesheet = ThemeManager.getStylesheet();
        float boxSize = stylesheet.get(this, StyleProps.BOX_SIZE, 12f);
        float gap = stylesheet.get(this, StyleProps.GAP, 4f);

        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, gap));
        this.setHeight(Constraints.childBased());
        this.setWidth(Constraints.sumOfChildrenWidth(0, gap));


        this.box = Panel.create()
                .setWidth(Constraints.pixels(boxSize))
                .setHeight(Constraints.pixels(boxSize))
                .addStyleClass("checkbox-box")
                .addStyleState(StyleState.NORMAL);

        CheckMark checkMark = new CheckMark(this);
        checkMark.setWidth(Constraints.relative(1.0f));
        checkMark.setHeight(Constraints.relative(1.0f));
        this.box.addChild(checkMark);

        updateChildren();

        this.onMouseEnter(e -> this.box.addStyleState(StyleState.HOVERED));
        this.onMouseLeave(e -> this.box.removeStyleState(StyleState.HOVERED));
        this.onFocusGained(e -> this.box.addStyleState(StyleState.FOCUSED));
        this.onFocusLost(e -> this.box.removeStyleState(StyleState.FOCUSED));

        this.onClick(c -> this.setChecked(!this.isChecked()));


        updateVisualState(0L);
    }

    public static CheckBox create() {
        return new CheckBox();
    }

    public static CheckBox of(String label) {
        return create().setSuffixLabel(Text.of(label));
    }

    public static CheckBox of(Text label) {
        return create().setSuffixLabel(label);
    }

    @Override
    protected void updateVisualState() {
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.get(this, InteractiveComponent.StyleProps.ANIMATION_DURATION, 150L);
        updateVisualState(duration);
    }

    protected void updateVisualState(long duration) {
        // The visual state is now handled by the stylesheet resolving the renderer.
        // This method can be kept for future subclass overrides.
    }

    private void updateChildren() {
        this.removeAllChildren();
        if (this.prefixLabel != null) {
            prefixLabel.setMargin(1, Auto.AUTO);
            this.addChild(this.prefixLabel);
        }
        this.addChild(this.box);
        if (this.suffixLabel != null) {
            suffixLabel.setMargin(1, Auto.AUTO);
            this.addChild(this.suffixLabel);
        }
    }

    @Nullable
    public TextComponent<?> getPrefixLabel() {
        return this.prefixLabel;
    }

    public CheckBox setPrefixLabel(Text text) {
        this.prefixLabel = SimpleTextComponent.of(text);
        updateChildren();
        return this;
    }

    public CheckBox clearPrefixLabel() {
        this.prefixLabel = null;
        updateChildren();
        return this;
    }

    @Nullable
    public TextComponent<?> getSuffixLabel() {
        return this.suffixLabel;
    }

    public CheckBox setSuffixLabel(Text text) {
        this.suffixLabel = SimpleTextComponent.of(text);
        updateChildren();
        return this;
    }

    public CheckBox clearSuffixLabel() {
        this.suffixLabel = null;
        updateChildren();
        return this;
    }

    public boolean isChecked() {
        return this.checkedState.get();
    }

    public CheckBox setChecked(boolean checked) {
        if (this.checkedState.get() != checked) {
            this.checkedState.set(checked);
            if (!this.isUpdatingFromState) {
                for (Consumer<Boolean> listener : checkChangedListeners) {
                    listener.accept(checked);
                }
            }
        }
        return this;
    }

    public State<Boolean> getCheckedState() {
        return this.checkedState;
    }

    public CheckBox onCheckChanged(Consumer<Boolean> listener) {
        this.checkChangedListeners.add(listener);
        return this;
    }

    public CheckBox bindChecked(State<Boolean> state) {
        state.bind(newValue -> {
            try {
                this.isUpdatingFromState = true;
                this.setChecked(newValue);
            } finally {
                this.isUpdatingFromState = false;
            }
        });
        this.onCheckChanged(newChecked -> {
            if (!state.get().equals(newChecked)) {
                state.set(newChecked);
            }
        });
        return this;
    }

    public static final class StyleProps {
        public static final StyleProperty<Color> CHECK_COLOR = new StyleProperty<>("checkbox.check.color", Color.class);
        public static final StyleProperty<Float> BOX_SIZE = new StyleProperty<>("checkbox.box.size", Float.class);
        public static final StyleProperty<Float> GAP = new StyleProperty<>("checkbox.gap", Float.class);
        public static final StyleProperty<Float> CHECK_THICKNESS = new StyleProperty<>("checkbox.check.thickness", Float.class);

        private StyleProps() {
        }
    }

    private static class CheckMark extends Component<CheckMark> {
        private final CheckBox checkBox;

        public CheckMark(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        public void draw(DrawContext context) {
            if (!this.checkBox.isChecked()) return;

            float x = getLeft();
            float y = getTop();
            float w = getWidth();
            float h = getHeight();

            var stylesheet = ThemeManager.getStylesheet();
            Color checkColor = stylesheet.get(this.checkBox, StyleProps.CHECK_COLOR, Color.WHITE);
            float thickness = stylesheet.get(this.checkBox, StyleProps.CHECK_THICKNESS, Math.max(1f, w * 0.2f));

            float padding = w * 0.25f;
            Vector2f p1 = new Vector2f(x + padding, y + padding);
            Vector2f p2 = new Vector2f(x + w - padding, y + h - padding);
            Vector2f p3 = new Vector2f(x + w - padding, y + padding);
            Vector2f p4 = new Vector2f(x + padding, y + h - padding);

            Render2DUtils.drawLine(context, p1, p2, thickness, checkColor);
            Render2DUtils.drawLine(context, p3, p4, thickness, checkColor);
        }
    }
}