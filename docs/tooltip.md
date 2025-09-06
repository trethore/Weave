# Tooltips

Weave includes a lightweight tooltip system that attaches content to any component and shows it on hover or focus. Tooltips can follow the mouse or be anchored to the owner, and they support custom content and sizing.

---

## What this page covers

- Attaching tooltips to components
- Options: delay, follow-mouse, placement, max width, fades
- Behavior on hover vs focus and how to pin/unpin in dev
- Using rich content inside tooltips

---

## Quick Start

Attach a text tooltip to any component:

```java
import net.minecraft.text.Text;
import tytoo.weave.ui.tooltip.TooltipOptions;

textField.setTooltip(Text.literal("This is a text field.\nPaste, copy, select…"),
        new TooltipOptions()
            .setDelayMs(250)
            .setMaxWidth(220f)
            .setFollowMouse(true));
```

## Options

`TooltipOptions` lets you tune presentation and behavior:

- `setDelayMs(long)`: Show delay in milliseconds (default 500ms).
- `setFollowMouse(boolean)`: If true, tooltip follows the cursor; otherwise it anchors to the owner.
- `setPlacement(Placement)`: When not following the mouse, place tooltip on TOP, BOTTOM, LEFT, or RIGHT of the owner.
- `setMaxWidth(float)`: Wrap text or constrain custom content width.
- `setFadeInMs(long)` / `setFadeOutMs(long)`: Fade durations.

## Custom Content

Tooltips support arbitrary components as content:

```java
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.ui.tooltip.TooltipOptions;

Panel tip = Panel.create()
    .setPadding(6)
    .setWidth(Constraints.childBased())
    .setHeight(Constraints.childBased());
tip.addChild(SimpleTextComponent.of("Advanced info").setScale(1.1f));

button.setTooltip(tip, new TooltipOptions().setFollowMouse(false).setPlacement(TooltipOptions.Placement.BOTTOM));
```

## Behavior Notes

- Tooltips show for the component under the mouse or the currently focused component, if one is attached.
- Moving the mouse updates the position immediately when following the cursor.
- In development, hold Alt to pin/unpin the tooltip for inspection.

---

**Next Step → [Popups & Modals](popups-modals.md)**

