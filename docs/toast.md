# Toasts

Weave provides a simple system for showing unobtrusive notifications, known as "toasts". Toasts appear on the screen for a short period and then fade out. They are useful for showing confirmations, alerts, or other brief messages.

---

## What this page covers

- Showing a simple text toast
- Using custom components as toast content
- Customizing toast behavior with `ToastOptions`

---

## Quick Start

Show a simple text toast with default options:

```java
import tytoo.weave.ui.toast.ToastManager;

ToastManager.show("File saved successfully!");
```

## Custom Content

You can use any Weave component as the content of a toast.

```java
import tytoo.weave.ui.toast.ToastManager;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.constraint.constraints.Constraints;

Panel content = Panel.create()
    .setPadding(8)
    .setWidth(Constraints.childBased())
    .setHeight(Constraints.childBased());
content.addChild(SimpleTextComponent.of("Custom Toast!").setScale(1.2f));

ToastManager.show(content);
```

## Options

`ToastOptions` lets you customize the behavior of toasts:

- `setDurationMs(long)`: The duration in milliseconds the toast is visible (default 3000ms).
- `setFadeInMs(long)` / `setFadeOutMs(long)`: The duration of the fade animations.
- `setPosition(Position)`: The position of the toast on the screen. Can be `TOP_RIGHT` (default), `TOP_LEFT`, `BOTTOM_RIGHT`, or `BOTTOM_LEFT`.
- `setMargin(float)`: The margin from the edges of the screen.
- `setGap(float)`: The gap between multiple toasts.

Example with options:

```java
import tytoo.weave.ui.toast.ToastManager;
import tytoo.weave.ui.toast.ToastOptions;

ToastManager.show("This is a toast in the bottom left.",
        new ToastOptions()
            .setDurationMs(5000)
            .setPosition(ToastOptions.Position.BOTTOM_LEFT));
```

---

**Next Step → [Tooltips](tooltip.md)**
