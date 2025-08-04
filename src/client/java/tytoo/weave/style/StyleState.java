package tytoo.weave.style;

public enum StyleState {
    NORMAL("normal"),
    HOVERED("hovered"),
    FOCUSED("focused"),
    ACTIVE("active"),
    DISABLED("disabled"),
    VALID("valid"),
    INVALID("invalid");

    private final String name;

    StyleState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}