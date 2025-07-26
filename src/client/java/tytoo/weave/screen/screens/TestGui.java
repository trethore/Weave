package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.Container;
import tytoo.weave.component.components.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Test GUI"));

        Panel.create()
                .setX(Constraints.pixels(10))
                .setY(Constraints.pixels(10))
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(80))
                .setColor(Color.BLUE)
                .setParent(getWindow());

        Container.of(
            Panel.create()
                .setX(Constraints.pixels(10))
                .setY(Constraints.pixels(10))
                .setWidth(Constraints.pixels(20))
                .setHeight(Constraints.pixels(20))
                .setColor(Color.RED),
            Panel.create()
                .setX(Constraints.pixels(40))
                .setY(Constraints.pixels(10))
                .setWidth(Constraints.pixels(20))
                .setHeight(Constraints.pixels(20))
                .setColor(Color.RED)
        ).setX(Constraints.pixels(120))
                .setY(Constraints.pixels(10))
                .setParent(getWindow());
    }
}