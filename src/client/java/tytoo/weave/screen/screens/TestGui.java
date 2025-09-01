package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.component.components.interactive.Slider;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;

public class TestGui extends WeaveScreen {


    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5));
        window.setPadding(10);

        Panel titlePanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .addStyleClass("test-gui-title")
                .setScale(1.5f);
        titleText.setX(Constraints.center()).setY(Constraints.center());

        titlePanel.addChildren(titleText);

        Panel testPanel = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 5));

        TextField textField = TextField.create()
                .setPlaceholder("My Size is default!");

        Button button = Button.of("I should also have a default size!");

        State<Integer> sliderValue = new State<>(50);
        SimpleTextComponent sliderLabel = SimpleTextComponent.of("Value: 50");
        sliderValue.bind(v -> sliderLabel.setText("Value: " + v));

        Slider<Integer> slider = Slider.integerSlider(Slider.Orientation.HORIZONTAL, 0, 100, 50);
        slider.bindValue(sliderValue);

        State<String> comboBoxValue = new State<>(null);
        SimpleTextComponent comboBoxLabel = SimpleTextComponent.of("Selected: null");
        comboBoxValue.bind(v -> comboBoxLabel.setText("Selected: " + v));

        ComboBox<String> comboBox = ComboBox.create(comboBoxValue)
                .setPlaceholder("-- Please choose an option --")
                .addOption("Option 1", "option_1")
                .addOption("Another Option", "option_2")
                .addOption("The Third Choice", "option_3")
                .addOption("A much, much longer option to test text clipping", "long_option");

        testPanel.addChildren(textField, button, slider, sliderLabel,comboBox, comboBoxLabel);

        window.addChildren(titlePanel, testPanel);
    }
}