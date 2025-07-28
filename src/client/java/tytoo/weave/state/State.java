package tytoo.weave.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class State<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private T value;

    public State(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (!Objects.equals(this.value, newValue)) {
            this.value = newValue;
            for (Consumer<T> listener : new ArrayList<>(listeners)) {
                listener.accept(newValue);
            }
        }
    }

    public void addListener(Consumer<T> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }

    public void bind(Consumer<T> setter) {
        addListener(setter);
        setter.accept(this.value);
    }
}