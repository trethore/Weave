package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.animation.Easings;
import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.GradientOutlineEffect;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.*;
import tytoo.weave.style.renderer.SolidColorRenderer;
import tytoo.weave.style.selector.StyleSelector;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DemoWeaveScreen extends WeaveScreen {
    public DemoWeaveScreen() {
        super(Text.literal("Weave Demo"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 8));
        window.setPadding(10);
        applyWindowStyles();

        Panel content = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.START, 8));

        content.addChildren(
                buildHeader(),
                buildControlsRow(),
                buildSliderRow(),
                buildSelectionRow(),
                buildProgressSection(),
                buildScrollSection()
        );

        window.addChild(content);
        playIntroAnimation();
    }

    private void applyWindowStyles() {
        Color start = new Color(255, 226, 89);
        Color end = new Color(153, 74, 255);

        window.addLocalStyleRule(new StyleRule(
                new StyleSelector(Window.class, null, null, null),
                Map.ofEntries(
                        Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(Color.decode("#222222"))),
                        Map.entry(LayoutStyleProperties.BORDER_RADIUS, 0f),
                        Map.entry(LayoutStyleProperties.BORDER_WIDTH, 1.5f),
                        Map.entry(LayoutStyleProperties.BORDER_COLOR, new Color(30, 30, 30, 160)),
                        Map.entry(CommonStyleProperties.TRANSITION_DURATION, 180L),
                        Map.entry(CommonStyleProperties.TRANSITION_EASING, Easings.EASE_OUT_QUAD)
                )
        ));

        List<Color> outline = Arrays.asList(start, end);
        window.addEffect(Effects.gradientOutline(outline, 1.5f, true, GradientOutlineEffect.Direction.BOTTOM_LEFT_TO_TOP_RIGHT));
        window.addEffect(Effects.shadow(new Color(0, 0, 0, 130), 0f, 0f, 10f, 0f));
    }

    private void playIntroAnimation() {
        window.setOpacity(0.0f).setScale(0.96f);
        window.animate().duration(420).easing(Easings.EASE_OUT_QUAD).opacity(1.0f);
        window.animate().duration(420).easing(Easings.EASE_OUT_BACK).scale(1.0f);
    }

    private Panel buildHeader() {
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
        return header;
    }

    private Panel buildControlsRow() {
        Panel row = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(28))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        Button helloBtn = Button.of("Click me");
        TextField input = TextField.create().setPlaceholder("Type here...").setWidth(Constraints.relative(0.5f));
        helloBtn.onClick(e -> helloBtn.animate().duration(200).easing(Easings.EASE_OUT_BACK).scale(1.1f).then(() -> helloBtn.animate().duration(160).scale(1.0f)));

        row.addChildren(helloBtn, input);
        return row;
    }

    private Panel buildSliderRow() {
        Panel row = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(36))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        Slider<Integer> slider = Slider.integerSlider(Slider.Orientation.HORIZONTAL, 0, 100, 50);
        slider.setWidth(Constraints.relative(0.6f));

        SimpleTextComponent sliderLabel = SimpleTextComponent.of("Value: 50");
        slider.getValueState().addListener(val -> sliderLabel.setText("Value: " + val));

        row.addChildren(slider, sliderLabel);
        return row;
    }

    private Panel buildSelectionRow() {
        Panel row = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(28))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        CheckBox cb = CheckBox.of("Enable feature");

        RadioButtonGroup<String> group = RadioButtonGroup.create(new State<>("A"));
        group.addChildren(
                RadioButton.of("A", "Option A"),
                RadioButton.of("B", "Option B")
        );

        State<String> comboState = new State<>("Apple");
        ComboBox<String> combo = ComboBox.create(comboState)
                .setPlaceholder("Pick a fruit")
                .setOptions(
                        List.of(
                                new ComboBox.Option<>("Apple", "Apple"),
                                new ComboBox.Option<>("Banana", "Banana"),
                                new ComboBox.Option<>("Cherry", "Cherry")
                        )
                )
                .setWidth(Constraints.pixels(140));

        row.addChildren(cb, group, combo);
        return row;
    }

    private Panel buildProgressSection() {
        Panel row = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(28))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        ProgressBar bar = ProgressBar.of(100f, 0f).setWidth(Constraints.relative(0.6f));
        Button play = Button.of("Animate");
        play.onClick(e -> bar.animate().duration(1200).easing(Easings.EASE_OUT_QUAD)
                .animateProperty(bar.getValueState(), 100f, tytoo.weave.animation.Interpolators.FLOAT, null, "progress-value"));

        row.addChildren(bar, play);
        return row;
    }

    private Panel buildScrollSection() {
        Panel row = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.START, 8));

        ScrollPanel scroll = new ScrollPanel()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setVerticalScrollbar(true)
                .addEffect(Effects.scissor());

        BasePanel<?> content = scroll.getContentPanel();
        content.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 4));

        for (int i = 1; i <= 20; i++) {
            content.addChild(SimpleTextComponent.of("Scrollable item " + i));
        }

        row.addChild(scroll);
        return row;
    }
}
