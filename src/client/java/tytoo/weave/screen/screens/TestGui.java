package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.CheckBox;
import tytoo.weave.component.components.interactive.RadioButton;
import tytoo.weave.component.components.interactive.RadioButtonGroup;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
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
                .setHeight(Constraints.pixels(30));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .setStyle(Styling.create()
                        .color(Color.WHITE)
                        .shadow(true)
                        .colorWave(new ColorWave(ColorWave.createRainbow(36), 2f)))
                .setScale(1.5f);
        titleText.setX(Constraints.center()).setY(Constraints.center());

        titlePanel.addChildren(titleText);

        Panel testPanel = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 5));

        CheckBox.of("Test").setParent(testPanel).setMargin(10, 0);

        SimpleTextComponent radioLabel = SimpleTextComponent.of("Selected Option: Java");
        radioLabel.setMargin(10, 0, 0, 0);

        State<String> selectedLanguage = new State<>("Java");
        selectedLanguage.bind(v -> radioLabel.setText("Selected Option: " + v));

        RadioButtonGroup<String> languageGroup = RadioButtonGroup.create(selectedLanguage)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5f));

        languageGroup.addChildren(
                RadioButton.of("Java", "Java"),
                RadioButton.of("Kotlin", "Kotlin"),
                RadioButton.of("Rust", "Rust"),
                RadioButton.of("C#", "C# (Disabled)").setEnabled(false),
                RadioButton.of("JavaScript", "JavaScript")
        );

        testPanel.addChildren(
                languageGroup,
                radioLabel
        );

        window.addChildren(titlePanel, testPanel);
    }
}