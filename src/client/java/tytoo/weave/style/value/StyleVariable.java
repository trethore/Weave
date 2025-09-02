package tytoo.weave.style.value;

import java.util.Objects;

public record StyleVariable<T>(String name, T defaultValue) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleVariable<?> that = (StyleVariable<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

