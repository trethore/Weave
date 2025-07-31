package tytoo.weave.theme;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;

import java.util.HashMap;
import java.util.Map;

public class Stylesheet {
    private final Map<Class<? extends Component<?>>, ComponentStyle> styles = new HashMap<>();
    private final Map<Class<? extends Component<?>>, Map<String, Object>> properties = new HashMap<>();

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

    public <T> void setProperty(Class<? extends Component<?>> componentClass, String key, T value) {
        properties.computeIfAbsent(componentClass, k -> new HashMap<>()).put(key, value);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getProperty(Class<?> componentClass, String key) {
        Class<?> currentClass = componentClass;
        while (currentClass != null && Component.class.isAssignableFrom(currentClass)) {
            Map<String, Object> classProperties = properties.get(currentClass);
            if (classProperties != null && classProperties.containsKey(key)) {
                return (T) classProperties.get(key);
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    public <T> T getProperty(Class<?> componentClass, String key, T defaultValue) {
        T value = getProperty(componentClass, key);
        return value != null ? value : defaultValue;
    }
}