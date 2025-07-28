package tytoo.weave.animation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Animator {
    private static final Animator INSTANCE = new Animator();
    private final List<Animation<?>> animations = new CopyOnWriteArrayList<>();

    private Animator() {
    }

    public static Animator getInstance() {
        return INSTANCE;
    }

    public void add(Animation<?> animation) {
        animation.start();
        this.animations.add(animation);
    }

    public void update() {
        if (animations.isEmpty()) return;

        for (Animation<?> animation : animations) {
            animation.update();
            if (animation.isFinished()) {
                animations.remove(animation);
            }
        }
    }
}