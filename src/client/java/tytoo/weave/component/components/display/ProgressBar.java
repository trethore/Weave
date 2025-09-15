package tytoo.weave.component.components.display;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import tytoo.weave.animation.Easings;
import tytoo.weave.animation.Interpolators;
import tytoo.weave.component.Component;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;

public class ProgressBar extends Component<ProgressBar> {
    private final State<Float> valueState;
    private final State<Float> visualProgressState;
    private final State<Boolean> halfState;
    private final State<Boolean> completeState;
    private final State<Float> progressState;
    private float max;

    protected ProgressBar(float max, float initialValue) {
        this.addStyleState(StyleState.NORMAL);
        this.setHittable(true);

        float defaultWidth = ThemeManager.getStylesheet().get(this, StyleProps.DEFAULT_WIDTH, 150f);
        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight((component, parentHeight) -> ThemeManager.getStylesheet().get(this, StyleProps.THICKNESS, 8f));

        this.onMouseEnter(e -> setStyleState(StyleState.HOVERED, true));
        this.onMouseLeave(e -> setStyleState(StyleState.HOVERED, false));

        this.max = Math.max(0.0001f, max);
        this.valueState = new State<>(clampValue(initialValue));
        this.progressState = State.computed(() -> safeDivide(this.valueState.get(), this.max));
        this.halfState = State.computed(() -> this.progressState.get() >= 0.5f);
        this.completeState = State.computed(() -> this.progressState.get() >= 1.0f);

        this.visualProgressState = new State<>(this.progressState.get());
        this.valueState.addListener(v -> animateToProgress(safeDivide(v, this.max)));
    }

    public static ProgressBar create() {
        return new ProgressBar(1.0f, 0.0f);
    }

    public static ProgressBar of(float max, float initialValue) {
        return new ProgressBar(max, initialValue);
    }

    private float safeDivide(float a, float b) {
        if (b == 0.0f) return 0.0f;
        return Math.max(0.0f, Math.min(1.0f, a / b));
    }

    private float clampValue(float v) {
        if (v < 0.0f) return 0.0f;
        return Math.min(v, max);
    }

    private void animateToProgress(float targetProgress) {
        long duration = ThemeManager.getStylesheet().get(this, StyleProps.ANIMATION_DURATION, 200L);
        if (duration < 0L) duration = 0L;
        this.animate().duration(duration).easing(Easings.EASE_OUT_SINE)
                .animateProperty(this.visualProgressState, targetProgress, Interpolators.FLOAT, null, "progress_value");
    }

    public float getValue() {
        return this.valueState.get();
    }

    public ProgressBar setValue(float value) {
        this.valueState.set(clampValue(value));
        return this;
    }

    public State<Float> getValueState() {
        return this.valueState;
    }

    public State<Float> getProgressState() {
        return this.progressState;
    }

    public State<Boolean> getHalfState() {
        return this.halfState;
    }

    public State<Boolean> getCompleteState() {
        return this.completeState;
    }

    public ProgressBar bindValue(@NotNull State<Float> external) {
        this.valueState.addListener(v -> {
            Float current = external.get();
            if (current == null || Math.abs(current - v) > 1e-6) {
                external.set(v);
            }
        });
        external.bind(v -> {
            if (v == null) return;
            this.setValue(v);
        });
        return this;
    }

    public float getMax() {
        return max;
    }

    public ProgressBar setMax(float max) {
        if (max <= 0.0f) max = 0.0001f;
        this.max = max;
        float clamped = clampValue(this.valueState.get());
        if (clamped != this.valueState.get()) {
            this.valueState.set(clamped);
        } else {
            animateToProgress(safeDivide(clamped, this.max));
        }
        return this;
    }

    public float getVisualProgress() {
        return this.visualProgressState.get();
    }

    public FillPolicy getFillPolicy() {
        FillPolicy policy = ThemeManager.getStylesheet().get(this, StyleProps.FILL_POLICY, FillPolicy.LEFT_TO_RIGHT);
        return policy != null ? policy : FillPolicy.LEFT_TO_RIGHT;
    }

    @Override
    public void draw(DrawContext context) {
        super.draw(context);
    }

    public enum FillPolicy {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        CENTER_OUT
    }

    public static final class StyleProps {
        public static final StyleSlot THICKNESS = StyleSlot.of("progress.thickness", ProgressBar.class, Float.class);
        public static final StyleSlot DEFAULT_WIDTH = StyleSlot.of("progress.default-width", ProgressBar.class, Float.class);
        public static final StyleSlot VALUE_COLOR = StyleSlot.of("progress.value.color", ProgressBar.class, Color.class);
        public static final StyleSlot BACKGROUND_COLOR = StyleSlot.of("progress.background.color", ProgressBar.class, Color.class);
        public static final StyleSlot ANIMATION_DURATION = StyleSlot.of("progress.animation-duration", ProgressBar.class, Long.class);
        public static final StyleSlot FILL_POLICY = StyleSlot.of("progress.fill-policy", ProgressBar.class, FillPolicy.class);

        private StyleProps() {
        }
    }
}
