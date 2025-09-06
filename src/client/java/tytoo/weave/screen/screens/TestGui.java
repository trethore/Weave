package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.TextArea;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.GradientOutlineEffect;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.ObservableList;
import tytoo.weave.state.State;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.popup.Anchor;
import tytoo.weave.ui.popup.PopupOptions;
import tytoo.weave.ui.tooltip.TooltipOptions;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestGui extends WeaveScreen {
    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5));
        window.setPadding(10);

        window.addEffect(Effects.gradientOutline(List.of(Color.GRAY, Color.WHITE), 1f, false, GradientOutlineEffect.Direction.BOTTOM_LEFT_TO_TOP_RIGHT));

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

        // ComboBox demo
        State<String> comboState = new State<>(null);
        ComboBox<String> comboBox = ComboBox.create(comboState)
                .setPlaceholder("Select an option...")
                .setIncludePlaceholderOption(true)
                .addOption("Apple", "apple")
                .addOption("Banana", "banana")
                .addOption("Cherry", "cherry");

        TextField textField = TextField.create().setPlaceholder("Type here...");
        textField.setTooltip(Text.literal("This is a text field.\nIt supports typing, selection, and clipboard."),
                new TooltipOptions().setDelayMs(250).setMaxWidth(220f).setFollowMouse(true));
        TextArea textArea = TextArea.create().setPlaceholder("Multiline input...\nUse arrows, Home/End, PageUp/Down.\nPaste long text to test.");
        textArea.setHeight(Constraints.pixels(100));

        ObservableList<String> items = new ObservableList<>();
        for (int i = 1; i <= 10000; i++) {
            items.add("Item #" + i);
        }

        Panel listContainer = Panel.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f));

        ListView<String> listView = ListView.<String>create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.relative(1.0f))
                .setItems(items)
                .setGap(2f)
                .setHeightMode(ListView.HeightMode.MEASURE_ONCE)
                .setSelectionMode(ListView.SelectionMode.SINGLE)
                .setItemFactory(s -> SimpleTextComponent.of(s).setPadding(2, 4));

        listContainer.addChild(listView);

        // Modal demo button
        Button openModalBtn = Button.of("Open Modal");
        openModalBtn.onClick(btn -> {
            Panel modal = Panel.create()
                    .setPadding(10)
                    .setWidth(Constraints.pixels(260))
                    .setHeight(Constraints.pixels(140));

            Panel bg = Panel.create()
                    .setWidth(Constraints.relative(1.0f))
                    .setHeight(Constraints.relative(1.0f))
                    .addStyleClass("combo-box-dropdown");
            modal.addChild(bg);

            Panel content = Panel.create()
                    .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 6))
                    .setWidth(Constraints.relative(1.0f))
                    .setHeight(Constraints.relative(1.0f));
            content.addChild(SimpleTextComponent.of("Example Modal").setPadding(2, 2));
            Button closeBtn = Button.of("Close");

            final UIManager.PopupHandle[] handleRef = new UIManager.PopupHandle[1];
            closeBtn.onClick(b -> UIManager.closePopup(handleRef[0]));
            content.addChild(closeBtn);
            modal.addChild(content);

            PopupOptions opts = new PopupOptions().setModal(true).setCloseOnBackdropClick(true).setTrapFocus(true);
            handleRef[0] = UIManager.openPopup(modal, new Anchor(window, Anchor.Side.TOP, Anchor.Align.CENTER, 0f, 80f, 0f), opts);
        });

        testPanel.addChildren(comboBox, openModalBtn, textField, textArea, listContainer);

        window.addChildren(titlePanel, testPanel);
    }
}
