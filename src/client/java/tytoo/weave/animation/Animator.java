package tytoo.weave.animation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Animator {
    private static final Animator INSTANCE = new Animator();
    private final Map<Object, Animation<?>> animations = new ConcurrentHashMap<>();

    private Animator() {
    }

    public static Animator getInstance() {
        return INSTANCE;
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

        var iterator = animations.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var animation = entry.getValue();
            animation.update();
            if (animation.isFinished()) {
                iterator.remove();
            }
        }
    }

    public void stop(Object key) {
        Animation<?> animation = animations.remove(key);
        if (animation != null) {
            animation.finish();
        }
    }
}