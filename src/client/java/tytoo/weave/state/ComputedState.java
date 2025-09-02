package tytoo.weave.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

class ComputedState<T> extends State<T> {
    private final Supplier<T> computer;
    private final Set<State<?>> dependencies = new HashSet<>();
    private final Map<State<?>, Consumer<?>> dependencyListeners = new HashMap<>();

    ComputedState(Supplier<T> computer) {
        super(null);
        this.computer = computer;
        this.recompute();
    }

    private void onDependencyChanged(Object ignored) {
        recompute();
    }

    private void recompute() {
        for (Map.Entry<State<?>, Consumer<?>> entry : dependencyListeners.entrySet()) {
            @SuppressWarnings("unchecked")
            Consumer<Object> listener = (Consumer<Object>) entry.getValue();
            @SuppressWarnings("unchecked")
            State<Object> dep = (State<Object>) entry.getKey();
            dep.removeListener(listener);
        }
        dependencyListeners.clear();
        dependencies.clear();

        State.computationStack.get().push(this);
        try {
            T newValue = this.computer.get();
            super.set(newValue);
        } finally {
            State.computationStack.get().pop();
        }
    }

    void addDependency(State<?> state) {
        if (dependencies.add(state)) {
            Consumer<Object> l = ignored -> recompute();
            dependencyListeners.put(state, l);
            @SuppressWarnings("unchecked")
            State<Object> dep = (State<Object>) state;
            dep.addListener(l);
        }
    }

    @Override
    public void set(T value) {
        throw new UnsupportedOperationException("Cannot set the value of a computed state directly.");
    }
}
