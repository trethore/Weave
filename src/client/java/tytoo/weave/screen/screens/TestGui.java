package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
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
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 5));

        ScrollPanel scrollPanel = new ScrollPanel()
                .setVerticalScrollbar(true);
        scrollPanel.setLayoutData(LinearLayout.Data.grow(1));

        for (int i = 1; i <= 40; i++) {
            Button b = Button.of("Button " + i);
            b.setWidth(Constraints.relative(1.0f));
            scrollPanel.addChild(b);
        }

        testPanel.addChildren(scrollPanel);

        window.addChildren(titlePanel, testPanel);
    }
}
