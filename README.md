# Weave

**Weave** is a declarative, component-based UI library for Minecraft.
It helps you build clean, responsive, and themeable GUIs that are easy to compose, extend, and ship in your mods.

![Some screen shot of the demo gui !](https://i.imgur.com/KycmXsg.png)

---

## Developer Docs

You can find the developer documentation [HERE](docs/README.md).

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

## Installation

Weave is published to GitHub Packages (Maven). Use Gradle with Fabric Loom and add it as a mod dependency via `modImplementation`.

1) Add GitHub Packages repository

In your root `build.gradle` (or `settings.gradle` for central repos), add:

```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/trethore/Weave")
        credentials {
            username = findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Recommended: put credentials in `~/.gradle/gradle.properties`:

```
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_PERSONAL_ACCESS_TOKEN
```

Your token needs `read:packages` scope.

2) Add the dependency

In your mod’s `dependencies` block:

```
dependencies {
    // Fabric loader + Fabric API as usual
    modImplementation "net.fabricmc:fabric-loader:${loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${fabric_version}"

    // Weave UI
    modImplementation "tytoo.weave:weave-ui:1.0.0+1.21.4"
}
```

Replace the version with the desired release (see GitHub Releases). The artifact coordinates are `tytoo.weave:weave-ui:<version>`.

3) Initialize Weave in your client initializer

Weave ships without a runtime entrypoint in the release artifact. Call `WeaveCore.init()` from your mod’s `ClientModInitializer` to register required events:

```
import net.fabricmc.api.ClientModInitializer;
import tytoo.weave.WeaveCore;

public final class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WeaveCore.init();
    }
}
```

4) Run in dev

```
./gradlew runClient
```

In-game:
- `/weave testgui` → open the demo screen
- `/weave reloadtheme` → reload the theme in dev

---

## Contributing

Contributions are welcome!

- Report bugs or request features in the [issue tracker](https://github.com/trethore/Weave/issues).
- Submit improvements and fixes as [pull requests](https://github.com/trethore/Weave/pulls).
- Before contributing, make sure you can build and test the project locally:

  ```bash
  ./gradlew build        # build artifacts
  ./gradlew runClient    # run in dev environment
  ```

Please follow best practices (clean code, descriptive commits, small PRs).

---

## License

This project is licensed under the [MIT License](LICENSE).
