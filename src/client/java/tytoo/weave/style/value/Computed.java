package tytoo.weave.style.value;

import tytoo.weave.component.Component;

import java.util.function.Function;

public final class Computed<T> implements StyleValue<T> {
    private final Function<Component<?>, T> computer;

    public Computed(Function<Component<?>, T> computer) {
        this.computer = computer;
    }

    @Override
    public T resolve(Component<?> component) {
        return computer.apply(component);
    }
}

