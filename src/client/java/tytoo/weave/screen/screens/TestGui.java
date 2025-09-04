package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.BaseTextInput;
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
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 10));

        TextField textField = TextField.create()
                .setPlaceholder("Enter exactly 10 characters")
                .setValidator(s -> s.length() == 10);

        State<Text> validationTextState = State.computed(() -> {
            BaseTextInput.ValidationState state = textField.getValidationState();
            return switch (state) {
                case NEUTRAL -> Text.literal("Enter 10 chars.").styled(s -> s.withColor(0xAAAAAA));
                case VALID -> Text.literal("Valid!").styled(s -> s.withColor(0x55FF55));
                case INVALID -> Text.literal("Invalid! Must be 10 chars.").styled(s -> s.withColor(0xFF5555));
            };
        });

        SimpleTextComponent validationLabel = SimpleTextComponent.of(Text.empty());
        validationTextState.bind(validationLabel::setText);

        testPanel.addChildren(textField, validationLabel);

        window.addChildren(titlePanel, testPanel);
    }
}