# Popups & Modals

Weave provides a flexible popup system for dropdowns, context menus, and modal dialogs. Popups are anchored to a target component and rendered on an overlay layer that manages focus, backdrops, and closing behavior.

---

## What this page covers

- Opening a popup anchored to a component
- Modal backdrops and close behavior
- Focus trapping and returning focus on close
- Styling the backdrop via stylesheet

---

## Opening a Popup

Use `UIManager.openPopup` to mount content on the overlay. Positioning is driven by an `Anchor` and `PopupOptions`.

```java
import tytoo.weave.ui.UIManager;
import tytoo.weave.ui.popup.Anchor;
import tytoo.weave.ui.popup.PopupOptions;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;

Panel menu = Panel.create()
    .setPadding(8)
    .setWidth(Constraints.pixels(180))
    .setHeight(Constraints.childBased());

UIManager.PopupHandle handle = UIManager.openPopup(
    menu,
    new Anchor(button, Anchor.Side.BOTTOM, Anchor.Align.START, 0f, 0f, 6f),
    new PopupOptions().setAutoFlip(true).setGap(6f)
);
```

The `Anchor` specifies the target component, which side to attach to, and how to align along that side. `PopupOptions` control behavior described below.

## Modals and Backdrops

To create a modal dialog, enable `setModal(true)`. This adds a backdrop to the overlay and sets reasonable defaults:

```java
PopupOptions opts = new PopupOptions()
    .setModal(true)                // add a backdrop
    .setTrapFocus(true)            // keep Tab focus within the popup
    .setCloseOnBackdropClick(true) // clicking the backdrop closes the popup
    .setCloseOnEsc(true)           // Esc closes the popup
    .setCloseOnFocusLoss(true);    // closing when focus leaves the popup

UIManager.PopupHandle handle = UIManager.openPopup(dialogPanel,
    new Anchor(window, Anchor.Side.TOP, Anchor.Align.CENTER, 0f, 80f, 0f), opts);
```

`UIManager` remembers the previously focused component and restores focus automatically when the popup is closed.

You can manually close a popup using the handle:

```java
UIManager.closePopup(handle);
```

## Styling the Backdrop

Backdrop visuals are themeable via style properties in your stylesheet:

```java
import tytoo.weave.ui.popup.PopupStyleProperties;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;
import tytoo.weave.component.components.layout.Panel;
import java.awt.Color;

stylesheet.addRule(new StyleRule(
    // Applies to the backdrop panel added for modals
    new StyleSelector(Panel.class, null, java.util.Set.of("popup-backdrop"), null),
    java.util.Map.of(
        PopupStyleProperties.BACKDROP_COLOR, new Color(0, 0, 0),
        PopupStyleProperties.BACKDROP_OPACITY, 0.45f,
        PopupStyleProperties.BACKDROP_CLICK_THROUGH, false
    )
));
```

Notes

- `autoFlip`: When true, flips the popup to the opposite side if it would overflow the screen.
- `gap`: Extra distance between the target and popup.
- `clickThroughBackdrop`: When true, the backdrop does not intercept clicks; use with caution.

---

**Next Step → [Global Shortcuts](global-shortcuts.md)**

