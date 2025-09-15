package tytoo.weave.animation;

@FunctionalInterface
public interface PropertyInterpolator<T> {
    T interpolate(T start, T end, double progress);
}
