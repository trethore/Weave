package tytoo.weave.theme;

public class ThemeManager {
    private static Theme currentTheme = new DefaultTheme();

    public static Theme getTheme() {
        return currentTheme;
    }

    public static void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null.");
        }
        ThemeManager.currentTheme = theme;
    }
}