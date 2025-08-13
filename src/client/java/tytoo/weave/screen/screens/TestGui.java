package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.animation.Easing;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;

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
                .addStyleClass("test-gui-title")
                .setScale(1.5f);
        titleText.setX(Constraints.center()).setY(Constraints.center());

        titlePanel.addChildren(titleText);

        Panel testPanel = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 5));

        SimpleTextComponent animatedText = SimpleTextComponent.of("Animating!");
        animatedText.setOpacity(0f);
        animatedText.setScale(0.5f);
        testPanel.addChildren(animatedText);

        animatedText.animate().duration(1000).opacity(0.1f).then(() ->
                animatedText.animate().duration(1000).color(Color.CYAN).then(() ->
                        animatedText.animate().duration(500).easing(Easing.EASE_OUT_BACK).scale(1.2f).then(() ->
                                animatedText.animate().duration(500).easing(Easing.EASE_OUT_SINE).scale(1.0f))));

        SimpleTextComponent hoverText = SimpleTextComponent.of("Hover me!");
        hoverText.onMouseEnter(e -> hoverText.animate().duration(200).color(Color.YELLOW).scale(1.1f).rotation(45.0f).opacity(0.5f));
        hoverText.onMouseLeave(e -> hoverText.animate().duration(200).color(Color.WHITE).scale(1.0f).rotation(0.0f).opacity(1));
        testPanel.addChildren(hoverText);

        window.addChildren(titlePanel, testPanel);
    }
}