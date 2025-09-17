package tytoo.weave.style.contract;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;

import java.util.Objects;

public final class StyleSlot {
    private static final Class<? extends Component<?>> ROOT_COMPONENT_CLASS = castComponentClass(Component.class);

    private final String id;
    private final Class<? extends Component<?>> componentType;
    private final Class<?> valueType;
    @Nullable
    private final String description;

    private StyleSlot(String id, Class<? extends Component<?>> componentType, Class<?> valueType, @Nullable String description) {
        this.id = Objects.requireNonNull(id, "id");
        this.componentType = Objects.requireNonNull(componentType, "componentType");
        this.valueType = Objects.requireNonNull(valueType, "valueType");
        this.description = description;
    }

    public static StyleSlot of(String id, Class<? extends Component<?>> componentType, Class<?> valueType) {
        return new StyleSlot(id, componentType, valueType, null);
    }

    public static StyleSlot of(String id, Class<? extends Component<?>> componentType, Class<?> valueType, @Nullable String description) {
        return new StyleSlot(id, componentType, valueType, description);
    }

    public static StyleSlot forRoot(String id, Class<?> valueType) {
        return new StyleSlot(id, ROOT_COMPONENT_CLASS, valueType, null);
    }

    public static StyleSlot forRoot(String id, Class<?> valueType, @Nullable String description) {
        return new StyleSlot(id, ROOT_COMPONENT_CLASS, valueType, description);
    }

    public static Class<? extends Component<?>> rootComponentClass() {
        return ROOT_COMPONENT_CLASS;
    }

    public static Class<? extends Component<?>> componentType(Class<?> type) {
        return castComponentClass(Objects.requireNonNull(type, "type"));
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Component<?>> castComponentClass(Class<?> type) {
        return (Class<? extends Component<?>>) type;
    }

    public @NotNull String id() {
        return id;
    }

    public Class<? extends Component<?>> componentType() {
        return componentType;
    }

    public Class<?> valueType() {
        return valueType;
    }

    public @Nullable String description() {
        return description;
    }

    public boolean supports(Component<?> component) {
        return componentType.isAssignableFrom(component.getClass());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleSlot styleSlot = (StyleSlot) o;
        return id.equals(styleSlot.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
