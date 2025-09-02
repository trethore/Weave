package tytoo.weave.theme;

import tytoo.weave.WeaveClient;
import tytoo.weave.ui.UIManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private static final Map<String, Object> globalStyleVariables = new HashMap<>();
    private static Theme currentTheme = new DefaultTheme();

    @SuppressWarnings("unchecked")
    public static <T extends Theme> T getTheme() {
        return (T) currentTheme;
    }

    public static void setTheme(Theme theme) {
        if (theme == null) {
            WeaveClient.LOGGER.error("Attempted to set a null theme.");
            throw new IllegalArgumentException("Theme cannot be null.");
        }
        ThemeManager.currentTheme = theme;
    }

    public static Stylesheet getStylesheet() {
        return currentTheme.getStylesheet();
    }

    public static Map<String, Object> getGlobalStyleVariables() {
        return Collections.unmodifiableMap(globalStyleVariables);
    }

    public static <T> void setGlobalVar(String name, T value) {
        globalStyleVariables.put(name, value);
        UIManager.invalidateAllStyles();
    }

    public static void clearGlobalVar(String name) {
        if (globalStyleVariables.remove(name) != null) {
            UIManager.invalidateAllStyles();
        }
    }
}
