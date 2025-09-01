package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.component.components.interactive.Slider;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.ComponentStyle;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.renderer.SolidColorRenderer;
import tytoo.weave.style.selector.StyleSelector;

import java.awt.*;
import java.util.Map;
import java.util.Set;

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

        Separator coolSeparator = Separator.horizontal()
                .setLabel("A Separator")
                .setLabelAlignment(Separator.LabelAlignment.LEFT_WITH_LINE);

        coolSeparator.addLocalStyleRule(new StyleRule(
                new StyleSelector(TextComponent.class, null, Set.of("separator-label"), null),
                Map.of(TextComponent.StyleProps.TEXT_COLOR, new Color(220, 190, 90),
                        TextComponent.StyleProps.TEXT_SCALE, 0.8f)
        ));
        coolSeparator.addLocalStyleRule(new StyleRule(
                new StyleSelector(Panel.class, null, Set.of("separator-line-left"), null),
                Map.of(ComponentStyle.StyleProps.BASE_RENDERER, new SolidColorRenderer(new Color(160, 70, 70)))
        ));
        coolSeparator.addLocalStyleRule(new StyleRule(
                new StyleSelector(Panel.class, null, Set.of("separator-line-right"), null),
                Map.of(ComponentStyle.StyleProps.BASE_RENDERER, new SolidColorRenderer(new Color(70, 160, 70)))
        ));

        TextField textField = TextField.create()
                .setPlaceholder("My Size is default!");

        Button button = Button.of("I should also have a default size!");
        button.setEnabled(false);

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
                .setDropdownMaxHeight(60f)
                .setIncludePlaceholderOption(true)
                .addOption("Option 1", "option_1")
                .addOption("Another Option", "option_2")
                .addOption("The Third Choice", "option_3")
                .addOption("A much, much longer option to test text clipping", "long_option");

        for (int i = 4; i <= 30; i++) {
            comboBox.addOption("Option " + i, "option_" + i);
        }

        testPanel.addChildren(coolSeparator, textField, button, slider, sliderLabel, comboBoxLabel, comboBox);

        window.addChildren(titlePanel, testPanel);
    }
}
