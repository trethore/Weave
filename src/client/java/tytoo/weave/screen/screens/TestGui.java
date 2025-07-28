package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.Styling;

import java.awt.*;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Showcase GUI"));

        getWindow()
                .setWidth(Constraints.relative(0.9f))
                .setHeight(Constraints.relative(0.9f))
                .setPadding(10)
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.START,
                        5
                ));

        getWindow().getStyle().setColor(new Color(20, 20, 20, 220));

        Panel header = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30))
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.CENTER)
                )
                .addChildren(
                        TextComponent.of("Weave UI Showcase")
                                .setStyle(Styling.create().bold(true))
                                .setScale(1.5f)
                );

        header.getStyle().setColor(new Color(40, 40, 40, 200));

        Panel content = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f, -55))
                .setPadding(10)
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.START,
                        5)
                );

        content.getStyle().setColor(new Color(30, 30, 30, 150));
        content.addEffect(Effects.outline(new Color(80, 255, 80), 2));


        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidth(Constraints.relative(1.0f)).setHeight(Constraints.relative(1.0f)).setPadding(2).setGap(5);
        scrollPanel.getStyle().setColor(new Color(0, 0, 0, 50));
        for (int i = 1; i <= 20; i++) {
            int finalI = i;
            scrollPanel.addChildren(Button.of("Scrollable Button " + i)
                    .setWidth(Constraints.relative(1.0f))
                    .onClick(action -> System.out.println("Clicked " + finalI))
            );
        }

        content.addChildren(scrollPanel);

        getWindow().addChildren(header, Separator.horizontal(), content);
    }
}