package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;
import java.util.Random;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Test GUI"));

        Panel panel = Panel.create()
                .setX(Constraints.center())
                .setY(Constraints.center())
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(100))
                .setColor(Color.GREEN)
                .setParent(getWindow());

        panel.onMouseClick(() -> panel.setColor(new Color(new Random().nextInt(0xFFFFFF))));
    }
}