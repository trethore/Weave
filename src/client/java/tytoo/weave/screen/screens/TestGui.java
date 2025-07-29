package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.Canvas;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.Styling;

import java.awt.*;

public class TestGui extends WeaveScreen {

    public TestGui() {
        super(Text.literal("Weave Showcase GUI"));

        getWindow()
                .setWidth(Constraints.relative(0.9f))
                .setHeight(Constraints.relative(0.9f))
                .setPadding(10)
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.START,
                        5
                ));

        getWindow().getStyle().setColor(new Color(20, 20, 20, 220));

        Panel header = Panel.create()
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

        Panel canvasContainer = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(120))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, 5));

        canvasContainer.addChildren(
                TextComponent.of("Canvas for custom drawing:"),
                Canvas.create()
                        .setWidth(Constraints.pixels(100))
                        .setHeight(Constraints.pixels(100))
                        .onDraw((context, canvas) -> {
                            float x = canvas.getLeft() + canvas.getWidth() / 2;
                            float y = canvas.getTop() + canvas.getHeight() / 2;
                            float radius = (Math.min(canvas.getWidth(), canvas.getHeight()) / 2f) - 2;
                            tytoo.weave.utils.render.Render2DUtils.drawCircle(context, x, y, radius, new Color(200, 50, 50));
                        })
                        .addEffect(Effects.outline(Color.WHITE, 1))
        );
        canvasContainer.getStyle().setColor(new Color(40, 40, 40, 200));
        getWindow().addChildren(header, Separator.horizontal(), canvasContainer);
    }
}