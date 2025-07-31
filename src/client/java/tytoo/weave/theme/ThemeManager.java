package tytoo.weave.theme;

public class ThemeManager {
    private static Theme currentTheme = new DefaultTheme();

    @SuppressWarnings("unchecked")
    public static <T extends Theme> T getTheme() {
        return (T) currentTheme;
    }

    public static void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null.");
        }
        ThemeManager.currentTheme = theme;
    }

    public static Stylesheet getStylesheet() {
        return currentTheme.getStylesheet();
    }
}