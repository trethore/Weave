# Weave Developer Guide

Welcome to the **Weave Developer Guide**.  
This documentation walks you through everything you need to **install Weave, build UIs, style them, add animations, and extend the system** with your own components and effects.

---
## Supported minecraft versions

Weave currently only supports the version 1.21.4 of Minecraft (with Fabric API).

---

## How to use this guide

- **First-time reader:** Start with [Installation & Setup](installation.md) and read sequentially. Each page ends with a **Next Step →** link to guide you.
- **Looking for something specific?** Use the quick links below. You can jump directly to a topic and circle back later.

---

## Links

- [Installation & Setup](installation.md)
- [Create a WeaveScreen](weave-screen.md)
- [Components & Layout](components.md)
- [Effects, Animations & Easings](animations-effects.md)
- [Styles & Themes](styles-themes.md)
- [Extensibility (add your own)](extensibility.md)
- [State, Events & Constraints](state-events-constraints.md)
- [Focus & Keyboard Navigation](focus-keyboard-navigation.md)
- [Tooltips](tooltip.md)
- [Popups & Modals](popups-modals.md)
- [Virtualized ListView](virtualized-list.md)
- [Global Shortcuts](global-shortcuts.md)
- [ImageManager & Cleanup](image-manager.md)
- [Recipes](recipes.md)

---

## Development tips

- In development:
    - `/weave testgui` opens the demo screen.
    - `/weave reloadtheme` reloads the default theme.
- Start with structure (components and layout), then layer styling, then add animations last.
- Prefer binding component state to your data model rather than pushing values from event handlers.
- Keep theme rules generic; use ids/classes for one-offs to avoid over-specific selectors.

---

**Next Step → [Installation & Setup](installation.md)**  
