package tytoo.weave.theme;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentStyle;

import java.util.HashMap;
import java.util.Map;

public class Stylesheet {
    private final Map<Class<? extends Component<?>>, ComponentStyle> styles = new HashMap<>();

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
}