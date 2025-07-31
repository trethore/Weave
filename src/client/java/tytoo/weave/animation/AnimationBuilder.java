package tytoo.weave.animation;

import tytoo.weave.component.Component;
import tytoo.weave.state.State;

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
    private <T> void startAnimation(State<T> state, T toValue, PropertyInterpolator<T> interpolator, Consumer<T> onUpdate) {
        if (onUpdate != null) {
            state.addListener(onUpdate);
        }
        Consumer<Animation<T>> finalOnFinish = onFinish != null ? (Consumer<Animation<T>>) (Consumer<?>) onFinish : null;
        Animation<T> animation = new Animation<>(state, toValue, duration, easing, interpolator, finalOnFinish);
        Animator.getInstance().add(animation);
    }

    public void width(float to) {
        State<Float> widthState = new State<>(component.getWidth());
        component.setWidth((c, p) -> widthState.get());
        startAnimation(widthState, to, Interpolators.FLOAT, v -> component.invalidateLayout());
    }

    public void height(float to) {
        State<Float> heightState = new State<>(component.getHeight());
        component.setHeight((c, p) -> heightState.get());
        startAnimation(heightState, to, Interpolators.FLOAT, v -> component.invalidateLayout());
    }

    public void x(float to) {
        State<Float> xState = new State<>(component.getRawLeft());
        component.setX((c, pW, cW) -> xState.get());
        startAnimation(xState, to, Interpolators.FLOAT, v -> component.invalidateLayout());
    }

    public void y(float to) {
        State<Float> yState = new State<>(component.getRawTop());
        component.setY((c, pH, cH) -> yState.get());
        startAnimation(yState, to, Interpolators.FLOAT, v -> component.invalidateLayout());
    }

    public void opacity(float to) {
        State<Float> opacityState = new State<>(component.getOpacity());
        startAnimation(opacityState, to, Interpolators.FLOAT, component::setOpacity);
    }

    public void rotation(float to) {
        State<Float> rotationState = new State<>(component.getRotation());
        startAnimation(rotationState, to, Interpolators.FLOAT, component::setRotation);
    }

    public void scale(float to) {
        State<Float> scaleState = new State<>(component.getScaleX());
        startAnimation(scaleState, to, Interpolators.FLOAT, component::setScale);
    }

    public void scaleX(float to) {
        State<Float> scaleXState = new State<>(component.getScaleX());
        startAnimation(scaleXState, to, Interpolators.FLOAT, newScaleX -> component.setScale(newScaleX, component.getScaleY()));
    }

    public void scaleY(float to) {
        State<Float> scaleYState = new State<>(component.getScaleY());
        startAnimation(scaleYState, to, Interpolators.FLOAT, newScaleY -> component.setScale(component.getScaleX(), newScaleY));
    }

    public void color(Color to) {
        Color startColor = new Color(0, 0, 0, 0);
        var base = component.getStyle().getBaseRenderer();
        if (base instanceof tytoo.weave.style.renderer.SolidColorRenderer scr) {
            startColor = scr.getColor();
        }

        var animatedRenderer = new tytoo.weave.style.renderer.SolidColorRenderer(startColor);
        State<Color> colorState = new State<>(startColor);

        component.getStyle().setBaseRenderer(animatedRenderer);
        startAnimation(colorState, to, Interpolators.COLOR, animatedRenderer::setColor);
    }
}