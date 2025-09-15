# Recipes

This page contains a collection of practical, copy-paste-friendly code snippets for common UI patterns and tasks. Use these as a starting point for your own components and screens.

---

## What you’ll find here

- A complete example of a multi-component settings screen.
- How to apply one-off styles using local style rules.
- A pattern for creating a modal dialog with an overlay.
- How to override theme variables at runtime for dynamic styling.

---

## Recipe: A Complex Settings Screen

This example combines multiple layouts (`LinearLayout`, `GridLayout`), various interactive components, state binding, and animations to create a feature-rich screen.

```java
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
import java.awt.Color;

public final class DemoScreen extends WeaveScreen {
    public DemoScreen() {
        super(Text.literal("Demo"));

        // Configure root window
        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 10));
        window.setPadding(12);
        window.addEffect(Effects.shadow(new Color(0,0,0,120), 0, 4, 12, 0));

        // Screen Title
        SimpleTextComponent title = SimpleTextComponent.of("Weave Demo").setScale(1.6f).setX(Constraints.center());

        // Use a GridLayout for the main form content
        Panel grid = Panel.create()
            .setLayout(GridLayout.of(2, 8)) // 2 columns, 8px gap
            .setWidth(Constraints.relative(1.0f));

        // --- Form Components with State Binding ---
        State<String> username = new State<>("");
        TextField tf = TextField.create().setPlaceholder("Username").bindText(username);

        State<Boolean> enabled = new State<>(true);
        CheckBox cb = CheckBox.of("Enable Feature").bindChecked(enabled);

        State<String> about = new State<>("");
        TextArea ta = TextArea.create().setPlaceholder("About you")
            .setHeight(Constraints.pixels(100))
            .bindText(about)
            .setLayoutData(GridLayout.GridData.span(2, 1)); // Span both columns

        State<String> flavor = new State<>("vanilla");
        RadioButtonGroup<String> group = RadioButtonGroup.create(flavor)
            .addChildren(
                RadioButton.of("vanilla", "Vanilla"),
                RadioButton.of("choco", "Chocolate")
            );

        State<String> choice = new State<>("one");
        ComboBox<String> combo = ComboBox.create(choice)
            .setPlaceholder("Choose…")
            .addOption("One", "one").addOption("Two", "two").addOption("Three", "three");

        // --- Separator and Progress Bar ---
        Separator progressSeparator = new Separator(Separator.Orientation.HORIZONTAL)
            .withLabel("Progress", Separator.LabelAlignment.CENTER)
            .setLayoutData(GridLayout.GridData.span(2, 1));

        ProgressBar bar = ProgressBar.create().setValue(0.25f);
        bar.setLayoutData(GridLayout.GridData.span(2, 1));

        // Submit Button with Animation
        Button submit = Button.of("Submit")
            .setLayoutData(GridLayout.GridData.span(2, 1)) // Span and center
            .setX(Constraints.center())
            .onMouseClick(e -> submit.animate().duration(180).easing(Easings.EASE_OUT_BACK).scale(1.08f).then(() -> submit.animate().duration(120).scale(1.0f)));

        // Add components to the grid
        grid.addChildren(tf, cb, ta, group, combo, progressSeparator, bar, submit);

        // Add title and grid to the window
        window.addChildren(title, grid);

        // Fade in the screen
        window.setOpacity(0f);
        window.animate().duration(200).opacity(1f);
    }
}
```

## Recipe: Local Styles for a "Card" Component

Sometimes you need a one-off style that isn't part of your global theme. You can use local style rules and style classes for this.

```java
import tytoo.weave.style.*;
import tytoo.weave.style.selector.StyleSelector;

// In your WeaveScreen constructor, add a local rule to the window.
// This rule will apply to any descendant of the window with the class "card".
window.addLocalStyleRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.Component.class, null, Set.of("card"), null),
    Map.ofEntries(
        Map.entry(LayoutStyleProperties.PADDING, new EdgeInsets(8)),
        Map.entry(LayoutStyleProperties.BORDER_WIDTH, 1.0f),
        Map.entry(LayoutStyleProperties.BORDER_RADIUS, 6.0f),
        Map.entry(LayoutStyleProperties.BORDER_COLOR, new java.awt.Color(100, 100, 100)),
        Map.entry(ComponentStyle.Slots.BASE_RENDERER, new SolidColorRenderer(new Color(40, 40, 40, 180)))
    )
));

// Now, any panel with this class will get the card styling.
Panel card1 = Panel.create().addStyleClass("card");
Panel card2 = Panel.create().addStyleClass("card");
window.addChildren(card1, card2);
```

## Recipe: Runtime Theme Variable Override

You can dynamically change theme variables to update the entire UI's appearance without a reload. This is great for user-configurable accent colors.

```java
import tytoo.weave.theme.ThemeManager;
import java.awt.Color;

// Assuming your theme uses a variable named "weave.color.primary"
// for its main accent color.
Button changeColorButton = Button.of("Change Accent Color")
    .onMouseClick(e -> {
        ThemeManager.setGlobalVar("weave.color.primary", new java.awt.Color(255, 80, 80));
    });
```

---

**Next Step → [Developer Guide Overview](README.md)**
