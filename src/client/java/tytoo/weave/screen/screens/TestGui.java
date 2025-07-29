package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.animation.Easing;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.Styling;

import java.awt.*;

public class TestGui extends WeaveScreen {

    private final State<Boolean> isExpanded = new State<>(false);

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
                .addChildren(
                        TextComponent.of("Weave UI Showcase")
                                .setStyle(Styling.create().bold(true))
                                .setScale(1.5f)
                );
        header.getStyle().setColor(new Color(40, 40, 40, 200));

        final float separatorY = 30 + 5;
        Separator separator = Separator.horizontal()
                .setX(Constraints.pixels(0))
                .setY(Constraints.pixels(separatorY));

        final float contentY = separatorY + 1 + 5;
        Panel contentPanel = Panel.create()
                .setX(Constraints.pixels(0))
                .setY(Constraints.pixels(contentY))
                .setWidth(Constraints.relative(1.0f))
                .setHeight((c, parentHeight) -> parentHeight - contentY)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, 20))
                .addEffect(Effects.outline(Color.WHITE, 1));

        Panel animatedPanel = Panel.create()
                .setWidth(Constraints.pixels(150))
                .setHeight(Constraints.pixels(100));
        animatedPanel.getStyle().setColor(new Color(50, 50, 200));

        isExpanded.addListener(expanded -> {
            if (expanded) {
                animatedPanel.animate()
                        .duration(500)
                        .easing(Easing.EASE_OUT_BACK)
                        .width(400f);
                animatedPanel.animate()
                        .duration(500)
                        .color(new Color(200, 50, 50));
            } else {
                animatedPanel.animate()
                        .duration(500)
                        .easing(Easing.EASE_OUT_BACK)
                        .width(150f);
                animatedPanel.animate()
                        .duration(500)
                        .color(new Color(50, 50, 200));
            }
        });

        Button animateButton = Button.of("Animate!")
                .onClick(button -> isExpanded.set(!isExpanded.get()));

        contentPanel.addChildren(animateButton, animatedPanel);

        getWindow().addChildren(header, separator, contentPanel);
    }
}