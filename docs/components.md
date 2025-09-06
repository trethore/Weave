# Components & Layout

Weave UIs are built by composing `Component` instances into a tree. You start with a root `window`, add children, set their layout constraints, and attach event handlers to make them interactive. This declarative approach lets you focus on structure and appearance, while Weave handles the rendering and layout calculations.

---

## What this page covers

- The different categories of components available (Layout, Display, Interactive).
- How to add children and build a component tree.
- The distinction between layout-managed children and overlays.
- An overview of the core layout systems: `LinearLayout` and `GridLayout`.
- How to bind component properties (like a checkbox's state) to your mod's data.

---

## Core Concepts

### The Component Tree

Every UI starts with a root component (typically the `window` in a `WeaveScreen`). You build the hierarchy by adding children to containers.

```java
// A Panel is a generic container
Panel container = Panel.create();

// A Button and TextField are interactive components
Button actionButton = Button.of("Click Me");
TextField inputField = TextField.create();

// Add the button and text field as children of the panel
container.addChildren(actionButton, inputField);

// Add the panel to the main window
window.addChildren(container);
```

### Managed vs. Overlay Children

- **Managed Children (Default):** These are positioned and sized by their parent's `Layout` manager (e.g., `LinearLayout`). This is the most common type.
- **Overlay Children:** By calling `component.setManagedByLayout(false)`, you can make a component "float" above its siblings. It will ignore the parent's layout manager and rely solely on its own position constraints (`setX`, `setY`). Overlays are drawn last and receive input first, making them ideal for tooltips, popups, or dropdowns.

### IDs, Classes, and States

To apply styles from a stylesheet, you use selectors that target components.
- **ID:** `setId("my-unique-id")` for targeting a single, specific component.
- **Class:** `addStyleClass("card")` for applying a shared style to multiple components.
- **State:** `HOVERED`, `FOCUSED`, `ACTIVE`, `DISABLED` are automatically applied by the `UIManager` during user interaction. You can use these pseudo-classes in your stylesheet to create responsive visuals (e.g., a button that changes color on hover).

## Component Catalog

### Layout Components

These components are responsible for structuring your UI.

- **`Window`:** The root component for a `WeaveScreen`.
- **`Panel`:** A general-purpose container.
- **`ScrollPanel`:** A panel with built-in vertical scrolling for content that overflows its bounds.
- **`Container`:** A lightweight panel, useful for simple grouping.
- **`Separator`:** A horizontal or vertical line to divide sections.
- **`Canvas`:** A component that gives you a direct `DrawContext` callback for custom rendering.

### Display Components

These components display information but are not interactive.

- **`SimpleTextComponent` / `WrappedTextComponent`:** For single-line or multi-line text.
- **`Image`:** Displays a texture from a `net.minecraft.util.Identifier`.
- **`ProgressBar`:** A bar that visually represents a value.

### Interactive Components

These components respond to user input.

- **`Button` / `ImageButton`:** Clickable buttons with text or an image.
- **`CheckBox`:** A standard checkable box.
- **`RadioButton` / `RadioButtonGroup`:** For selecting one option from a set.
- **`ComboBox`:** A dropdown menu for selections.
- **`Slider`:** A draggable slider for selecting a numeric value.
- **`TextField` / `TextArea`:** Single-line and multi-line text input fields.
- **`ListView<T>`:** A virtualized list that efficiently renders very large datasets. See [Virtualized ListView](virtualized-list.md).

## Layout Systems

Layouts automate the positioning of managed children within a container. You set one on a panel using `.setLayout()`.

### LinearLayout

`LinearLayout` arranges children in a single horizontal or vertical row. It's perfect for forms, lists, and toolbars.

```java
import tytoo.weave.layout.LinearLayout;

// Arrange children vertically, aligned to the top, with an 8px gap.
// Cross-axis alignment stretches children to fill the container's width.
panel.setLayout(LinearLayout.of(
    LinearLayout.Orientation.VERTICAL,
    LinearLayout.Alignment.START,
    LinearLayout.CrossAxisAlignment.STRETCH,
    8
));
```

### GridLayout

`GridLayout` arranges children in a grid with a fixed number of columns. It's ideal for inventories, galleries, or dashboards. You can make an item span multiple columns or rows using `setLayoutData`.

```java
import tytoo.weave.layout.GridLayout;
import tytoo.weave.component.components.display.ProgressBar;

Panel grid = Panel.create()
    .setLayout(GridLayout.of(2, 6)); // 2 columns, 6px gap

Button b1 = Button.of("A");
Button b2 = Button.of("B");
ProgressBar bar = ProgressBar.create().setValue(0.75f);

// Make the progress bar span both columns
bar.setLayoutData(GridLayout.GridData.span(2, 1));

grid.addChildren(b1, b2, bar);
window.addChildren(grid);
```

## State Binding

For interactive components, it's best to bind their values to a `State` object. This creates a two-way connection: if the user changes the component, the `State` updates; if your code changes the `State`, the component's visuals update automatically.

```java
import tytoo.weave.state.State;
import tytoo.weave.component.components.interactive.CheckBox;
import tytoo.weave.component.components.interactive.Slider;

// Create a state object to hold a boolean value.
State<Boolean> featureEnabled = new State<>(true);

// Bind the CheckBox's checked status to the state.
CheckBox toggle = CheckBox.of("Enable Feature").bindChecked(featureEnabled);

// Create another component whose visibility depends on the same state.
Panel featurePanel = Panel.create().bindVisibility(featureEnabled);

// Example with a slider
State<Integer> volume = new State<>(50);
Slider<Integer> volumeSlider = Slider.integerSlider(Slider.Orientation.HORIZONTAL, 0, 100, volume.get())
    .bindValue(volume)
    .onValueChanged(v -> System.out.println("New volume: " + v));

window.addChildren(toggle, featurePanel, volumeSlider);
```

---

**Next Step → [Effects, Animations & Easings](animations-effects.md)**
