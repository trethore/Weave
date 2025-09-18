package tytoo.weave.style.contract;

import java.util.Objects;

public final class StyleProperty<T> {
    private final StyleSlot slot;
    private final Class<T> valueType;

    StyleProperty(StyleSlot slot, Class<T> valueType) {
        this.slot = Objects.requireNonNull(slot, "slot");
        this.valueType = Objects.requireNonNull(valueType, "valueType");
    }

    public StyleSlot slot() {
        return slot;
    }

    public Class<T> valueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return slot.toString();
    }
}
