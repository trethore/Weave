package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Clone Test GUI"));

        Button originalButton = Button.of("Original Button")
                .setX(Constraints.center())
                .setY(Constraints.pixels(50))
                .setNormalColor(new Color(0, 0, 200))
                .setHoveredColor(new Color(50, 50, 255))
                .onClick(b -> System.out.println("Original button's click listener fired!"));

        getWindow().addChild(originalButton);

        Button clonedButton = originalButton.clone();

        clonedButton
                .setText(Text.of("Cloned Button"))
                .setY(Constraints.sibling(10))
                .setNormalColor(new Color(200, 0, 0))
                .setHoveredColor(new Color(255, 50, 50));


        getWindow().addChild(clonedButton);
    }
}