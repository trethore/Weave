package tytoo.weave.animation;

import tytoo.weave.state.State;

import java.util.function.Consumer;

public class Animation<T> {
    private final State<T> target;
    private final T endValue;
    private final long duration;
    private final Easing.EasingFunction easing;
    private final PropertyInterpolator<T> interpolator;
    private final Consumer<Animation<T>> onFinish;

    private T startValue;
    private long startTime = -1;
    private boolean finished = false;

    public Animation(State<T> target, T endValue, long duration, Easing.EasingFunction easing, PropertyInterpolator<T> interpolator, Consumer<Animation<T>> onFinish) {
        this.target = target;
        this.endValue = endValue;
        this.duration = duration;
        this.easing = easing;
        this.interpolator = interpolator;
        this.onFinish = onFinish;
    }

    void start() {
        this.startTime = System.currentTimeMillis();
        this.startValue = this.target.get();
    }

    void update() {
        if (finished || startTime == -1) return;

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1.0f, (float) elapsed / duration);
        float easedProgress = easing.ease(progress);

        target.set(interpolator.interpolate(startValue, endValue, easedProgress));

        if (progress >= 1.0f) {
            finished = true;
            if (onFinish != null) {
                onFinish.accept(this);
            }
        }
    }

    public void finish() {
        this.finished = true;
    }

    public boolean isFinished() {
        return finished;
    }
}