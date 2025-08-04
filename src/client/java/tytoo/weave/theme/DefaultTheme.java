package tytoo.weave.theme;

import net.minecraft.client.font.TextRenderer;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.InteractiveComponent.StyleProps;
import tytoo.weave.component.components.interactive.TextArea;
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
        Color buttonNormalColor = new Color(80, 80, 80);
        stylesheet.set(Button.class, StyleProps.COLOR_NORMAL, buttonNormalColor);
        stylesheet.set(Button.class, StyleProps.COLOR_HOVERED, new Color(100, 100, 100));
        stylesheet.set(Button.class, StyleProps.COLOR_FOCUSED, new Color(120, 120, 120));
        stylesheet.set(Button.class, StyleProps.COLOR_ACTIVE, new Color(60, 60, 60));
        stylesheet.set(Button.class, StyleProps.COLOR_DISABLED, new Color(50, 50, 50, 150));
        stylesheet.set(Button.class, StyleProps.ANIMATION_DURATION, 150L);
        stylesheet.setStyleFor(Button.class, new ComponentStyle()
                .setColor(stylesheet.get(Button.class, StyleProps.COLOR_NORMAL, buttonNormalColor))
        );

        Color imageButtonNormalColor = new Color(100, 100, 100, 180);
        stylesheet.set(ImageButton.class, StyleProps.COLOR_NORMAL, imageButtonNormalColor);
        stylesheet.set(ImageButton.class, StyleProps.COLOR_HOVERED, new Color(120, 120, 120, 180));
        stylesheet.set(ImageButton.class, StyleProps.COLOR_FOCUSED, new Color(140, 140, 140, 180));
        stylesheet.set(ImageButton.class, StyleProps.COLOR_ACTIVE, new Color(80, 80, 80, 180));
        stylesheet.set(ImageButton.class, StyleProps.COLOR_DISABLED, new Color(50, 50, 50, 120));
        stylesheet.set(ImageButton.class, StyleProps.ANIMATION_DURATION, 150L);
        stylesheet.set(ImageButton.class, ImageButton.StyleProps.IMAGE_BUTTON_PADDING, 5f);
        stylesheet.set(ImageButton.class, ImageButton.StyleProps.IMAGE_BUTTON_GAP, 4f);
        stylesheet.setStyleFor(ImageButton.class, new ComponentStyle()
                .setColor(stylesheet.get(ImageButton.class, StyleProps.COLOR_NORMAL, imageButtonNormalColor))
        );

        stylesheet.setStyleFor(TextField.class, new ComponentStyle().setColor(new Color(20, 20, 20)));
        stylesheet.set(TextField.class, StyleProps.COLOR_NORMAL, new Color(20, 20, 20));
        stylesheet.set(TextField.class, StyleProps.COLOR_HOVERED, new Color(20, 20, 20));
        stylesheet.set(TextField.class, StyleProps.COLOR_FOCUSED, new Color(20, 20, 20));
        stylesheet.set(TextField.class, StyleProps.COLOR_ACTIVE, new Color(20, 20, 20));
        stylesheet.set(TextField.class, StyleProps.COLOR_DISABLED, new Color(15, 15, 15, 150));
        stylesheet.set(TextField.class, TextField.StyleProps.SELECTION_COLOR, new Color(50, 100, 200, 128));
        stylesheet.set(TextField.class, TextField.StyleProps.BORDER_COLOR_VALID, new Color(0, 180, 0));
        stylesheet.set(TextField.class, TextField.StyleProps.BORDER_COLOR_INVALID, new Color(180, 0, 0));
        stylesheet.set(TextField.class, TextField.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
        stylesheet.set(TextField.class, TextField.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
        stylesheet.set(TextField.class, TextField.StyleProps.PLACEHOLDER_COLOR, new Color(150, 150, 150));
        stylesheet.set(TextField.class, TextField.StyleProps.CURSOR_COLOR, Color.LIGHT_GRAY);
        stylesheet.set(TextField.class, TextField.StyleProps.CURSOR_BLINK_INTERVAL, 500L);

        // TextArea
        stylesheet.setStyleFor(TextArea.class, new ComponentStyle().setColor(new Color(20, 20, 20)));
        stylesheet.set(TextArea.class, StyleProps.COLOR_NORMAL, new Color(20, 20, 20));
        stylesheet.set(TextArea.class, StyleProps.COLOR_HOVERED, new Color(20, 20, 20));
        stylesheet.set(TextArea.class, StyleProps.COLOR_FOCUSED, new Color(20, 20, 20));
        stylesheet.set(TextArea.class, StyleProps.COLOR_ACTIVE, new Color(20, 20, 20));
        stylesheet.set(TextArea.class, StyleProps.COLOR_DISABLED, new Color(15, 15, 15, 150));
        stylesheet.set(TextArea.class, TextArea.StyleProps.SELECTION_COLOR, new Color(50, 100, 200, 128));
        stylesheet.set(TextArea.class, TextArea.StyleProps.BORDER_COLOR_VALID, new Color(0, 180, 0));
        stylesheet.set(TextArea.class, TextArea.StyleProps.BORDER_COLOR_INVALID, new Color(180, 0, 0));
        stylesheet.set(TextArea.class, TextArea.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
        stylesheet.set(TextArea.class, TextArea.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
        stylesheet.set(TextArea.class, TextArea.StyleProps.PLACEHOLDER_COLOR, new Color(150, 150, 150));
        stylesheet.set(TextArea.class, TextArea.StyleProps.CURSOR_COLOR, Color.LIGHT_GRAY);
        stylesheet.set(TextArea.class, TextArea.StyleProps.CURSOR_BLINK_INTERVAL, 500L);

        // CheckBox
        stylesheet.set(CheckBox.class, StyleProps.COLOR_NORMAL, new Color(80, 80, 80));
        stylesheet.set(CheckBox.class, StyleProps.COLOR_HOVERED, new Color(100, 100, 100));
        stylesheet.set(CheckBox.class, StyleProps.COLOR_FOCUSED, new Color(120, 120, 120));
        stylesheet.set(CheckBox.class, CheckBox.StyleProps.CHECK_COLOR, Color.WHITE);
        stylesheet.set(CheckBox.class, CheckBox.StyleProps.BOX_SIZE, 12f);
        stylesheet.set(CheckBox.class, CheckBox.StyleProps.GAP, 4f);
        stylesheet.set(CheckBox.class, CheckBox.StyleProps.CHECK_THICKNESS, 2f);

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