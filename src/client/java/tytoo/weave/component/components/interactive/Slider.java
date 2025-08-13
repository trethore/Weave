package tytoo.weave.component.components.interactive;

import tytoo.weave.animation.Easing;
import tytoo.weave.animation.Interpolators;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Slider<N extends Number & Comparable<N>> extends InteractiveComponent<Slider<N>> {

    private final State<N> valueState;
    private final Orientation orientation;
    private final Function<Double, N> fromDouble;
    private final Panel thumb;
    private final State<Float> visualProgressState;
    private final float trackPadding;
    private N min, max, step;
    private boolean isUpdatingFromState = false;
    private boolean isDragging = false;

    protected Slider(N min, N max, N initialValue, N step, Orientation orientation, Function<Double, N> fromDouble) {
        this.setFocusable(true);
        this.min = min;
        this.max = max;
        this.step = step;
        this.orientation = orientation;
        this.fromDouble = fromDouble;

        this.addStyleState(StyleState.NORMAL);
        this.addStyleClass("interactive-visual");

        this.thumb = Panel.create();
        var stylesheet = ThemeManager.getStylesheet();
        Color thumbColor = stylesheet.get(this, StyleProps.THUMB_COLOR, new Color(160, 160, 160));
        this.trackPadding = stylesheet.get(this, StyleProps.TRACK_PADDING, 4f);
        this.thumb.getStyle().setColor(thumbColor);

        if (orientation == Orientation.HORIZONTAL) {
            this.setHeight(Constraints.pixels(20));
            this.setWidth(Constraints.pixels(150));
            thumb.setWidth(Constraints.pixels(8));
            thumb.setHeight(Constraints.relative(0.8f));
            thumb.setY(Constraints.center());
        } else {
            this.setWidth(Constraints.pixels(20));
            this.setHeight(Constraints.pixels(150));
            thumb.setHeight(Constraints.pixels(8));
            thumb.setWidth(Constraints.relative(0.8f));
            thumb.setX(Constraints.center());
        }
        this.addChild(thumb);

        this.onMouseClick(this::handleMouseClick);
        this.onMouseDrag(this::handleMouseDrag);

        this.onMouseRelease(event -> {
            this.isDragging = false;
            if (isFocused()) {
                double snappedProgress = calculateProgressForValue(getValue());
                animateVisualProgress((float) snappedProgress);
            }
        });

        this.valueState = new State<>(clamp(initialValue));
        this.visualProgressState = new State<>(0f);

        this.visualProgressState.addListener(p -> updateThumbPosition());
        this.visualProgressState.set((float) calculateProgressForValue(valueState.get()));
        this.updateThumbPosition();
    }

    public static Slider<Integer> integerSlider(Orientation orientation, int min, int max, int initialValue) {
        return new Slider<>(min, max, initialValue, 1, orientation, val -> (int) Math.round(val));
    }

    public static Slider<Integer> integerSlider(Orientation orientation, int min, int max, int initialValue, int step) {
        return new Slider<>(min, max, initialValue, step, orientation, val -> (int) Math.round(val));
    }

    public static Slider<Float> floatSlider(Orientation orientation, float min, float max, float initialValue) {
        return new Slider<>(min, max, initialValue, null, orientation, Double::floatValue);
    }

    public static Slider<Float> floatSlider(Orientation orientation, float min, float max, float initialValue, float step) {
        return new Slider<>(min, max, initialValue, step, orientation, Double::floatValue);
    }

    public static Slider<Double> doubleSlider(Orientation orientation, double min, double max, double initialValue) {
        return new Slider<>(min, max, initialValue, null, orientation, val -> val);
    }

    public static Slider<Double> doubleSlider(Orientation orientation, double min, double max, double initialValue, double step) {
        return new Slider<>(min, max, initialValue, step, orientation, val -> val);
    }

    private void handleMouseClick(MouseClickEvent event) {
        double rawProgress = calculateRawProgress(event.getX(), event.getY());
        updateValueFromProgress(rawProgress, true);
    }

    private void handleMouseDrag(MouseDragEvent event) {
        this.isDragging = true;
        double rawProgress = calculateRawProgress(event.getX(), event.getY());
        updateValueFromProgress(rawProgress, false);
    }

    private void updateValueFromProgress(double rawProgress, boolean animate) {
        N value = getValueFromProgress(rawProgress);
        N snappedValue = (step != null && step.doubleValue() > 0) ? snapToStep(value) : value;

        if (valueState.get().compareTo(snappedValue) != 0) {
            valueState.set(snappedValue);
        }

        if (animate) {
            double snappedProgress = calculateProgressForValue(snappedValue);
            animateVisualProgress((float) snappedProgress);
        } else {
            visualProgressState.set((float) rawProgress);
        }
    }

    private void animateVisualProgress(float targetProgress) {
        this.thumb.animate().duration(100).easing(Easing.EASE_OUT_SINE)
                .animateProperty(this.visualProgressState, targetProgress, Interpolators.FLOAT, null, "slider_progress");
    }

    private double calculateRawProgress(float mouseX, float mouseY) {
        double rawProgress;
        if (orientation == Orientation.HORIZONTAL) {
            float totalWidth = getInnerWidth();
            float thumbWidth = thumb.getFinalWidth();
            float trackWidth = totalWidth - 2 * trackPadding;
            float clickableWidth = trackWidth - thumbWidth;
            if (clickableWidth <= 0) return 0.0;

            float relativeMouseX = mouseX - getInnerLeft() - trackPadding - (thumbWidth / 2f);
            rawProgress = relativeMouseX / clickableWidth;
        } else {
            float totalHeight = getInnerHeight();
            float thumbHeight = thumb.getFinalHeight();
            float trackHeight = totalHeight - 2 * trackPadding;
            float clickableHeight = trackHeight - thumbHeight;
            if (clickableHeight <= 0) return 0.0;

            float relativeMouseY = mouseY - getInnerTop() - trackPadding - (thumbHeight / 2f);
            rawProgress = 1 - (relativeMouseY / clickableHeight);
        }
        return Math.max(0, Math.min(1, rawProgress));
    }

    private double calculateProgressForValue(N value) {
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        double range = maxVal - minVal;
        return (range == 0) ? 0 : (val - minVal) / range;
    }

    private N getValueFromProgress(double progress) {
        double range = this.max.doubleValue() - this.min.doubleValue();
        return this.fromDouble.apply(this.min.doubleValue() + progress * range);
    }

    private void updateThumbPosition() {
        float progress = visualProgressState.get();
        if (orientation == Orientation.HORIZONTAL) {
            thumb.setX((component, parentWidth, componentWidth) -> trackPadding + (parentWidth - componentWidth - 2 * trackPadding) * progress);
        } else {
            thumb.setY((component, parentHeight, componentHeight) -> trackPadding + (parentHeight - componentHeight - 2 * trackPadding) * (1 - progress));
        }
        thumb.invalidateLayout();
    }

    private N snapToStep(N value) {
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double stepVal = step.doubleValue();
        if (stepVal <= 0) return value;

        double snapped = Math.round((val - minVal) / stepVal) * stepVal + minVal;
        return fromDouble.apply(snapped);
    }

    private N clamp(N value) {
        if (value.compareTo(min) < 0) return min;
        if (value.compareTo(max) > 0) return max;
        return value;
    }

    public State<N> getValueState() {
        return valueState;
    }

    public N getValue() {
        return valueState.get();
    }

    public void setValue(N value) {
        N clampedValue = clamp(value);
        N snappedValue = (step != null && step.doubleValue() > 0) ? snapToStep(clampedValue) : clampedValue;

        isUpdatingFromState = true;
        if (valueState.get().compareTo(snappedValue) != 0) {
            valueState.set(snappedValue);
        }
        isUpdatingFromState = false;
        if (!this.isDragging) {
            animateVisualProgress((float) calculateProgressForValue(snappedValue));
        }
    }

    public Slider<N> onValueChanged(Consumer<N> listener) {
        this.valueState.addListener(listener);
        return this;
    }

    public Slider<N> bindValue(State<N> state) {
        this.onValueChanged(newValue -> {
            if (isUpdatingFromState) return;
            if (state.get() == null || state.get().compareTo(newValue) != 0) {
                state.set(newValue);
            }
        });

        state.bind(newValue -> {
            if (isUpdatingFromState) return;
            if (newValue == null) return;
            try {
                isUpdatingFromState = true;
                this.setValue(newValue);
            } finally {
                isUpdatingFromState = false;
            }
        });

        return this;
    }

    public N getMin() {
        return min;
    }

    public Slider<N> setMin(N min) {
        this.min = min;
        setValue(valueState.get());
        updateThumbPosition();
        return this;
    }

    public N getMax() {
        return max;
    }

    public Slider<N> setMax(N max) {
        this.max = max;
        setValue(valueState.get());
        updateThumbPosition();
        return this;
    }

    public N getStep() {
        return step;
    }

    public Slider<N> setStep(N step) {
        this.step = step;
        setValue(valueState.get());
        return this;
    }

    public Panel getThumb() {
        return thumb;
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public static final class StyleProps {
        public static final StyleProperty<Color> THUMB_COLOR = new StyleProperty<>("slider.thumb.color", Color.class);
        public static final StyleProperty<Float> TRACK_PADDING = new StyleProperty<>("slider.track.padding", Float.class);

        private StyleProps() {
        }
    }
}