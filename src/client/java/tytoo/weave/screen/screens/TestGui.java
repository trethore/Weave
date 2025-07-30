package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
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
                .setHeight(Constraints.relative(0.9f))
                .setPadding(10);
        getWindow().getStyle().setColor(new Color(20, 20, 20, 220));

        Panel header = Panel.create()
                .setX(Constraints.pixels(0))
                .setY(Constraints.pixels(0))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30))
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.CENTER)
                )
                .addChildren(TextComponent.of("LinearLayout Flex-Grow Showcase").setScale(1.5f));
        header.getStyle().setColor(new Color(40, 40, 40, 200));

        final float separatorY = 30 + 5;
        Separator separator = Separator.horizontal()
                .setY(Constraints.pixels(separatorY))
                .setWidth(Constraints.relative(1.0f));

        final float contentY = separatorY + 1 + 5;
        Panel dashboardPanel = Panel.create()
                .setY(Constraints.pixels(contentY))
                .setWidth(Constraints.relative(1.0f))
                .setHeight((c, parentHeight) -> parentHeight - contentY)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, 5));

        Panel leftPanel = Panel.create()
                .setWidth(Constraints.pixels(120))
                .setHeight(Constraints.relative(1.0f))
                .setPadding(5);
        leftPanel.getStyle().setColor(new Color(30, 30, 30, 200));
        leftPanel.addChildren(TextComponent.of("Fixed Width Panel"));

        Panel centerPanel = Panel.create()
                .setHeight(Constraints.relative(1.0f))
                .setPadding(5)
                .setLayoutData(LinearLayout.Data.grow(1));
        centerPanel.getStyle().setColor(new Color(40, 40, 40, 200));
        centerPanel.addChildren(TextComponent.of("Flexible Panel (Grow = 1)"));

        Panel rightPanel = Panel.create()
                .setHeight(Constraints.relative(1.0f))
                .setPadding(5)
                .setLayoutData(LinearLayout.Data.grow(2));
        rightPanel.getStyle().setColor(new Color(50, 50, 50, 200));
        rightPanel.addChildren(TextComponent.of("Flexible Panel (Grow = 2)"));

        dashboardPanel.addChildren(leftPanel, centerPanel, rightPanel);

        getWindow().addChildren(header, separator, dashboardPanel);
    }
}