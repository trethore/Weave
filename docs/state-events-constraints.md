# State, Events & Constraints

These three concepts form the foundation of a dynamic and responsive Weave UI. They work together to manage data, handle user interaction, and define the spatial arrangement of components. Understanding their relationship is key to building complex and robust interfaces.

---

## What this page covers

- **State Management:** Using `State<T>` to hold observable data and bind it to components for automatic UI updates.
- **Event System:** Listening and reacting to user input like mouse clicks, keyboard presses, and focus changes.
- **Layout Constraints:** A deep dive into the different constraint types for precisely controlling the size and position of components.

---

## How They Fit Together

A typical interaction loop in a Weave application follows this pattern:

1.  **Data is stored in `State` objects.** This is your single source of truth.
2.  **Components are *bound* to this `State`.** Their appearance (e.g., text content, checked status, visibility) is a function of the state's current value.
3.  **The user interacts with a component, firing an `Event`.**
4.  **An event listener updates the `State` object.**
5.  **The change in `State` automatically triggers an update** in all bound components, which are then re-rendered.
6.  **Constraints and Layouts** recalculate the position and size of components as needed.

This unidirectional data flow makes your UI predictable and easy to debug.

## State Management with `State<T>`

The `State<T>` class is a simple wrapper for a value that can be observed.

### Creating and Binding State

Binding is the most powerful feature of the state system. It creates a live link between your data and the UI.

```java
import tytoo.weave.state.State;
import tytoo.weave.component.components.interactive.CheckBox;
import tytoo.weave.component.components.layout.Panel;

// 1. Create a state object to hold a boolean value.
State<Boolean> isPanelVisible = new State<>(true);

// 2. Create a checkbox and BIND its checked property to the state.
//    Now, if the user clicks the checkbox, `isPanelVisible` will automatically update.
CheckBox toggle = CheckBox.of("Show panel").bindChecked(isPanelVisible);

// 3. Create a panel and BIND its visibility to the same state.
//    If `isPanelVisible` becomes false, this panel will automatically disappear.
Panel myPanel = Panel.create().bindVisibility(isPanelVisible);

window.addChildren(toggle, myPanel);

// You can also change the state programmatically, and the UI will update.
// isPanelVisible.set(false); // This would uncheck the box and hide the panel.
```

## The Event System

Weave provides a comprehensive event system for handling user input. Events are fired on the component directly under the cursor and then "bubble up" the component tree to ancestors.

### Listening to Events

You can attach listeners using the convenient `on...()` methods on any component.

- **Mouse:** `onMouseClick`, `onMouseRelease`, `onMouseEnter`, `onMouseLeave`, `onMouseDrag`, `onMouseScroll`
- **Keyboard:** `onKeyPress`, `onCharTyped` (requires the component to be focused)
- **Focus:** `onFocusGained`, `onFocusLost`

```java
Button myButton = Button.of("Submit");

myButton.onMouseEnter(event -> {
    System.out.println("Mouse entered the button!");
});

myButton.onMouseClick(event -> {
    if (event.getButton() == 0) { // Left mouse button
        System.out.println("Button clicked!");
        event.cancel(); // Stops the event from bubbling to parent components.
    }
});
```

## Layout Constraints

Constraints define the rules for a component's size and position within its parent. They are evaluated by the layout engine each time the UI needs to be redrawn.

### Sizing Constraints

Set with `setWidth()` and `setHeight()`.

- `Constraints.pixels(n)`: A fixed size in pixels.
- `Constraints.relative(f)`: A fraction of the parent's inner dimension (e.g., `1.0f` for 100%).
- `Constraints.childBased(padding)`: Size is determined by the largest child, plus padding.
- `Constraints.sumOfChildrenWidth(padding, gap)` / `Constraints.sumOfChildrenHeight(padding, gap)`: Size is the sum of all children's dimensions, plus padding and gaps. Useful for containers that should shrink-wrap their content.
- `Constraints.aspect(16f/9f)`: Maintains a specific aspect ratio. The size is calculated based on the component's other dimension.

### Positioning Constraints

Set with `setX()` and `setY()`.

- `Constraints.pixels(n)`: A fixed offset in pixels from the parent's top-left inner corner.
- `Constraints.relative(f)`: A fractional offset based on the parent's size.
- `Constraints.center()`: Automatically centers the component on the desired axis.

### Min/Max Sizing

You can further refine sizing with `setMinWidth/Height` and `setMaxWidth/Height`. These are always applied *after* the main sizing constraint is calculated.

---

**Next Step → [Recipes](recipes.md)**
