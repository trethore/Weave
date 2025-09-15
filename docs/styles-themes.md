# Styles & Themes

Weave uses a powerful styling system inspired by CSS to separate a UI's appearance from its structure. This allows you to create reusable, themeable components and make global visual changes without altering your component-building code.

---

## What this page covers

- The core concepts: **Stylesheets**, **Rules**, **Selectors**, and **Properties**.
- How the styling cascade and selector specificity work.
- Using **variables** to create consistent and easily configurable themes.
- How to define styles for component parts (like a slider's thumb).
- The development workflow for tweaking styles and reloading themes.

---

## The Styling System: A Mental Model

1.  **Theme:** The active `Theme` provides a global `Stylesheet`. `ThemeManager` controls which theme is active.
2.  **Stylesheet:** A collection of `StyleRule` objects.
3.  **StyleRule:** A rule connects a `StyleSelector` to a map of `StyleSlot` values.
4.  **Selector:** A query that determines which components a rule applies to (e.g., "all buttons with the class `primary` that are currently hovered").
5.  **Style Slot:** A typed key for a visual attribute (e.g., `LayoutStyleProperties.BORDER_COLOR`). Slots belong to component/theme contracts so themes know exactly which visuals they can supply.
6.  **Cascade:** Styles are resolved by collecting all matching rules from the global theme and any local stylesheets on the component or its ancestors. Rules with more specific selectors override those with less specific ones.

## Stylesheets and Rules

The `Stylesheet` is the central repository for your UI's visual language. You add rules to define how components should look.

### Contracts & Slots

Every component declares a `ComponentThemeContract` listing the style slots it understands. Slots are typed (`StyleSlot`) and
scoped to specific component types, so supplying a value that does not match a component's contract will be ignored (or
warned about if the slot is required). You can introspect contracts at runtime via `StyleContractRegistry.resolve(Button.class)`
to drive editors or validations, and custom components should register their own contracts during initialization.

```java
// Get the active stylesheet
Stylesheet stylesheet = ThemeManager.getStylesheet();

// Create a new rule
StyleRule rule = new StyleRule(
    // The selector defines what this rule applies to
    new StyleSelector(
        tytoo.weave.component.components.interactive.Button.class, // Target all Button components
        null,                                                        // No ID requirement
        Set.of("primary-button"),                                    // Must have the "primary-button" class
        Set.of(StyleState.HOVERED)                                   // Must be in the HOVERED state
    ),
    // The slots define the visual styles
    Map.of(
        ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(100, 150, 255)),
        TextComponent.StyleProps.TEXT_COLOR, Color.WHITE
    )
);

// Add the rule to the stylesheet
stylesheet.addRule(rule);
```

### Local Styles

For one-off styling needs, you can add rules directly to a component instance. These rules only apply to that component and its descendants.

```java
Panel warningBox = Panel.create()
    .addLocalStyleRule(new StyleRule(
        new StyleSelector(Panel.class, null, null, null),
        Map.of(LayoutStyleProperties.BORDER_COLOR, new Color(220, 50, 50))
    ));
```

## Selectors

Selectors are the heart of the styling system, allowing for precise targeting of components.

- **By Type:** `new StyleSelector(Button.class, ...)`
- **By ID:** `new StyleSelector(Component.class, "main-menu", ...)` (matches `component.setId("main-menu")`)
- **By Class:** `new StyleSelector(Component.class, null, Set.of("card", "large"), ...)` (matches `component.addStyleClass("card").addStyleClass("large")`)
- **By State:** `new StyleSelector(..., Set.of(StyleState.HOVERED, StyleState.FOCUSED))`
- **By Hierarchy:**
    - `StyleSelector.descendant(Panel.class, Button.class, ...)`: Matches a Button anywhere inside a Panel.
    - `StyleSelector.child(Panel.class, Button.class, ...)`: Matches a Button that is a direct child of a Panel.
- **By Part:** `StyleSelector.part(Slider.class, "thumb", ...)`: Targets a specific named child within a composite component. The child field in the parent must be annotated with `@NamedPart`.

## Variables and Computed Values

Variables allow you to define central design tokens (like a primary color) and reuse them throughout your stylesheet.

**1. Define a Variable Key**
```java
public static final StyleVariable<Color> PRIMARY_ACCENT = new StyleVariable<>("weave.color.primary", new Color(40, 160, 220));
```

**2. Use the Variable in a Rule**
```java
stylesheet.addRule(new StyleRule(
    ...,
    Map.of(ComponentStyle.Slots.ACTIVE_RENDERER, new Var(PRIMARY_ACCENT))
));
```

**3. Override at Runtime**
This will automatically restyle the entire UI to use the new color.
```java
ThemeManager.setGlobalVar("weave.color.primary", new Color(255, 80, 80));
```

## Themes

A `Theme` is a class that provides a `Stylesheet` and a default `TextRenderer`. You can create multiple themes (e.g., for light and dark mode) and switch between them at runtime using `ThemeManager.setTheme(new MyTheme())`.

## Development Workflow

- **`/weave reloadtheme`:** In a development environment, this command reinstantiates and applies the default theme, allowing you to see style changes without restarting the game.
- **Hot-swapping Variables:** Use `ThemeManager.setGlobalVar(...)` to tweak colors and other values live in-game.

---

**Next Step → [Extensibility (add your own)](extensibility.md)**
