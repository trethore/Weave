package tytoo.weave.animation;

import tytoo.weave.component.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Animator {
    private static final Animator INSTANCE = new Animator();
    private static final Map<Class<? extends Component<?>>, Function<? extends Component<?>, ? extends AnimationBuilder<?>>> BUILDER_PROVIDERS = new ConcurrentHashMap<>();
    private final Map<Object, Animation<?>> animations = new ConcurrentHashMap<>();

    private Animator() {
    }

    public static Animator getInstance() {
        return INSTANCE;
    }

    public static <C extends Component<C>> void registerBuilder(Class<C> componentClass, Function<C, AnimationBuilder<C>> provider) {
        BUILDER_PROVIDERS.put(componentClass, provider);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Component<C>> AnimationBuilder<C> getBuilderFor(C component) {
        Class<?> currentClass = component.getClass();
        while (currentClass != null && Component.class.isAssignableFrom(currentClass)) {
            Function<? extends Component<?>, ? extends AnimationBuilder<?>> provider = BUILDER_PROVIDERS.get(currentClass);
            if (provider != null) {
                Function<C, AnimationBuilder<C>> castedProvider = (Function<C, AnimationBuilder<C>>) provider;
                return castedProvider.apply(component);
            }
            currentClass = currentClass.getSuperclass();
        }
        return new AnimationBuilder<>(component);
    }

    public void add(Object key, Animation<?> animation) {
        Animation<?> oldAnimation = animations.put(key, animation);
        if (oldAnimation != null) {
            oldAnimation.finish();
        }
        animation.start();
    }

    public void update() {
        if (animations.isEmpty()) return;

        List<Animation<?>> finishedAnimations = null;

        Iterator<Map.Entry<Object, Animation<?>>> iterator = animations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Animation<?>> entry = iterator.next();
            Animation<?> animation = entry.getValue();
            animation.update();
            if (animation.isFinished()) {
                iterator.remove();
                if (animation.getOnFinish() != null) {
                    if (finishedAnimations == null) {
                        finishedAnimations = new ArrayList<>();
                    }
                    finishedAnimations.add(animation);
                }
            }
        }

        if (finishedAnimations != null) {
            for (Animation<?> animation : finishedAnimations) {
                animation.getOnFinish().accept(animation);
            }
        }
    }

    public void stop(Object key) {
        Animation<?> animation = animations.remove(key);
        if (animation != null) {
            animation.finish();
        }
    }

    public void stopAll(Component<?> component) {
        animations.entrySet().removeIf(entry -> {
            if (entry.getKey() instanceof AnimationBuilder.AnimationKey animKey && animKey.component() == component) {
                Animation<?> animation = entry.getValue();
                animation.finish();
                return true;
            }
            return false;
        });
    }
}