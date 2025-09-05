# Recipes

What you’ll find here
- Copy‑paste friendly snippets that combine multiple features (components, layout, animation, styling). Use these as a starting point and tweak to match your theme and data flow.

Quickstart screen with multiple components

```
import net.minecraft.text.Text;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.layout.Separator;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.ProgressBar;
import tytoo.weave.component.components.interactive.*;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.layout.GridLayout;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.state.State;
import tytoo.weave.animation.Easings;

public final class DemoScreen extends WeaveScreen {
    public DemoScreen() {
        super(Text.literal("Demo"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 10));
        window.setPadding(12);

        SimpleTextComponent title = SimpleTextComponent.of("Weave Demo").setScale(1.6f).setX(Constraints.center());

        Panel grid = Panel.create()
            .setLayout(GridLayout.of(2, 8))
            .setWidth(Constraints.relative(1.0f))
            .setHeight(Constraints.relative(1.0f));

        TextField tf = TextField.create().setPlaceholder("Username");
        TextArea ta = TextArea.create().setPlaceholder("About you")
            .setHeight(Constraints.pixels(100));

        State<Boolean> enabled = new State<>(true);
        CheckBox cb = CheckBox.of("Enable").bindChecked(enabled);

        State<String> flavor = new State<>("vanilla");
        RadioButtonGroup<String> group = RadioButtonGroup.create(flavor)
            .addChildren(RadioButton.of("vanilla", "Vanilla"), RadioButton.of("choco", "Chocolate"));

        State<String> choice = new State<>("one");
        ComboBox<String> combo = ComboBox.create(choice)
            .setPlaceholder("Choose…")
            .addOption("One", "one").addOption("Two", "two").addOption("Three", "three");

        ProgressBar bar = ProgressBar.create().setValue(0.25f);
        bar.setLayoutData(GridLayout.GridData.span(2, 1));

        Button submit = Button.of("Submit")
            .onMouseClick(e -> submit.animate().duration(180).easing(Easings.EASE_OUT_BACK).scale(1.08f).then(() -> submit.animate().duration(120).scale(1.0f)));

        grid.addChildren(tf, cb, ta, group, combo, submit, new Separator("Progress"), bar);

        window.addChildren(title, grid);
        window.setOpacity(0f);
        window.animate().duration(200).opacity(1f);
    }
}
```

Local styles: border + padding

```
import tytoo.weave.style.*;
import tytoo.weave.style.selector.StyleSelector;

window.addLocalStyleRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.Component.class, null, Set.of("card"), null),
    Map.ofEntries(
        Map.entry(LayoutStyleProperties.PADDING, new EdgeInsets(8)),
        Map.entry(LayoutStyleProperties.BORDER_WIDTH, 1.0f),
        Map.entry(LayoutStyleProperties.BORDER_RADIUS, 6.0f),
        Map.entry(LayoutStyleProperties.BORDER_COLOR, new java.awt.Color(100,100,100))
    )
));

Panel card = Panel.create().addStyleClass("card");
window.addChildren(card);
```

Theme variable override at runtime

```
import tytoo.weave.theme.ThemeManager;

ThemeManager.setGlobalVar("weave.color.primary", new java.awt.Color(255, 80, 80));
```
---

**Next Step:** [Overview](https://github.com/trethore/Weave/blob/main/docs/README.md)
