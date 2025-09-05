# Styles & Themes

Mental model
- The active Theme provides a base Stylesheet. Specificity and cascade apply: global theme → ancestor local rules → component local rules. Variables let you centralize shared values; computed values derive visuals from component state.

Stylesheets
- Central source of visual properties and renderers: `ThemeManager.getStylesheet()`.
- Attach rules with `stylesheet.addRule(new StyleRule(selector, properties))`.
- Resolution merges theme rules, ancestors’ local rules, and the component’s local rules, sorted by selector specificity.

Selectors
- Type: `new StyleSelector(Button.class, null, null, null)`
- ID and classes: `#id`, `.class` via `setId` / `addStyleClass`
- States: `:hovered`, `:focused`, `:active`, `:selected`, `:disabled`, `:valid`, `:invalid`
- Hierarchy: `descendant` and `child` helpers to target elements within parents
- Parts: `StyleSelector.part(HostType.class, "partFieldName", PartType.class, ...)` targets children annotated with `@NamedPart`

Properties
- Common: `CommonStyleProperties` (cursor, accent-color, transition-duration/easing, scroll-amount)
- Layout: `LayoutStyleProperties` (padding, margin, width/height/min/max, border/overlay-border width/color/radius)
- Component-specific: each component exposes a `StyleProps` inner class with typed `StyleProperty<T>` keys (e.g., `TextComponent.StyleProps.TEXT_COLOR`)

Local styles

```
// Attach rule to a specific component instance
component.addLocalStyleRule(new StyleRule(
    new StyleSelector(Component.class, null, Set.of("warning"), null),
    Map.of(LayoutStyleProperties.BORDER_COLOR, new Color(220, 60, 60))
));

// Or batch via a local stylesheet
component.addLocalStylesheet(ss -> {
    ss.addRule(...);
});
```

Variables
- Global theme variables: `ThemeManager.setGlobalVar("weave.color.primary", color)` invalidate and restyle the UI.
- Component-local variables: `component.setVar("key", value)` affect descendant resolution.
- Use variables in rules with `new Var(new StyleVariable<>("name", defaultValue))` and computed values with `new Computed<>(component -> ...)`.

Themes
- Implement `Theme` to supply a `Stylesheet` and a `TextRenderer`.
- Apply with `ThemeManager.setTheme(new MyTheme())`; restyling occurs automatically, animations run if transitions are configured.

Example theme rule

```
stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.components.interactive.Button.class, null, Set.of("interactive-visual"), null),
    Map.ofEntries(
        Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(50, 50, 50))),
        Map.entry(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(70, 70, 70)))
    )
));
```

Next Step: [Extensibility](https://github.com/trethore/Weave/blob/main/docs/extensibility.md)

Dev workflow
- Use `/weave reloadtheme` in dev to reinstantiate the default theme.
- Hot variables: `ThemeManager.setGlobalVar(...)` updates styles at runtime.

Cursor customization

```
import tytoo.weave.ui.CursorType;

// I-beam for all text inputs is already set by DefaultTheme.
// Example: set pointer for any component with class .clickable
stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.Component.class, null, Set.of("clickable"), null),
    Map.of(CommonStyleProperties.CURSOR, CursorType.POINTER)
));
```

Part selectors

```
// Style the Slider thumb via part selector
stylesheet.addRule(new StyleRule(
    StyleSelector.part(tytoo.weave.component.components.interactive.Slider.class, "thumb", tytoo.weave.component.components.layout.Panel.class, null, null, null),
    Map.of(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new java.awt.Color(200,200,200)))
));
```
