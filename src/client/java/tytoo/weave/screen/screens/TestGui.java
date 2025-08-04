package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.WeaveClient;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
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
                .setHeight(Constraints.pixels(30));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .setStyle(Styling.create()
                        .color(Color.WHITE)
                        .shadow(true)
                        .colorWave(new ColorWave(ColorWave.createRainbow(36), 2f)))
                .setScale(1.5f);
        titleText.setX(Constraints.center()).setY(Constraints.center());

        titlePanel.addChildren(titleText);

        ScrollPanel scrollPanel = new ScrollPanel()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setGap(4);

        for (int i = 1; i <= 15; i++) {
            final int buttonNum = i;
            Button button = Button.of("Button " + buttonNum)
                    .setWidth(Constraints.relative(1.0f))
                    .onClick(b -> WeaveClient.LOGGER.info("Clicked button {}!", buttonNum));
            scrollPanel.addChild(button);
        }

        window.addChildren(titlePanel, scrollPanel);
    }
}