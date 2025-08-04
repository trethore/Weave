package tytoo.weave.component.components.interactive;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CheckBox extends InteractiveComponent<CheckBox> {

    private final State<Boolean> checkedState = new State<>(false);
    private final List<Consumer<Boolean>> checkChangedListeners = new ArrayList<>();
    private final Panel box;
    @Nullable
    private TextComponent<?> label;
    private boolean isUpdatingFromState = false;

    protected CheckBox() {
        var stylesheet = ThemeManager.getStylesheet();
        float boxSize = stylesheet.get(getClass(), StyleProps.BOX_SIZE, 12f);
        float gap = stylesheet.get(getClass(), StyleProps.GAP, 4f);

        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, gap));
        this.setHeight(Constraints.childBased());
        this.setWidth(Constraints.childBased());

        this.box = Panel.create()
                .setWidth(Constraints.pixels(boxSize))
                .setHeight(Constraints.pixels(boxSize));

        this.box.getStyle().setColor(stylesheet.get(getClass(), InteractiveComponent.StyleProps.COLOR_NORMAL, new Color(40, 40, 40)));

        CheckMark checkMark = new CheckMark(this);
        checkMark.setWidth(Constraints.relative(1.0f));
        checkMark.setHeight(Constraints.relative(1.0f));
        this.box.addChild(checkMark);

        this.addChild(this.box);

        this.getStyle().setBaseRenderer(null);

        this.onClick(c -> this.setChecked(!this.isChecked()));

        updateVisualState();
    }

    public static CheckBox create() {
        return new CheckBox();
    }

    public static CheckBox of(String label) {
        return create().setLabel(Text.of(label));
    }

    public static CheckBox of(Text label) {
        return create().setLabel(label);
    }

    @Override
    protected void updateVisualState() {
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.get(this.getClass(), InteractiveComponent.StyleProps.ANIMATION_DURATION, 150L);

        Color normalColor = stylesheet.get(this.getClass(), InteractiveComponent.StyleProps.COLOR_NORMAL, new Color(80, 80, 80));
        Color hoveredColor = stylesheet.get(this.getClass(), InteractiveComponent.StyleProps.COLOR_HOVERED, new Color(100, 100, 100));
        Color focusedColor = stylesheet.get(this.getClass(), InteractiveComponent.StyleProps.COLOR_FOCUSED, new Color(120, 120, 120));

        Color targetColor = isFocused() ? focusedColor : (isHovered() ? hoveredColor : normalColor);

        this.box.animate().duration(duration).color(targetColor);
    }

    @Nullable
    public TextComponent<?> getLabel() {
        return this.label;
    }

    public CheckBox setLabel(Text text) {
        if (this.label != null) {
            this.removeChild(this.label);
        }
        this.label = SimpleTextComponent.of(text).setY(Constraints.center());
        this.addChild(this.label);
        invalidateLayout();
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
            Color checkColor = stylesheet.get(CheckBox.class, StyleProps.CHECK_COLOR, Color.WHITE);
            float thickness = stylesheet.get(CheckBox.class, StyleProps.CHECK_THICKNESS, Math.max(1f, w * 0.2f));

            Vector2f p1 = new Vector2f(x + w * 0.2f, y + h * 0.5f);
            Vector2f p2 = new Vector2f(x + w * 0.45f, y + h * 0.75f);
            Vector2f p3 = new Vector2f(x + w * 0.8f, y + h * 0.25f);

            drawLine(context, p1, p2, thickness, checkColor);
            drawLine(context, p2, p3, thickness, checkColor);
        }

        private void drawLine(DrawContext context, Vector2f p1, Vector2f p2, float thickness, Color color) {
            Vector2f dir = new Vector2f(p2).sub(p1);
            if (dir.lengthSquared() == 0) return;
            dir.normalize();
            Vector2f perp = new Vector2f(-dir.y, dir.x).mul(thickness / 2f);

            Vector2f v1 = new Vector2f(p1).add(perp);
            Vector2f v2 = new Vector2f(p2).add(perp);
            Vector2f v3 = new Vector2f(p2).sub(perp);
            Vector2f v4 = new Vector2f(p1).sub(perp);

            RenderSystem.enableBlend();
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

            Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            buffer.vertex(matrix, v1.x, v1.y, 0).color(color.getRGB());
            buffer.vertex(matrix, v2.x, v2.y, 0).color(color.getRGB());
            buffer.vertex(matrix, v3.x, v3.y, 0).color(color.getRGB());
            buffer.vertex(matrix, v4.x, v4.y, 0).color(color.getRGB());

            BuiltBuffer builtBuffer = buffer.end();
            if (builtBuffer != null) {
                BufferRenderer.drawWithGlobalProgram(builtBuffer);
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}