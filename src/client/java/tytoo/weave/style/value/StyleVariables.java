package tytoo.weave.style.value;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.theme.ThemeManager;

import java.util.Map;

public final class StyleVariables {
    private StyleVariables() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T resolve(@Nullable Component<?> component, String name, T defaultValue) {
        Component<?> current = component;
        while (current != null) {
            Map<String, Object> vars = current.getStyleVariables();
            if (vars != null && vars.containsKey(name)) {
                Object v = vars.get(name);
                if (v != null) return (T) v;
            }
            current = current.getParent();
        }

        Object global = ThemeManager.getGlobalStyleVariables().get(name);
        if (global != null) {
            return (T) global;
        }
        return defaultValue;
    }
}

