package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.TextArea;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.Styling;

import java.awt.*;

public class TestGui extends WeaveScreen {

    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5));
        window.setPadding(10);

        Panel titlePanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.childBased(10))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER));

        TextComponent titleText = TextComponent.of("Weave Test UI")
                .setStyle(Styling.create()
                        .color(Color.BLACK)
                        .shadow(true)
                        .colorWave(new ColorWave(ColorWave.createRainbow(36), 2f)))
                .setScale(1.5f);

        titlePanel.addChildren(titleText);

        Panel contentPanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setPadding(10)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, 5))
                .setLayoutData(LinearLayout.Data.grow(1));

        TextArea textArea = TextArea.create()
                .setWidth(Constraints.relative(1.0f))
                .setLayoutData(LinearLayout.Data.grow(1))
                .setText("This is a multi-line text area.\n\nTest Undo/Redo (Ctrl+Z, Ctrl+Y)\nTest selection and clipboard (Ctrl+A, C, X, V)");

        TextField textField = TextField.create()
                .setWidth(Constraints.relative(1.0f))
                .setText("This is a single-line text field.");

        contentPanel.addChildren(textArea, textField);
        window.addChildren(titlePanel, contentPanel);
    }
}