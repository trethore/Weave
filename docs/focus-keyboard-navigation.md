# Focus & Keyboard Navigation

Weave provides predictable focus handling and rich keyboard navigation out of the box. This page explains how focus is assigned, how to make your components focusable, and which keys are handled by default.

---

## What this page covers

- How focus moves with Tab/Shift+Tab and focus scopes
- Making components focusable and controlling tab order
- Default key behavior for buttons, radios, sliders, and escape handling
- Focus events: listen for focus gained/lost
- Requesting or clearing focus programmatically

---

## Making Components Focusable

Only focusable components participate in keyboard navigation. Most interactive components provided by Weave are focusable by default. For your own components, enable focus like this:

```java
import tytoo.weave.component.Component;

public final class MyFocusable extends Component<MyFocusable> {
    public MyFocusable() {
        setFocusable(true);
    }
}
```

You can query focus state at any time via `component.isFocused()`.

## Tab Order and Scopes

- Pressing Tab moves focus forward; Shift+Tab moves backwards.
- The default order is the visual/tree order of focusable components within the current scope.
- Set an explicit order with `setTabIndex(int)`; if any component within a scope has a positive tab index, numeric order is used among those components.

```java
textField.setTabIndex(10);
submitButton.setTabIndex(20);
```

### Focus Scopes (Overlays & Popups)

Focus traversal is scoped to the nearest ancestor that is not managed by a layout (e.g., popups, overlays). This means Tab stays within an open popup/modal and does not jump back to the underlying UI until the popup is closed.

- Overlays created via `UIManager.openPopup(...)` automatically form a scope.
- You can opt a container out of layout management yourself with `setManagedByLayout(false)` if you want a custom scope root.

## Default Key Behavior

UIManager provides sensible defaults for common widgets:

- Buttons, CheckBoxes, RadioButtons, ComboBox
  - Enter/Space: activates the focused control (fires click-like events).
- RadioButton within a `RadioButtonGroup`
  - Left/Up: move selection to the previous radio
  - Right/Down: move selection to the next radio
- Slider (integer/float/double)
  - Left/Down: decrement by step
  - Right/Up: increment by step
  - Home/End: jump to min/max
- Escape
  - If a popup is open and configured to close on Esc, closes the topmost popup.
  - Otherwise, clears the current focus.

These behaviors are handled by `UIManager` and require no extra code on your side.

## Focus Events

Listen for focus changes on any component:

```java
import tytoo.weave.event.focus.FocusGainedEvent;
import tytoo.weave.event.focus.FocusLostEvent;

textField.onFocusGained((FocusGainedEvent e) -> {
    // highlight or start caret blinking
});

textField.onFocusLost((FocusLostEvent e) -> {
    // commit value or cleanup
});
```

## Programmatic Focus Control

From a `WeaveScreen`, you can request or clear focus via `UIManager`.

```java
import tytoo.weave.ui.UIManager;

// Inside your WeaveScreen subclass (this extends Screen)
UIManager.requestFocus(this, textField); // Focus the text field
UIManager.clearFocus(this);              // Clear any focus
```

---

**Next Step → [Tooltips](tooltip.md)**

