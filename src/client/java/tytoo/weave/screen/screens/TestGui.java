package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Test GUI"));

        Panel container = Panel.create()
                .setX(Constraints.center())
                .setY(Constraints.center())
                .setWidth(Constraints.childBased(5))
                .setHeight(Constraints.childBased(5))
                .setColor(new Color(0, 0, 0, 100))
                .setParent(getWindow());

        for (int i = 0; i < 3; i++) {
            Panel.create()
                .setX(Constraints.pixels(10))
                .setY(i == 0 ? Constraints.pixels(10) : Constraints.sibling(5))
                .setWidth(Constraints.pixels(80)).setHeight(Constraints.pixels(20))
                .setColor(Color.getHSBColor(i / 3f, 1, 1))
                .setParent(container);
        }
    }
}