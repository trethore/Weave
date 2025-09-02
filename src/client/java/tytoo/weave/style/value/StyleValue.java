package tytoo.weave.style.value;

import tytoo.weave.component.Component;

public interface StyleValue<T> {
    T resolve(Component<?> component);
}

