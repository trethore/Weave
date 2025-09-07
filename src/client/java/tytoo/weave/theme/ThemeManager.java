package tytoo.weave.theme;

import tytoo.weave.WeaveCore;
import tytoo.weave.ui.UIManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    static final ThreadLocal<Stylesheet> ACTIVE_STYLESHEET = new ThreadLocal<>();
    private static final Map<String, Object> globalStyleVariables = new HashMap<>();
    private static Theme currentTheme = new DefaultTheme();

    @SuppressWarnings("unchecked")
    public static <T extends Theme> T getTheme() {
        return (T) currentTheme;
    }

    public static void setTheme(Theme theme) {
        if (theme == null) {
            WeaveCore.LOGGER.error("Attempted to set a null theme.");
            throw new IllegalArgumentException("Theme cannot be null.");
        }
        ThemeManager.currentTheme = theme;
        UIManager.invalidateAllStyles();
    }

    public static Stylesheet getStylesheet() {
        Stylesheet active = ACTIVE_STYLESHEET.get();
        return active != null ? active : currentTheme.getStylesheet();
    }

    public static void pushActiveStylesheet(Stylesheet stylesheet) {
        ACTIVE_STYLESHEET.set(stylesheet);
    }

    public static void popActiveStylesheet() {
        ACTIVE_STYLESHEET.remove();
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
