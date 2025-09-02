package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;

import java.util.Map;


public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5));
        window.setPadding(10);

        Panel titlePanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(30));

        SimpleTextComponent titleText = SimpleTextComponent.of("Weave Test UI")
                .addStyleClass("test-gui-title")
                .setScale(1.5f);
        titleText.setX(Constraints.center()).setY(Constraints.center());

        titlePanel.addChildren(titleText);

        Panel testPanel = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, 10));

        State<Float> selectedPercent = new State<>(0f);
        ComboBox<Float> percentCombo = ComboBox.create(selectedPercent)
                .setDropdownMaxHeight(60f)
                .addOption("0%", 0f)
                .addOption("25%", 25f)
                .addOption("50%", 50f)
                .addOption("75%", 75f)
                .addOption("100%", 100f);

        ProgressBar barRtl = ProgressBar.create().setMax(100f).bindValue(selectedPercent);
        barRtl.addLocalStyleRule(new StyleRule(new StyleSelector(ProgressBar.class, null, null, null),
                Map.ofEntries(Map.entry(ProgressBar.StyleProps.FILL_POLICY, ProgressBar.FillPolicy.RIGHT_TO_LEFT))));

        ProgressBar barLtr = ProgressBar.create().setMax(100f).bindValue(selectedPercent);
        barLtr.addLocalStyleRule(new StyleRule(new StyleSelector(ProgressBar.class, null, null, null),
                Map.ofEntries(Map.entry(ProgressBar.StyleProps.FILL_POLICY, ProgressBar.FillPolicy.LEFT_TO_RIGHT))));

        ProgressBar barCenter = ProgressBar.create().setMax(100f).bindValue(selectedPercent);
        barCenter.addLocalStyleRule(new StyleRule(new StyleSelector(ProgressBar.class, null, null, null),
                Map.ofEntries(Map.entry(ProgressBar.StyleProps.FILL_POLICY, ProgressBar.FillPolicy.CENTER_OUT))));

        testPanel.addChildren(percentCombo, barRtl, barLtr, barCenter);

        window.addChildren(titlePanel, testPanel);
    }
}
