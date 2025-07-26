package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.Panel;
import tytoo.weave.component.components.TextComponent;
import tytoo.weave.component.components.WrappedTextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.screen.WeaveScreen;

import java.awt.*;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Weave Test GUI"));

        getWindow().setColor(new Color(40, 40, 40, 150));

        final Color normalColor = Color.GREEN;
        final Color hoverColor = Color.YELLOW;
        final Color focusColor = Color.CYAN;

        Panel panel = Panel.create();

        panel.setX(Constraints.center())
                .setY(Constraints.center())
                .setWidth(Constraints.pixels(100))
                .setHeight(Constraints.pixels(100))
                .setColor(normalColor)
                .setFocusable(true)
                .setParent(getWindow());

        panel.onMouseClick(event -> System.out.println("Panel clicked with button " + event.getButton())).onMouseEnter(event -> {
            System.out.println("Mouse entered panel!");
            if (!panel.isFocused()) panel.setColor(hoverColor);
        }).onMouseLeave(event -> {
            System.out.println("Mouse left panel!");
            if (!panel.isFocused()) panel.setColor(normalColor);
        }).onFocusGained(event ->
                panel.setColor(focusColor)
        ).onFocusLost(event -> {
            if (panel.isHovered()) {
                panel.setColor(hoverColor);
            } else {
                panel.setColor(normalColor);
            }
        });

        panel.addChild(
                TextComponent.of("Hello, Weave!")
                        .setColor(Color.WHITE)
                        .setShadow(true)
                        .setX(Constraints.center())
                        .setY(Constraints.center())
        );

        getWindow().addChild(
                WrappedTextComponent.of("This is a long piece of text that should wrap nicely within the bounds of this component.")
                        .setX(Constraints.pixels(10))
                        .setY(Constraints.sibling(10))
                        .setWidth(Constraints.relative(1.0f, -20))
        );
    }
}