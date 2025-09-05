# Components & Layout

Weave UIs are composed from `Component` instances arranged in a tree under the root `window`. You add children, apply constraints, set layouts, and attach events.

What this page covers
- The component taxonomy (layout, display, interactive), how managed vs. overlay children behave, and how to think about constraints and layouts together. If you’re new to Weave, skim the examples to see the common patterns before diving deeper into styling.

Common components
- Layout: `Window`, `Panel`, `ScrollPanel`, `Container`, `Separator`
- Display: `Text`, `WrappedText`, `Image`, `ProgressBar`
- Interactive: `Button`, `CheckBox`, `RadioButton`, `RadioButtonGroup`, `ComboBox`, `Slider`, `TextField`, `TextArea`, `ImageButton`

Adding components

```
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.constraint.constraints.Constraints;

window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 10));
window.setPadding(10);

Panel content = Panel.create()
    .setWidth(Constraints.relative(1.0f))
    .setHeight(Constraints.relative(1.0f))
    .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.START, 8));

Button action = Button.of("Click me")
    .onMouseClick(e -> System.out.println("clicked!"));

TextField input = TextField.create().setPlaceholder("Type here...");

content.addChildren(input, action);
window.addChildren(content);
```

Constraints
- Size: `Constraints.pixels(n)`, `Constraints.relative(f)`, `Constraints.childBased(padding)`, `Constraints.sumOfChildrenWidth(gap)`, `Constraints.sumOfChildrenHeight(gap)`.
- Position: `Constraints.center()`, or compute `X/Y` with available size and margins.
- Min/Max: `setMinWidth/Height`, `setMaxWidth/Height`.

Layouts
- `LinearLayout` arranges children horizontally or vertically with alignment, cross-axis alignment (start/center/end/stretch), and gaps.
- `GridLayout` flows children into a grid with column count and gaps; use `GridLayout.GridData.span(colSpan, rowSpan)` via `setLayoutData` per child.

Managed vs overlay children
- Managed children are arranged by the container’s layout.
- Set `setManagedByLayout(false)` to float a child as an overlay (drawn above and hit-tested first).

IDs, classes, and states
- `setId("my-id")` and `addStyleClass("my-class")` enable stylesheet rules.
- States (e.g., `HOVERED`, `FOCUSED`, `ACTIVE`, `DISABLED`) are set automatically by `UIManager` during interaction.

Event handling
- Use fluent handlers: `onMouseClick`, `onMouseRelease`, `onMouseEnter`, `onMouseLeave`, `onMouseDrag`, `onMouseScroll`, `onKeyPress`, `onCharTyped`, `onFocusGained`, `onFocusLost`.
- `onEvent(Event.ANY, listener)` to observe any event bubbled from a component subtree.

Grid example

```
import tytoo.weave.layout.GridLayout;
import tytoo.weave.component.components.display.ProgressBar;

Panel grid = Panel.create()
    .setLayout(GridLayout.of(2, 6))
    .setWidth(Constraints.relative(1.0f))
    .setHeight(Constraints.relative(1.0f));

Button b1 = Button.of("A");
Button b2 = Button.of("B");
ProgressBar bar = ProgressBar.create().setValue(0.42f);

bar.setLayoutData(GridLayout.GridData.span(2, 1));
grid.addChildren(b1, b2, bar);
window.addChildren(grid);
```

Input components and binding

```
import tytoo.weave.state.State;
import tytoo.weave.component.components.interactive.CheckBox;
import tytoo.weave.component.components.interactive.RadioButton;
import tytoo.weave.component.components.interactive.RadioButtonGroup;
import tytoo.weave.component.components.interactive.ComboBox;

State<Boolean> enabled = new State<>(true);
CheckBox toggle = CheckBox.of("Enable feature").bindChecked(enabled);

State<String> flavor = new State<>("vanilla");
RadioButtonGroup<String> group = RadioButtonGroup.create(flavor)
    .addChildren(RadioButton.of("vanilla", "Vanilla"),
                 RadioButton.of("choco", "Chocolate"));

State<String> selection = new State<>("one");
ComboBox<String> combo = ComboBox.create(selection)
    .setPlaceholder("Choose…")
    .addOption("One", "one")
    .addOption("Two", "two");

window.addChildren(toggle, group, combo);
```

Sliders

```
import tytoo.weave.component.components.interactive.Slider;

State<Integer> intValue = new State<>(50);
Slider<Integer> slider = Slider.integerSlider(Slider.Orientation.HORIZONTAL, 0, 100, intValue.get())
    .bindValue(intValue)
    .onValueChanged(v -> System.out.println("value=" + v));

window.addChildren(slider);
```

Text fields and areas

```
import tytoo.weave.component.components.interactive.TextArea;

TextField tf = TextField.create().setPlaceholder("Username");
TextArea ta = TextArea.create().setPlaceholder("Tell us more…");
window.addChildren(tf, ta);
```

Images and progress

```
import tytoo.weave.component.components.display.Image;
import tytoo.weave.component.components.display.ProgressBar;
import net.minecraft.util.Identifier;

Image img = Image.of(Identifier.of("minecraft", "textures/item/apple.png"));
ProgressBar pb = ProgressBar.create().setValue(0.7f);
window.addChildren(img, pb);
```

Next Step: [Effects, Animations & Easings](https://github.com/trethore/Weave/blob/main/docs/animations-effects.md)
