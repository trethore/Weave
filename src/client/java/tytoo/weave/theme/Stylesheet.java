package tytoo.weave.theme;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.StyleProperty;

import java.util.HashMap;
import java.util.Map;

public class Stylesheet {
    private final Map<Class<? extends Component<?>>, ComponentStyle> styles = new HashMap<>();
    private final Map<Class<? extends Component<?>>, Map<StyleProperty<?>, Object>> properties = new HashMap<>();

    public <T extends Component<T>> void setStyleFor(Class<T> componentClass, ComponentStyle style) {
        styles.put(componentClass, style);
    }

    @Nullable
    public ComponentStyle getStyleFor(Class<?> componentClass) {
        Class<?> currentClass = componentClass;
        while (currentClass != null && Component.class.isAssignableFrom(currentClass)) {
            ComponentStyle style = styles.get(currentClass);
            if (style != null) {
                return style;
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    public <T> void set(Class<? extends Component<?>> componentClass, StyleProperty<T> property, T value) {
        properties.computeIfAbsent(componentClass, k -> new HashMap<>()).put(property, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> componentClass, StyleProperty<T> property, T defaultValue) {
        Class<?> currentClass = componentClass;
        while (currentClass != null && Component.class.isAssignableFrom(currentClass)) {
            Map<StyleProperty<?>, Object> classProperties = properties.get(currentClass);
            if (classProperties != null && classProperties.containsKey(property)) {
                Object value = classProperties.get(property);
                try {
                    return (T) value;
                } catch (ClassCastException e) {
                    return defaultValue;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return defaultValue;
    }
}