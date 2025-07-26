package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.display.TextImage;
import tytoo.weave.component.components.display.WrappedTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.ImageButton;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Test GUI"));

        getWindow().addChild(
                WrappedTextComponent.of("This is a long piece of text that should wrap nicely within the bounds of this component. Also, this is a test GUI for the Weave UI library.")
                        .setX(Constraints.center())
                        .setY(Constraints.pixels(10))
                        .setWidth(Constraints.relative(0.9f))
        );

        Button button = Button.of("Click me!")
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(10))
                .onClick(b -> System.out.println("Button clicked!"))
                .setParent(getWindow());

        ImageButton.of(Identifier.of("minecraft", "textures/item/diamond.png"))
                .setX(Constraints.sibling(5))
                .setY(c -> button.getTop())
                .setWidth(Constraints.pixels(20))
                .setHeight(Constraints.aspect(1.0f))
                .onClick(b -> System.out.println("Image button clicked!"))
                .setParent(getWindow());

        getWindow().addChild(Separator.horizontal()
                .setColor(Color.GRAY)
                .setY(Constraints.sibling(10))
                .setWidth(Constraints.relative(1f, -20))
                .setX(Constraints.pixels(10))
        );

        getWindow().addChild(TextImage.of(Identifier.of("minecraft", "textures/item/gold_ingot.png"), "An item with text!")
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(10))
        );

        Panel focusTestPanel = Panel.create()
                .setX(Constraints.center())
                .setY(Constraints.sibling(10))
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(50))
                .setColor(Color.MAGENTA)
                .setFocusable(true)
                .setParent(getWindow());

        focusTestPanel.onFocusGained(e -> focusTestPanel.setColor(Color.YELLOW));
        focusTestPanel.onFocusLost(e -> focusTestPanel.setColor(Color.MAGENTA));

        focusTestPanel.addChild(TextComponent.of("Focusable Panel")
                .setX(Constraints.center())
                .setY(Constraints.center()));

        TextComponent.of("This is a big text!")
                .setScale(2.0f)
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(10))
                .setParent(getWindow());

        TextComponent.of("This text has ")
                .append(Text.literal("multiple").formatted(Formatting.RED, Formatting.BOLD))
                .append(Text.literal(" colors and a "))
                .append(Text.literal("hover color").formatted(Formatting.AQUA))
                .setHoverColor(Color.YELLOW)
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(10))
                .setParent(getWindow());
    }
}