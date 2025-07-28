package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.GridLayout;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.Styling;

import java.awt.*;
import java.util.Random;

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
                        10)
                );

        content.getStyle().setColor(new Color(30, 30, 30, 150));

        Panel gridPanel = Panel.create()
                .setWidth(Constraints.relative(0.75f))
                .setHeight(Constraints.relative(0.5f))
                .setLayout(GridLayout.of(2, 10, 10))
                .addChildren(
                        Button.of("1").setLayoutData(GridLayout.GridData.rowSpan(2)),
                        Button.of("2"), Button.of("3"),
                        Button.of("4"), Button.of("5")
                );

        State<Color> reactivePanelColor = new State<>(new Color(40, 80, 160));

        Panel reactivePanel1 = Panel.create()
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(30));

        Panel reactivePanel2 = Panel.create()
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(30));


        reactivePanelColor.bind(color -> reactivePanel1.getStyle().setColor(color));
        reactivePanelColor.bind(color -> reactivePanel2.getStyle().setColor(color));

        Button changeColorButton = Button.of("Change Color")
                .onClick(b -> {
                    Random rand = new Random();
                    reactivePanelColor.set(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                });

        Panel reactiveContainer = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.childBased(0))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.SPACE_AROUND, 10))
                .addChildren(changeColorButton, reactivePanel1, reactivePanel2);

        Panel animatablePanel = Panel.create()
                .setWidth(Constraints.pixels(50))
                .setHeight(Constraints.pixels(50));
        animatablePanel.getStyle().setColor(Color.CYAN);

        State<Boolean> toggled = new State<>(false);

        Button animateButton = Button.of("Animate Me!")
                .onClick(b -> {
                    toggled.set(!toggled.get());
                    if (toggled.get()) {
                        animatablePanel.animate()
                                .duration(100)
                                .color(Color.BLUE);
                    } else {
                        animatablePanel.animate()
                                .duration(500)
                                .color(Color.RED);
                    }
                });

        Panel animationContainer = Panel.create().setWidth(Constraints.relative(1.0f)).setHeight(Constraints.childBased(0)).setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.SPACE_AROUND, 10)).addChildren(animateButton, animatablePanel);

        content.addChildren(gridPanel, Separator.horizontal(), reactiveContainer, Separator.horizontal(), animationContainer);

        getWindow().addChildren(header, Separator.horizontal(), content);
    }
}