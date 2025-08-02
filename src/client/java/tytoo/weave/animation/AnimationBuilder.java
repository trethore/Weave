package tytoo.weave.animation;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.state.State;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.renderer.ColorableRenderer;

import java.awt.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class AnimationBuilder<C extends Component<C>> {
    private final C component;
    private long duration = 250;
    private Easing.EasingFunction easing = Easing.EASE_OUT_SINE;

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

    public <T> AnimationBuilder<C> animateProperty(State<T> state, T toValue, PropertyInterpolator<T> interpolator, @Nullable Consumer<T> onUpdate, String propertyKey) {
        if (onUpdate != null) {
            state.addListener(onUpdate);
        }
        Animation<T> animation = new Animation<>(state, toValue, duration, easing, interpolator, null);
        Animator.getInstance().add(new AnimationKey(component, propertyKey), animation);
        return this;
    }

    public AnimationBuilder<C> width(float to) {
        State<Float> widthState = component.getAnimatedState("width", component.getWidth());
        component.setWidth((c, p) -> widthState.get());
        return animateProperty(widthState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "width");
    }

    public AnimationBuilder<C> height(float to) {
        State<Float> heightState = component.getAnimatedState("height", component.getHeight());
        component.setHeight((c, p) -> heightState.get());
        return animateProperty(heightState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "height");
    }

    public AnimationBuilder<C> x(float to) {
        State<Float> xState = component.getAnimatedState("x", component.getRawLeft());
        component.setX((c, pW, cW) -> xState.get());
        return animateProperty(xState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "x");
    }

    public AnimationBuilder<C> y(float to) {
        State<Float> yState = component.getAnimatedState("y", component.getRawTop());
        component.setY((c, pH, cH) -> yState.get());
        return animateProperty(yState, to, Interpolators.FLOAT, v -> component.invalidateLayout(), "y");
    }

    public AnimationBuilder<C> opacity(float to) {
        return animateProperty(component.getOpacityState(), to, Interpolators.FLOAT, null, "opacity");
    }

    public AnimationBuilder<C> rotation(float to) {
        return animateProperty(component.getRotationState(), to, Interpolators.FLOAT, null, "rotation");
    }

    public AnimationBuilder<C> scale(float to) {
        animateProperty(component.getScaleXState(), to, Interpolators.FLOAT, null, "scaleX");
        animateProperty(component.getScaleYState(), to, Interpolators.FLOAT, null, "scaleY");
        return this;
    }

    public AnimationBuilder<C> scaleX(float to) {
        return animateProperty(component.getScaleXState(), to, Interpolators.FLOAT, null, "scaleX");
    }

    public AnimationBuilder<C> scaleY(float to) {
        return animateProperty(component.getScaleYState(), to, Interpolators.FLOAT, null, "scaleY");
    }

    public AnimationBuilder<C> color(Color to) {
        ComponentStyle style = component.getStyle();
        var base = style.getBaseRenderer();

        if (base instanceof ColorableRenderer colorable) {
            State<Color> colorState = component.getAnimatedState("color", colorable.getColor());
            return animateProperty(colorState, to, Interpolators.COLOR, colorable::setColor, "color");
        }

        if (base == null) {
            Color startColor = new Color(0, 0, 0, 0);
            tytoo.weave.style.renderer.SolidColorRenderer animatedRenderer = new tytoo.weave.style.renderer.SolidColorRenderer(startColor);
            State<Color> colorState = component.getAnimatedState("color", startColor);

            style.setBaseRenderer(animatedRenderer);
            return animateProperty(colorState, to, Interpolators.COLOR, animatedRenderer::setColor, "color");
        }

        return this;
    }

    public void then(Runnable onFinish) {
        if (onFinish == null) return;
        State<Byte> dummyState = new State<>((byte) 0);
        Consumer<Animation<Byte>> onFinishConsumer = animation -> onFinish.run();
        Animation<Byte> timer = new Animation<>(dummyState, (byte) 1, duration, Easing.LINEAR, (start, end, progress) -> start, onFinishConsumer);
        Animator.getInstance().add(new AnimationKey(component, "then_timer_" + System.nanoTime()), timer);
    }

    public record AnimationKey(Component<?> component, String property) {
    }
}