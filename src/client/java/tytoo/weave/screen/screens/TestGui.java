package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;

public class TestGui extends WeaveScreen {

    public TestGui() {
        super(Text.literal("Weave Showcase GUI"));


        getWindow()
                .setWidth(Constraints.relative(0.9f))
                .setHeight(Constraints.relative(0.6f))
                .setPadding(10);
        getWindow().getStyle().setColor(new Color(20, 20, 20, 220));

        Panel header = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30))
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.CENTER
                ))
                .addChildren(TextComponent.of("LinearLayout Cross-Axis Alignment Showcase").setScale(1.5f));
        header.getStyle().setColor(new Color(40, 40, 40, 200));

        Panel contentPanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setLayoutData(LinearLayout.Data.grow(1))
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.START,
                        LinearLayout.CrossAxisAlignment.STRETCH,
                        5f
                ));
        contentPanel.getStyle().setColor(new Color(30, 30, 30, 200));

        contentPanel.addChildren(
                Button.of("This button is stretched"),
                Button.of("So is this one"),
                Button.of("All children of a vertical layout with CrossAxisAlignment.STRETCH will fill the available width."),
                Panel.create().setHeight(Constraints.pixels(20)).setLayoutData(LinearLayout.Data.grow(1))
        );

        getWindow().setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5f));
        getWindow().addChildren(header, Separator.horizontal(), contentPanel);
    }
}