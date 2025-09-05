# State, Events & Constraints

How these fit together
- State drives data; events reflect user intent; constraints determine layout. Bind components to state so visuals reflect changes, react to events to update state, and let your layout/constraints do the heavy lifting for positioning.

State
- `State<T>` stores observable values; `get()`, `set(v)`, `addListener(cb)`, and `bind(cb)` are available.
- Bind components to state to reflect and propagate changes.

Examples

```
import tytoo.weave.state.State;
import tytoo.weave.component.components.interactive.CheckBox;

State<Boolean> visible = new State<>(true);
CheckBox toggle = CheckBox.of("Show panel").bindChecked(visible);

Panel panel = Panel.create().bindVisibility(visible);
```

Events
- Mouse: `MouseClickEvent`, `MouseReleaseEvent`, `MouseEnterEvent`, `MouseLeaveEvent`, `MouseDragEvent`, `MouseScrollEvent`
- Keyboard: `KeyPressEvent`, `CharTypeEvent`
- Focus: `FocusGainedEvent`, `FocusLostEvent`
- Listen with fluent helpers or `onEvent(Event.ANY, ...)`.

```
button.onMouseEnter(e -> button.addStyleClass("hover"));
button.onMouseLeave(e -> button.removeStyleClass("hover"));
button.onKeyPress(e -> { /* handle space/enter */ });
```

Constraints
- Pixel, relative, center, child-based, sum-of-children, aspect ratio.

APIs
- Size: `setWidth(Constraints.pixels(w))`, `setHeight(Constraints.relative(1.0f))`
- Position: `setX(Constraints.center())`, `setY(Constraints.center())`
- Min/Max: `setMinWidth(h)`, `setMaxHeight(h)`
- Aspect: `setWidth(Constraints.aspect(16f/9f))` or on height.

Auto margins
- Provide `Float.NaN` for both horizontal or vertical margins via styles to center with auto margins; the engine converts to `center()` constraints internally.

---

**Next Step:** [Recipes](https://github.com/trethore/Weave/blob/main/docs/recipes.md)
