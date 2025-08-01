package tytoo.weave.animation;

import tytoo.weave.component.Component;
import tytoo.weave.state.State;
import tytoo.weave.style.renderer.ColorableRenderer;

import java.awt.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class AnimationBuilder<C extends Component<C>> {
    private final C component;
    private long duration = 250;
    private Easing.EasingFunction easing = Easing.EASE_OUT_SINE;
    private Consumer<Animation<?>> onFinish;

    public AnimationBuilder(C component) {
        this.component = component;
    }

    public AnimationBuilder<C> duration(long duration) {
        this.duration = duration;
        return this;
    }

    public AnimationBuilder<C> easing(Easing.EasingFunction easing) {
        this.easing = easing;
        return this;
    }

    public AnimationBuilder<C> onFinish(Consumer<Animation<?>> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> void startAnimation(State<T> state, T toValue, PropertyInterpolator<T> interpolator, Consumer<T> onUpdate, String propertyKey) {
        if (onUpdate != null) {
            state.addListener(onUpdate);
        }
        Consumer<Animation<T>> finalOnFinish = onFinish != null ? (Consumer<Animation<T>>) (Consumer<?>) onFinish : null;
        Animation<T> animation = new Animation<>(state, toValue, duration, easing, interpolator, finalOnFinish);
        Animator.getInstance().add(new AnimationKey(component, propertyKey), animation);
    }

    public void width(float to) {
        State<Float> widthState = component.getAnimatedState("width", component.getWidth());
        component.setWidth((c, p) -> widthState.get());
        startAnimation(widthState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "width");
    }

    public void height(float to) {
        State<Float> heightState = component.getAnimatedState("height", component.getHeight());
        component.setHeight((c, p) -> heightState.get());
        startAnimation(heightState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "height");
    }

    public void x(float to) {
        State<Float> xState = component.getAnimatedState("x", component.getRawLeft());
        component.setX((c, pW, cW) -> xState.get());
        startAnimation(xState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "x");
    }

    public void y(float to) {
        State<Float> yState = component.getAnimatedState("y", component.getRawTop());
        component.setY((c, pH, cH) -> yState.get());
        startAnimation(yState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "y");
    }

    public void opacity(float to) {
        startAnimation(component.getOpacityState(), to, Interpolators.FLOAT, null, "opacity");
    }

    public void rotation(float to) {
        startAnimation(component.getRotationState(), to, Interpolators.FLOAT, null, "rotation");
    }

    public void scale(float to) {
        Animator.getInstance().stop(new AnimationKey(component, "scaleX"));
        Animator.getInstance().stop(new AnimationKey(component, "scaleY"));
        startAnimation(component.getScaleXState(), to, Interpolators.FLOAT, null, "scaleX");
        startAnimation(component.getScaleYState(), to, Interpolators.FLOAT, null, "scaleY");
    }

    public void scaleX(float to) {
        startAnimation(component.getScaleXState(), to, Interpolators.FLOAT, null, "scaleX");
    }

    public void scaleY(float to) {
        startAnimation(component.getScaleYState(), to, Interpolators.FLOAT, null, "scaleY");
    }

    public void color(Color to) {
        var base = component.getStyle().getBaseRenderer();

        if (base instanceof ColorableRenderer colorable) {
            State<Color> colorState = component.getAnimatedState("color", colorable.getColor());
            startAnimation(colorState, to, Interpolators.COLOR, colorable::setColor, "color");
            return;
        }

        Color startColor = (base instanceof tytoo.weave.style.renderer.SolidColorRenderer scr) ? scr.getColor() : new Color(0, 0, 0, 0);
        var animatedRenderer = new tytoo.weave.style.renderer.SolidColorRenderer(startColor);
        State<Color> colorState = component.getAnimatedState("color", startColor);

        component.getStyle().setBaseRenderer(animatedRenderer);
        startAnimation(colorState, to, Interpolators.COLOR, animatedRenderer::setColor, "color");
    }

    private record AnimationKey(Component<?> component, String property) {
    }
}