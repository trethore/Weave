package tytoo.weave.style;

import java.util.Objects;

public final class StyleProperty<T> {
    private final String name;
    private final Class<T> type;

    public StyleProperty(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

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
    public String toString() {
        return "StyleProperty{" +
                "name='" + name + '\'' +
                '}';
    }
}