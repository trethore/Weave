package tytoo.weave.theme;

public class ThemeManager {
    private static Theme currentTheme = new DefaultTheme();
    private static Stylesheet stylesheet = new Stylesheet();

    public static Theme getTheme() {
        return currentTheme;
    }

    public static void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null.");
        }
        ThemeManager.currentTheme = theme;
    }

    public static Stylesheet getStylesheet() {
        return stylesheet;
    }

    public static void setStylesheet(Stylesheet stylesheet) {
        ThemeManager.stylesheet = stylesheet;
    }
}