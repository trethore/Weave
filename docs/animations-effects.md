# Effects, Animations & Easings

When to use what
- Use effects for draw-time visual passes (e.g., shadows, outlines, scissoring) that wrap a component’s renderer.
- Use property animations when you want time-based changes to component properties (opacity, scale, position, color) triggered by code.
- Use style transitions to animate between style states (hover, focus, active) where the stylesheet drives the visual change.

Effects
- Add with `component.addEffect(Effects.shadow(...))`, `Effects.outline(...)`, `Effects.scissor()`, or `Effects.gradientOutline(...)`.
- Effects can run code before/after a component draws; implement `Effect` to build your own.

Examples

```
import tytoo.weave.effects.Effects;
import java.awt.Color;

window.addEffect(Effects.shadow(new Color(0,0,0,150), 2f, 2f, 8f, 6f));
window.addEffect(Effects.outline(Color.GRAY, 1f, true));
```

Property animations

```
import tytoo.weave.animation.Easings;

// Fade in
component.animate()
    .duration(300)
    .easing(Easings.EASE_OUT_SINE)
    .opacity(1.0f);

// Scale bounce and then run code
component.animate()
    .duration(250)
    .easing(Easings.EASE_OUT_BACK)
    .scale(1.1f)
    .then(() -> component.animate().duration(150).scale(1.0f));
```

Color animations
- Text: `TextComponent` uses an internal color state; `animate().color(new Color(...))` animates its text color.
- Images and colorable renderers: `animate().color(...)` or `animateRendererColor(...)` on a specific renderer.

Easings
- Built-ins: `LINEAR`, `EASE_IN_SINE`, `EASE_OUT_SINE`, `EASE_IN_OUT_SINE`, `EASE_IN_QUAD`, `EASE_OUT_QUAD`, `EASE_IN_OUT_QUAD`, `EASE_OUT_BACK`.
- Custom: provide any `EasingFunction` (lambda over `t` from 0..1).

Style transitions (state-driven)

Set transition defaults in your stylesheet to animate style changes (e.g., hover/focus/active):

```
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;

// Apply to all components
stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.Component.class, null, null, null),
    Map.of(
        CommonStyleProperties.TRANSITION_DURATION, 150L,
        CommonStyleProperties.TRANSITION_EASING, Easings.EASE_OUT_SINE
    )
));
```



When style state/class changes (e.g., `HOVERED`, `ACTIVE`, or toggling a class), matching properties registered in `StyleTransitionRegistry` animate smoothly.

Extending transitions
- Register transitions for your own style properties with `StyleTransitionRegistry.registerStyleProperty(...)` or computed keys via `registerComputed(...)`.

Animating on hover via stylesheet

```
import tytoo.weave.style.*;
import tytoo.weave.style.selector.StyleSelector;
import java.awt.Color;

// default background and hover transition for all buttons
stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.components.interactive.Button.class, null, Set.of("interactive-visual"), null),
    Map.ofEntries(
        Map.entry(ComponentStyle.StyleProps.NORMAL_RENDERER, new SolidColorRenderer(new Color(60,60,60))),
        Map.entry(CommonStyleProperties.TRANSITION_DURATION, 150L),
        Map.entry(CommonStyleProperties.TRANSITION_EASING, Easings.EASE_OUT_SINE)
    )
));

stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.components.interactive.Button.class, null, null, Set.of(StyleState.HOVERED)),
    Map.of(ComponentStyle.StyleProps.HOVERED_RENDERER, new SolidColorRenderer(new Color(80,80,80)))
));
```

Text color waves

```
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.style.ColorWave;

SimpleTextComponent label = SimpleTextComponent.of("Rainbow!").addStyleClass("rainbow");

stylesheet.addRule(new StyleRule(
    new StyleSelector(TextComponent.class, null, Set.of("rainbow"), null),
    Map.of(TextComponent.StyleProps.COLOR_WAVE, new ColorWave(ColorWave.createRainbow(36), 2f))
));
```

Next Step: [Styles & Themes](https://github.com/trethore/Weave/blob/main/docs/styles-themes.md)
