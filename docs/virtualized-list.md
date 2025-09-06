# Virtualized ListView

`ListView<T>` efficiently renders large lists by virtualizing rows. Only the items visible in the viewport (plus a small buffer) are mounted, reusing row containers as you scroll. It supports selection, keyboard navigation, and flexible item rendering via a factory.

---

## What this page covers

- Creating a `ListView` with an item factory
- Providing data from `ObservableList<T>` or `State<List<T>>`
- Height modes and layout considerations
- Selection, keyboard navigation, and scrolling helpers

---

## Quick Start

```java
import tytoo.weave.component.components.interactive.ListView;
import tytoo.weave.state.ObservableList;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.constraint.constraints.Constraints;

ObservableList<String> items = new ObservableList<>();
for (int i = 1; i <= 10_000; i++) items.add("Item #" + i);

ListView<String> list = ListView.<String>create()
    .setWidth(Constraints.relative(1.0f))
    .setHeight(Constraints.relative(1.0f))
    .setItems(items)
    .setGap(2f)
    .setHeightMode(ListView.HeightMode.MEASURE_ONCE)
    .setSelectionMode(ListView.SelectionMode.SINGLE)
    .setItemFactory(s -> SimpleTextComponent.of(s).setPadding(2, 4));
```

Mount the list inside any container; `ListView` manages its own `ScrollPanel` and content panel.

## Data Sources

- `setItems(ObservableList<T>)`: Listen for incremental changes; the view updates automatically when the list is modified.
- `bindItems(State<List<T>>)` : Bind to a reactive state; updates when the state value changes.

## Item Rendering

Provide an item factory that returns a component for each data item. The returned component is mounted inside a row container that fills the width of the list. Keep your item factory lightweight; rows are pooled and reused.

```java
list.setItemFactory(user -> UserRow.create(user));
```

## Height Modes

- `FIXED`: Every row uses the same fixed height set by `setFixedItemHeight(float)`.
- `MEASURE_ONCE` (default): Measures one sample row to determine height; use when rows are uniform but you prefer automatic sizing.

The list sets its internal content height to `rows * itemHeight + gaps` and only mounts visible rows based on scroll position.

## Selection and Keyboard Navigation

- Selection modes: `SINGLE` or `MULTIPLE`.
- Mouse: Click to select; Shift-click selects ranges; Ctrl-click toggles items (in MULTIPLE mode).
- Keyboard:
  - Up/Down: move focus (accelerates when held)
  - Home/End: jump to first/last
  - PageUp/PageDown: move by a viewport-sized step

Hook selection change events:

```java
list.onSelectionChanged(indices -> System.out.println("Selected: " + indices));
```

---

**Next Step → [Developer Guide Overview](README.md)**

