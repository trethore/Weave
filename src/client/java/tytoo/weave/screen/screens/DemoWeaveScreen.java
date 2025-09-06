package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.Slider;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;

import java.util.Map;
import java.util.Set;

public class DemoWeaveScreen extends WeaveScreen {
    public DemoWeaveScreen() {
        super(Text.literal("Weave Demo"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 8));
        window.setPadding(10);

        Panel header = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30));

        SimpleTextComponent title = SimpleTextComponent.of("Weave Demo")
                .addStyleClass("weave-demo-title")
                .setScale(1.4f)
                .setX(Constraints.center())
                .setY(Constraints.center());

        title.addLocalStyleRule(new StyleRule(
                new StyleSelector(TextComponent.class, null, Set.of("weave-demo-title"), null),
                Map.ofEntries(
                        Map.entry(TextComponent.StyleProps.COLOR_WAVE, new ColorWave(ColorWave.createRainbow(36), 2.5f))
                )
        ));

        header.addChild(title);

        Panel content = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.START, 8));

        Panel row1 = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(28))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        Button helloBtn = Button.of("Click me");
        TextField input = TextField.create().setPlaceholder("Type here...").setWidth(Constraints.relative(0.5f));

        row1.addChildren(helloBtn, input);

        Panel row2 = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(36))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        Slider<Integer> slider = Slider.integerSlider(Slider.Orientation.HORIZONTAL, 0, 100, 50);
        slider.setWidth(Constraints.relative(0.6f));

        SimpleTextComponent sliderLabel = SimpleTextComponent.of("Value: 50");
        slider.getValueState().addListener(val -> sliderLabel.setText("Value: " + val));

        row2.addChildren(slider, sliderLabel);

        content.addChildren(row1, row2);

        window.addChildren(header, content);
    }
}
