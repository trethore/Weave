package tytoo.weave.style;

import java.util.Objects;

public class StyleState {
    public static final StyleState NORMAL = new StyleState("normal");
    public static final StyleState HOVERED = new StyleState("hovered");
    public static final StyleState FOCUSED = new StyleState("focused");

    private final String name;

    public StyleState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleState that = (StyleState) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}