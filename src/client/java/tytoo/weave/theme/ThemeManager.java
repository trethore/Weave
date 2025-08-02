package tytoo.weave.theme;

import tytoo.weave.WeaveClient;

public class ThemeManager {
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
}