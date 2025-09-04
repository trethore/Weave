# Weave

**Weave** is a declarative, component-based UI library for Minecraft.
It helps you build clean, responsive, and themeable GUIs that are easy to compose, extend, and ship in your mods.

---

## Declarative UI

With Weave, you **describe what the UI should look like** and how it behaves based on state — not how to draw it step by step.

- Compose a **tree of components** (layouts, panels, buttons, text, images, etc.).
- Attach **styles and effects** through a stylesheet-like system with typed properties.
- Bind visuals and layout to **observable state**; when state changes, the UI updates automatically.
- Define **transitions and easing** for smooth, automatic style changes instead of imperative animations.

This makes UI code predictable and modular:
**state flows in → components render from state → Weave handles layout, rendering, input, and transitions.**

---

## Design & Philosophy

- **Composable** – Everything is a component or state you can combine and reuse.
- **Pluggable** – Register custom components, animations, and style properties.
- **Non-intrusive** – Extend via public APIs — no forking or editing core required.
- **Customizable** – Override visuals and interactions via stylesheets, themes, or custom renderers.
- **Future-proof** – Clear separation of concerns (Component Tree, Layout, Stylesheet, Animator) ensures stability as the core evolves.

---

## What’s Included

**Components**
- Button, CheckBox, ComboBox, RadioButton, RadioButtonGroup, Slider, TextField, TextArea, ImageButton

**Display**
- Text, WrappedText, Image, ProgressBar

**Layout**
- Window, Panel, ScrollPanel, Container, Canvas, Separator

**Layout Systems**
- LinearLayout, GridLayout
- Constraints: pixel, relative, center, aspect ratio, child-based, sum-of-children

**Styling & Theming**
- Stylesheet, StyleRule, typed StyleProperty & StyleValue
- Variables, computed values, dedicated renderers (solid, rounded, gradient, progress, text field parts, etc.)

**Effects**
- Shadow, Outline, Scissor, Composite/Colorable renderers

**Animation**
- Animator, Animation/AnimationBuilder
- Property interpolators (float, color, edge insets) and style transitions

**Easing**
- Linear, In/Out/InOut Sine, In/Out/InOut Quad, Out Back

**Integration**
- `WeaveScreen`, `UIManager` (focus/hover/active handling)
- Input/render bridging via mixins

**Dev Commands**
- `/weave testgui` → open demo screen
- `/weave reloadtheme` → reload theme in dev environment

---

## Supported Minecraft Versions

- **Minecraft 1.21.4** (Fabric API required)

---

## 🚀 Installation

Weave is designed to be **vendored directly** into your mod repository.

```
your-repo/
  weave/        # Weave source and resources (this library)
  my-mod/       # Your mod
```

1. **Add Weave sources and resources**
   Copy `src/client/java/tytoo/weave/**` and `src/client/resources/**` into `weave/src/client/...`.

2. **Wire mixins**
   Add the Weave client mixin config to your mod’s `fabric.mod.json` to bridge input/render events.

3. **Initialize Weave**
   In your client entrypoint:

   ```java
   import net.fabricmc.api.ClientModInitializer;
   import tytoo.weave.WeaveCore;

   public final class MyModClient implements ClientModInitializer {
       @Override
       public void onInitializeClient() {
           WeaveCore.init();
       }
   }
   ```

4. **Run in dev**
   ```bash
   ./gradlew runClient
   ```
   In-game, run `/weave testgui` to open the demo screen.

---

## Contributing

Contributions are welcome! 🎉

- Report bugs or request features in the [issue tracker](https://github.com/trethore/Weave/issues).
- Submit improvements and fixes as [pull requests](https://github.com/trethore/Weave/pulls).
- Before contributing, make sure you can build and test the project locally:

  ```bash
  ./gradlew build        # build artifacts
  ./gradlew runClient    # run in dev environment
  ./gradlew publishToMavenLocal  # publish to local Maven
  ```

Please follow best practices (clean code, descriptive commits, small PRs).

---

## License

This project is licensed under the [MIT License](LICENSE).
