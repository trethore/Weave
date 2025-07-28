package tytoo.weave.animation;

import tytoo.weave.component.Component;
import tytoo.weave.state.State;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.style.renderer.SolidColorRenderer;

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

    public void width(float to) {
        State<Float> widthState = new State<>(component.getWidth());
        component.setWidth(c -> widthState.get());
        Consumer<Animation<Float>> finalOnFinish = onFinish != null ? onFinish::accept : null;
        Animation<Float> animation = new Animation<>(widthState, to, duration, easing, Interpolators.FLOAT, finalOnFinish);
        Animator.getInstance().add(animation);
    }

    public void height(float to) {
        State<Float> heightState = new State<>(component.getHeight());
        component.setHeight(c -> heightState.get());
        Consumer<Animation<Float>> finalOnFinish = onFinish != null ? onFinish::accept : null;
        Animation<Float> animation = new Animation<>(heightState, to, duration, easing, Interpolators.FLOAT, finalOnFinish);
        Animator.getInstance().add(animation);
    }

    public void x(float to) {
        State<Float> xState = new State<>(component.getRawLeft());
        component.setX(c -> xState.get());
        Consumer<Animation<Float>> finalOnFinish = onFinish != null ? onFinish::accept : null;
        Animation<Float> animation = new Animation<>(xState, to, duration, easing, Interpolators.FLOAT, finalOnFinish);
        Animator.getInstance().add(animation);
    }

    public void y(float to) {
        State<Float> yState = new State<>(component.getRawTop());
        component.setY(c -> yState.get());
        Consumer<Animation<Float>> finalOnFinish = onFinish != null ? onFinish::accept : null;
        Animation<Float> animation = new Animation<>(yState, to, duration, easing, Interpolators.FLOAT, finalOnFinish);
        Animator.getInstance().add(animation);
    }

    public void color(Color to) {
        Color startColor = new Color(0, 0, 0, 0);
        ComponentRenderer base = component.getStyle().getBaseRenderer();
        if (base instanceof SolidColorRenderer scr) startColor = scr.getColor();

        SolidColorRenderer animatedRenderer = new SolidColorRenderer(startColor);
        State<Color> colorState = new State<>(startColor);

        component.getStyle().setBaseRenderer(animatedRenderer);
        colorState.bind(animatedRenderer::setColor);
        Consumer<Animation<Color>> finalOnFinish = onFinish != null ? onFinish::accept : null;
        Animation<Color> animation = new Animation<>(colorState, to, duration, easing, Interpolators.COLOR, finalOnFinish);
        Animator.getInstance().add(animation);
    }
}