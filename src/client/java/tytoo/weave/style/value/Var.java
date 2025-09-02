package tytoo.weave.style.value;

import tytoo.weave.component.Component;

public final class Var<T> implements StyleValue<T> {
    private final StyleVariable<T> variable;
    private final T fallback;

    public Var(StyleVariable<T> variable) {
        this(variable, null);
    }

    public Var(StyleVariable<T> variable, T fallback) {
        this.variable = variable;
        this.fallback = fallback;
    }

    @Override
    public T resolve(Component<?> component) {
        T defaultValue = fallback != null ? fallback : variable.defaultValue();
        return StyleVariables.resolve(component, variable.name(), defaultValue);
    }
}

