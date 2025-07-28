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
import tytoo.weave.style.Styling;

import java.awt.*;


public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Showcase GUI"));

        getWindow()
                .setWidth(Constraints.relative(0.9f))
                .setHeight(Constraints.relative(0.9f))
                .setColor(new Color(20, 20, 20, 180))
                .setPadding(10)
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.START,
                        5
                ));

        Panel header = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30))
                .setColor(new Color(40, 40, 40, 200))
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.CENTER)
                )
                .addChildren(
                        TextComponent.of("Weave UI Showcase")
                                .setStyle(Styling.create().bold(true))
                                .setScale(1.5f)
                );

        Panel content = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f, -55))
                .setColor(new Color(30, 30, 30, 150))
                .setPadding(10)
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.START,
                        10)
                );

        Panel gridPanel = Panel.create()
                .setWidth(Constraints.relative(0.5f))
                .setHeight(Constraints.relative(0.5f))
                .setLayout(GridLayout.of(2, 10, 10))
                .addChildren(
                        Button.of("1").setLayoutData(GridLayout.GridData.span(2, 2)),
                        Button.of("2"), Button.of("3"),
                        Button.of("4"), Button.of("5")
                );

        content.addChildren(gridPanel, Separator.horizontal());


        getWindow().addChildren(header, content);
    }
}