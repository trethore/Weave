# Global Shortcuts

Use the ShortcutRegistry to register keyboard shortcuts that work at different scopes: globally, per screen, or within a component subtree. Shortcuts are evaluated with priority and can be enabled/disabled based on context.

---

## What this page covers

- Defining key chords (e.g., Ctrl+S, Ctrl+Shift+P)
- Registering for global, screen, or component-tree scope
- Conditional enablement and priorities
- Unregistering shortcuts

---

## Defining a Shortcut

```java
import org.lwjgl.glfw.GLFW;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;

ShortcutRegistry.Shortcut saveShortcut = ShortcutRegistry.Shortcut
    .of(ShortcutRegistry.KeyChord.ctrl(GLFW.GLFW_KEY_S), ctx -> {
        // Perform save action
        doSave();
        return true; // consume
    })
    .withPriority(100);
```

KeyChord helpers include `of(key)`, `ctrl(key)`, `ctrlShift(key)`, and `ctrlAlt(key)`.

## Registering a Shortcut

```java
import tytoo.weave.ui.shortcuts.ShortcutRegistry;

// Global: active for any Weave screen if not handled elsewhere
ShortcutRegistry.Registration reg1 = ShortcutRegistry.registerGlobal(saveShortcut);

// Screen-specific
ShortcutRegistry.Registration reg2 = ShortcutRegistry.registerForScreen(this, saveShortcut);

// Component-tree: only when focus is inside the given subtree
ShortcutRegistry.Registration reg3 = ShortcutRegistry.registerForComponent(window, saveShortcut);
```

Shortcuts are dispatched by `UIManager` after the focused component has had a chance to handle the key press. If a focused component consumes the event, shortcuts will not run.

## Conditional Enablement

Enable or disable a shortcut based on runtime context using `.when(...)`:

```java
ShortcutRegistry.Shortcut quickOpen = ShortcutRegistry.Shortcut
    .of(ShortcutRegistry.KeyChord.ctrlShift(GLFW.GLFW_KEY_P), ctx -> {
        openCommandPalette();
        return true;
    })
    .when(ctx -> !(ctx.focused() instanceof tytoo.weave.component.components.interactive.TextField));
```

## Unregistering

Keep the `Registration` and call `unregister()` when the shortcut should no longer be active (e.g., when closing a screen or disposing a tool):

```java
reg1.unregister();
reg2.unregister();
reg3.unregister();
```

---

**Next Step → [Virtualized ListView](virtualized-list.md)**

