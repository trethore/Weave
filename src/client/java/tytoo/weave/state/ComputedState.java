package tytoo.weave.state;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

class ComputedState<T> extends State<T> {
    private final Supplier<T> computer;
    private final Set<State<?>> dependencies = new HashSet<>();

    ComputedState(Supplier<T> computer) {
        super(null);
        this.computer = computer;
        this.recompute();
    }

    private void onDependencyChanged(Object ignored) {
        recompute();
    }

    private void recompute() {
        for (State<?> oldDependency : dependencies) {
            oldDependency.removeListener(this::onDependencyChanged);
        }
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
            state.addListener(this::onDependencyChanged);
        }
    }

    @Override
    public void set(T value) {
        throw new UnsupportedOperationException("Cannot set the value of a computed state directly.");
    }
}