package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.ListView;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.GradientOutlineEffect;
import tytoo.weave.layout.GridLayout;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.ObservableList;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.OutlineSides;
import tytoo.weave.style.selector.StyleSelector;
import tytoo.weave.ui.toast.ToastManager;
import tytoo.weave.ui.toast.ToastOptions;

import java.awt.*;
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

        final int totalButtons = 1000;
        final int columns = 5;
        ObservableList<List<Integer>> rows = new ObservableList<>();
        for (int i = 0; i < totalButtons; i += columns) {
            int end = Math.min(totalButtons, i + columns);
            java.util.List<Integer> row = new java.util.ArrayList<>();
            for (int j = i; j < end; j++) row.add(j);
            rows.add(row);
        }

        ListView<List<Integer>> gridView = ListView.<List<Integer>>create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setItems(rows)
                .setGap(4f)
                .setHeightMode(ListView.HeightMode.MEASURE_ONCE)
                .setSelectionMode(ListView.SelectionMode.SINGLE)
                .setItemFactory(row -> {
                    Panel rowPanel = Panel.create()
                            .setManagedByLayout(true)
                            .setWidth(Constraints.relative(1.0f))
                            .setLayout(GridLayout.of(columns, 6f));
                    for (Integer idx : row) {
                        int label = idx + 1;
                        Button b = Button.of("button#" + label).setWidth(Constraints.relative(1.0f));
                        b.onClick(e -> ToastManager.show("You clicked that button !", new ToastOptions().setDurationMs(2000).setPosition(ToastOptions.Position.BOTTOM_LEFT)));
                        rowPanel.addChild(b);
                    }
                    return rowPanel;
                });

        testPanel.addChildren(gridView);

        window.addChildren(titlePanel, testPanel);
    }
}
