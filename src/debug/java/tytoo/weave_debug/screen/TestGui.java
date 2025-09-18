package tytoo.weave_debug.screen;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.ScrollPanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.GradientOutlineEffect;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5));
        window.setPadding(10);

        window.addEffect(Effects.gradientOutline(new ColorWave(List.of(Color.GRAY, Color.WHITE), 0f), 1f, false, GradientOutlineEffect.Direction.BOTTOM_LEFT_TO_TOP_RIGHT, new OutlineSides(true, true, false, true)));
        window.addEffect(Effects.boxShadow(Color.decode("#222222"), 4f, 4f, 0f, 0));

        Panel titlePanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .addStyleClass("test-gui-title")
                .setScale(1.5f);

        titleText.addLocalStyleRule(new StyleRule(
                new StyleSelector(TextComponent.class, null, Set.of("test-gui-title"), null),
                Map.ofEntries(
                        Map.entry(TextComponent.StyleProps.COLOR_WAVE, new ColorWave(ColorWave.createRainbow(36), 2f))
                )
        ));

        titleText.setX(Constraints.center()).setY(Constraints.center());

        titlePanel.addChildren(titleText);

        Panel testPanel = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.START, 10));

        Panel controls = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, 8));

        ScrollPanel scrollPanel = new ScrollPanel()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setGap(6f)
                .setVerticalScrollbar(true);

        BasePanel<?> contentPanel = scrollPanel.getContentPanel();
        contentPanel.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 6f));

        List<Integer> entries = new ArrayList<>();

        Runnable renderEntries = () -> {
            contentPanel.removeAllChildren();
            for (Integer value : entries) {
                Button entryButton = Button.of("Scrollable entry #" + value)
                        .setWidth(Constraints.relative(1.0f));
                contentPanel.addChild(entryButton);
            }
        };

        Runnable populateEntries = () -> {
            entries.clear();
            for (int i = 1; i <= 40; i++) {
                entries.add(i);
            }
            renderEntries.run();
        };

        Button addEntriesButton = Button.of("Add 20 entries")
                .onClick(event -> {
                    int start = entries.isEmpty() ? 1 : entries.getLast() + 1;
                    for (int i = 0; i < 20; i++) {
                        entries.add(start + i);
                    }
                    renderEntries.run();
                });

        Button shrinkEntriesButton = Button.of("Shrink to 8 entries")
                .onClick(event -> {
                    if (entries.size() <= 8) return;
                    entries.subList(8, entries.size()).clear();
                    renderEntries.run();
                });

        Button resetEntriesButton = Button.of("Reset content")
                .onClick(event -> {
                    populateEntries.run();
                    scrollPanel.setScrollY(0f);
                });

        controls.addChildren(addEntriesButton, shrinkEntriesButton, resetEntriesButton);

        populateEntries.run();

        testPanel.addChildren(controls, scrollPanel);

        window.addChildren(titlePanel, testPanel);
    }
}
