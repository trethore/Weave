package tytoo.weave.animation;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.state.State;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.renderer.ColorableRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;

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
        if (component instanceof TextComponent<?> textComponent) {
            tytoo.weave.style.Styling currentStyling = textComponent.getBaseStyle();
            Color startColor = Color.WHITE;
            if (currentStyling != null && currentStyling.getColor() != null) {
                startColor = currentStyling.getColor();
            }

            State<Color> colorState = new State<>(startColor);
            Consumer<Color> onUpdate = newColor -> {
                tytoo.weave.style.Styling style = textComponent.getBaseStyle();
                if (style == null) {
                    style = tytoo.weave.style.Styling.create();
                }
                textComponent.setStyle(style.color(newColor));
            };
            return animateProperty(colorState, to, Interpolators.COLOR, onUpdate, "color");
        }

        ComponentStyle style = component.getStyle();
        Color startColor = (style.getBaseRenderer() instanceof ColorableRenderer colorable) ? colorable.getColor() : new Color(0, 0, 0, 0);
        SolidColorRenderer animatedRenderer = new tytoo.weave.style.renderer.SolidColorRenderer(startColor);
        State<Color> colorState = new State<>(startColor);
        style.setBaseRenderer(animatedRenderer);
        return animateProperty(colorState, to, Interpolators.COLOR, animatedRenderer::setColor, "color");
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