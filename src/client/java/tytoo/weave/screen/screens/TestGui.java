package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.Slider;
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

        // Integer Slider
        State<Integer> intValue = new State<>(50);
        SimpleTextComponent intLabel = SimpleTextComponent.of("Integer Slider: 50");
        intValue.bind(v -> intLabel.setText("Integer Slider: " + v));

        Slider<Integer> intSlider = Slider.integerSlider(Slider.Orientation.HORIZONTAL, 0, 100, 50, 5);
        intSlider.setWidth(Constraints.relative(0.8f)).bindValue(intValue);

        // Double Slider
        State<Double> doubleValue = new State<>(0.0);
        SimpleTextComponent doubleLabel = SimpleTextComponent.of("Double Slider: 0.00");
        doubleValue.bind(v -> doubleLabel.setText("Double Slider: " + String.format("%.2f", v)));

        Slider<Double> doubleSlider = Slider.doubleSlider(Slider.Orientation.VERTICAL, -10.0, 10.0, 0.0, 0.1);
        doubleSlider.setHeight(Constraints.pixels(100)).bindValue(doubleValue);

        // Float Slider
        State<Float> floatValue = new State<>(0.5f);
        SimpleTextComponent floatLabel = SimpleTextComponent.of("Float Slider: 0.50");
        floatValue.bind(v -> floatLabel.setText("Float Slider: " + String.format("%.2f", v)));

        Slider<Float> floatSlider = Slider.floatSlider(Slider.Orientation.HORIZONTAL, 0f, 1f, 0.5f);
        floatSlider.setWidth(Constraints.relative(0.8f)).bindValue(floatValue);

        testPanel.addChildren(
                intLabel,
                intSlider,
                doubleLabel,
                doubleSlider,
                floatLabel,
                floatSlider
        );

        window.addChildren(titlePanel, testPanel);
    }
}