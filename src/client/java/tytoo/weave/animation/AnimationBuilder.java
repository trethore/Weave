package tytoo.weave.animation;


import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.BaseImage;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.state.State;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.renderer.ColorableRenderer;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

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

    public <T> AnimationBuilder<C> animateProperty(State<T> state, T toValue, PropertyInterpolator<T> interpolator, @Nullable Consumer<T> onUpdate, Object propertyKey) {
        if (onUpdate != null) {
            state.addListener(onUpdate);
        }
        Animation<T> animation = new Animation<>(state, toValue, duration, easing, interpolator, null);
        Animator.getInstance().add(new AnimationKey(component, propertyKey), animation);
        return this;
    }

    public <T> AnimationBuilder<C> animateProperty(State<T> state, T toValue, PropertyInterpolator<T> interpolator, @Nullable Consumer<T> onUpdate, String propertyKey) {
        return animateProperty(state, toValue, interpolator, onUpdate, (Object) propertyKey);
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
            State<Color> colorState = textComponent.getColorOverrideState();
            if (colorState.get() == null) {
                Stylesheet ss = ThemeManager.getStylesheet();
                Color currentColor = ss.get(textComponent, TextComponent.StyleProps.TEXT_COLOR, Color.WHITE);
                colorState.set(currentColor);
            }
            return animateProperty(colorState, to, Interpolators.COLOR, null, "color");
        } else if (component instanceof BaseImage<?> baseImage) {
            return animateProperty(baseImage.getColorState(), to, Interpolators.COLOR, null, "color");
        }

        ComponentStyle style = component.getStyle();
        ComponentRenderer baseRenderer = style.getBaseRenderer();
        Color startColor = new Color(0, 0, 0, 0);
        if (baseRenderer instanceof ColorableRenderer colorable && colorable.getColor() != null) {
            startColor = colorable.getColor();
        }

        SolidColorRenderer animatedRenderer = new SolidColorRenderer(startColor);
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

    public record AnimationKey(Component<?> component, Object property) {
    }
}
