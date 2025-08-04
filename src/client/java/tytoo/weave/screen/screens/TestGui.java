package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.CheckBox;
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
                .setHeight(Constraints.childBased(10));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .setStyle(Styling.create()
                        .color(Color.BLACK)
                        .shadow(true)
                        .colorWave(new ColorWave(ColorWave.createRainbow(36), 2f)))
                .setScale(1.5f);
        titleText.setX(Constraints.center());

        titlePanel.addChildren(titleText);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.getContentPanel().setPadding(5);

        for (int i = 1; i <= 10; i++) {
            Button button = Button.of("Button " + i)
                    .setWidth(Constraints.relative(1.0f));
            scrollPanel.getContentPanel().addChild(button);
        }

        CheckBox scrollbarToggle = CheckBox.of("Show Scrollbar");
        scrollbarToggle.setChecked(true);
        scrollbarToggle.setX(Constraints.center());

        scrollPanel.setLayoutData(LinearLayout.Data.grow(1));
        window.addChildren(titlePanel, scrollbarToggle, scrollPanel);
    }
}