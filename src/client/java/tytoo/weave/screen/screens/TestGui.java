package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.component.components.interactive.RadioButton;
import tytoo.weave.component.components.interactive.RadioButtonGroup;
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
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        State<String> selectedRadioState = new State<>("Option 1");
        RadioButtonGroup<String> radioGroup = RadioButtonGroup.create(selectedRadioState);
        radioGroup.addChildren(
                RadioButton.of("Option 1", "Option 1"),
                RadioButton.of("Option 2", "Option 2"),
                RadioButton.of("Option 3", "Option 3")
        );

        State<String> comboValue = new State<>(null);
        ComboBox<String> comboBox = ComboBox.create(comboValue)
                .setPlaceholder("Select an option")
                .setIncludePlaceholderOption(true)
                .addOption("Option A", "A")
                .addOption("Option B", "B")
                .addOption("Option C", "C");

        testPanel.addChildren(radioGroup, comboBox);

        window.addChildren(titlePanel, testPanel);
    }
}
