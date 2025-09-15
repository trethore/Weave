package tytoo.weave.animation;

@FunctionalInterface
public interface EasingFunction {
    double ease(double t);
}
