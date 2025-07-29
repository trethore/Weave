package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
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

        Panel freeLayoutContainer = new Panel()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(300));

        Panel panelA = new Panel()
                .setX(Constraints.pixels(50)).setY(Constraints.pixels(20))
                .setWidth(Constraints.pixels(150)).setHeight(Constraints.pixels(150))
                .addEffect(Effects.outline(Color.WHITE, 1));
        panelA.getStyle().setColor(new Color(200, 50, 50, 200));
        panelA.addChildren(TextComponent.of("Panel A").setX(Constraints.center()).setY(Constraints.center()));

        Panel panelB = new Panel()
                .setX(Constraints.pixels(125)).setY(Constraints.pixels(95))
                .setWidth(Constraints.pixels(150)).setHeight(Constraints.pixels(150))
                .addEffect(Effects.outline(Color.WHITE, 1));
        panelB.getStyle().setColor(new Color(50, 50, 200, 200));
        panelB.addChildren(TextComponent.of("Panel B").setX(Constraints.center()).setY(Constraints.center()));

        freeLayoutContainer.addChildren(panelA, panelB);

        Button bringBToFront = Button.of("Bring Blue to Front").onClick(b -> panelB.bringToFront());
        Button sendBToBack = Button.of("Send Blue to Back").onClick(b -> panelB.sendToBack());

        panelA.onMouseClick(e -> panelA.bringToFront());
        panelB.onMouseClick(e -> panelB.bringToFront());

        Panel buttonContainer = new Panel()
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, 5))
                .setHeight(Constraints.childBased(5));
        buttonContainer.addChildren(bringBToFront, sendBToBack);

        getWindow().addChildren(header, Separator.horizontal(), buttonContainer, freeLayoutContainer);
    }
}