package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.animation.Easing;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.WrappedTextComponent;
import tytoo.weave.component.components.interactive.CheckBox;
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
                .setHeight(Constraints.childBased(10))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .setStyle(Styling.create()
                        .color(Color.BLACK)
                        .shadow(true)
                        .colorWave(new ColorWave(ColorWave.createRainbow(36), 2f)))
                .setScale(1.5f);

        titlePanel.addChildren(titleText);

        Panel contentPanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setPadding(10)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, 10))
                .setLayoutData(LinearLayout.Data.grow(1));

        final State<Boolean> isAnimated = new State<>(false);

        final WrappedTextComponent animatedText = WrappedTextComponent.of(
                        "This is some text that will be animated when you click the checkbox below. " +
                                "It demonstrates how state changes can trigger animations in the UI."
                )
                .setWidth(Constraints.relative(1.0f))
                .setStyle(Styling.create().color(Color.WHITE));

        CheckBox animationToggle = CheckBox.of("Toggle Animation").bindChecked(isAnimated);

        isAnimated.addListener(checked -> {
            if (checked) {
                animatedText.animate()
                        .duration(500)
                        .easing(Easing.EASE_OUT_BACK)
                        .scale(1.1f)
                        .rotation(45f)
                        .color(new Color(120, 220, 120));
            } else {
                animatedText.animate()
                        .duration(500)
                        .easing(Easing.EASE_OUT_SINE)
                        .scale(1.0f)
                        .rotation(0f)
                        .color(Color.WHITE);
            }
        });

        contentPanel.setFocusable(true);

        contentPanel.addChildren(animatedText, animationToggle);
        window.addChildren(titlePanel, contentPanel);
    }
}