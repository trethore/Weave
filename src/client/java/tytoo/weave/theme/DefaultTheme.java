package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.ImageButton;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.Styling;
import tytoo.weave.utils.McUtils;

import java.awt.*;

public class DefaultTheme implements Theme {
    private static final Styling DEFAULT_TEXT_STYLE = Styling.create().color(Color.WHITE).shadow(true);
    private final Stylesheet stylesheet;

    public DefaultTheme() {
        this.stylesheet = new Stylesheet();
        populateDefaultStyles();
    }

    private void populateDefaultStyles() {
        // General
        stylesheet.setStyleFor(Window.class, new ComponentStyle().setColor(new Color(20, 20, 20, 220)));
        stylesheet.setStyleFor(Panel.class, new ComponentStyle().setColor(new Color(40, 40, 40, 200)));

        // Separator
        stylesheet.setStyleFor(Separator.class, new ComponentStyle().setColor(new Color(128, 128, 128)));

        // Button
        stylesheet.setProperty(Button.class, "color.normal", new Color(0, 255, 0));
        stylesheet.setProperty(Button.class, "color.hovered", new Color(255, 0, 0));
        stylesheet.setProperty(Button.class, "color.focused", new Color(0, 140, 255));
        stylesheet.setProperty(Button.class, "animation.duration", 150L);
        stylesheet.setStyleFor(Button.class, new ComponentStyle().setColor(stylesheet.getProperty(Button.class, "color.normal")));

        // ImageButton
        stylesheet.setProperty(ImageButton.class, "color.normal", new Color(100, 100, 100, 180));
        stylesheet.setProperty(ImageButton.class, "color.hovered", new Color(120, 120, 120, 180));
        stylesheet.setProperty(ImageButton.class, "color.focused", new Color(140, 140, 140, 180));
        stylesheet.setProperty(ImageButton.class, "animation.duration", 150L);
        stylesheet.setStyleFor(ImageButton.class, new ComponentStyle().setColor(stylesheet.getProperty(ImageButton.class, "color.normal")));

        // TextField properties
        stylesheet.setStyleFor(TextField.class, new ComponentStyle().setColor(new Color(20, 20, 20)));
        stylesheet.setProperty(TextField.class, "selectionColor", new Color(50, 100, 200, 128));
        stylesheet.setProperty(TextField.class, "borderColor.valid", new Color(0, 180, 0));
        stylesheet.setProperty(TextField.class, "borderColor.invalid", new Color(180, 0, 0));
        stylesheet.setProperty(TextField.class, "borderColor.focused", new Color(160, 160, 160));
        stylesheet.setProperty(TextField.class, "borderColor.unfocused", new Color(80, 80, 80));
        stylesheet.setProperty(TextField.class, "placeholderColor", new Color(150, 150, 150));
        stylesheet.setProperty(TextField.class, "cursorColor", Color.LIGHT_GRAY);
    }

    @Override
    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    @Override
    public TextRenderer getTextRenderer() {
        return McUtils.getMc().map(mc -> mc.textRenderer).orElse(null);
    }

    @Override
    public Styling getDefaultTextStyle() {
        return DEFAULT_TEXT_STYLE;
    }
}