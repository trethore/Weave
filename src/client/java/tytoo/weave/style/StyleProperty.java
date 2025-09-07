package tytoo.weave.style;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record StyleProperty<T>(String name, Class<?> type) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleProperty<?> that = (StyleProperty<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public @NotNull String toString() {
        return "StyleProperty{" +
                "name='" + name + '\'' +
                '}';
    }
}
