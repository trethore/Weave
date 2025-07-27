package tytoo.weave.screen.screens;


import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tytoo.weave.component.components.display.Image;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.display.WrappedTextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.ImageButton;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.Styling;

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


        ImageButton imageButton = ImageButton.of(Identifier.of("minecraft", "textures/item/diamond.png"), "Some text!")
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(5))
                .setImageSize(16, 16)
                .setHeight(Constraints.pixels(20))
                .onClick(b -> System.out.println("Image button clicked!"))
                .setParent(getWindow());

        Button.of("Click me!")
                .setX(Constraints.sibling(5))
                .setY(c -> imageButton.getTop())
                .setHeight(Constraints.pixels(20))
                .setNormalColor(Color.BLACK)
                .setHoveredColor(Color.GRAY)
                .onClick(b -> System.out.println("Button clicked!"))
                .setParent(getWindow());

        getWindow().addChild(Separator.horizontal()
                .setColor(Color.PINK)
                .setY(Constraints.sibling(10))
                .setWidth(Constraints.relative(1f, -20))
                .setX(Constraints.pixels(10))
        );

        getWindow().addChild(Image.of(Identifier.of("minecraft", "textures/item/gold_ingot.png")).setLabel("An item with text!", Styling.create().bold(true))
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(5))
                .setWidth(Constraints.pixels(40))
                .setHeight(Constraints.aspect(16 / 9f))
                .setLabelScale(0.8f)
        );

        Panel focusTestPanel = Panel.create()
                .setX(Constraints.center())
                .setY(Constraints.sibling(10))
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(50))
                .setNormalColor(Color.MAGENTA)
                .setFocusedColor(Color.YELLOW)
                .setFocusable(true)
                .setParent(getWindow());

        focusTestPanel.addChild(TextComponent.of("Focusable Panel")
                .setX(Constraints.center())
                .setY(Constraints.center()));

        TextComponent.of("This is a big text!")
                .setScale(2.0f)
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(10))
                .setParent(getWindow());

        TextComponent.of("This text has ")
                .append("multiple", Styling.create().color(Color.RED).bold(true))
                .append(" colors and a ")
                .append(
                        "hover style",
                        Styling.create().color(Color.CYAN).underline(true),
                        Styling.create().obfuscated(true)
                )
                .setHoverStyle(Styling.reset().color(Color.YELLOW).shadow(true).shadowColor(Color.ORANGE))
                .setX(Constraints.pixels(10))
                .setY(Constraints.sibling(10))
                .onMouseClick(listener -> System.out.println("Clicked!"))
                .setParent(getWindow());

        Panel layoutPanel = Panel.create()
                .setX(Constraints.center())
                .setY(Constraints.sibling(10))
                .setWidth(Constraints.relative(0.9f))
                .setHeight(Constraints.pixels(30))
                .setColor(new Color(50, 50, 50))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, 5))
                .setParent(getWindow());

        layoutPanel.addChildren(Button.of("Left"), Button.of("Middle"), Button.of("Right"));
    }
}