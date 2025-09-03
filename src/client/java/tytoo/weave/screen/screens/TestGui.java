package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.interactive.BaseTextInput.ValidationState;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;


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
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 10));

        SimpleTextComponent validationLabel = SimpleTextComponent.of("");

        TextField textField = TextField.create()
                .setPlaceholder("Enter text (max 10 chars)")
                .setValidator(text -> text.length() <= 10)
                .onTextChanged(newText -> {
                    validationLabel.setText(textField.getValidationState() == ValidationState.VALID ? "Valid" : "Invalid");
                });

        validationLabel.setText(textField.getValidationState() == ValidationState.VALID ? "Valid" : "Invalid");

        testPanel.addChildren(textField, validationLabel);

        window.addChildren(titlePanel, testPanel);
    }
}
