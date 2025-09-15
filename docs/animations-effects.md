# Effects, Animations & Transitions

Weave provides a powerful, multi-layered system for adding motion and advanced visuals to your UI. These are separated into three distinct concepts: static **effects**, programmatic **animations**, and state-driven **style transitions**.

---

## What this page covers

- **Effects:** How to add static, draw-time visual layers like shadows and outlines.
- **Property Animations:** How to programmatically animate component properties like opacity, scale, and color over time using a fluent API.
- **Style Transitions:** How to automatically animate style changes between states (e.g., hover, focus) by defining transitions in your stylesheet.
- **Easings:** The available easing functions for creating smooth, natural motion.

---

## When to Use What

- **Use Effects** for visual modifications that happen every frame, wrapping a component's renderer. They are static and not time-based.
  - *Examples:* Adding a drop shadow, an outline, or a scissoring mask.

- **Use Property Animations** for time-based changes triggered explicitly by your code, often in response to an event.
  - *Examples:* Fading a component in, making a button "bounce" when clicked, or moving a panel across the screen.

- **Use Style Transitions** for smooth, automatic animations between a component's visual states. The stylesheet defines the "before" and "after" look, and Weave handles the animation.
  - *Examples:* A button that smoothly changes color when hovered, or a text field whose border color animates when focused.

## Effects

Effects are added directly to a component instance and can run code before or after the component and its children are drawn.

```java
import tytoo.weave.effects.Effects;
import java.awt.Color;

// Add a soft drop shadow to the main window.
window.addEffect(Effects.shadow(new Color(0, 0, 0, 150), 2f, 2f, 8f, 6f));

// Add a 1px gray outline that is drawn inside the component's bounds.
panel.addEffect(Effects.outline(Color.GRAY, 1f, true));
```

## Property Animations

You can animate common properties using the fluent `.animate()` API on any component. This creates an `AnimationBuilder` where you can chain settings and actions.

```java
import tytoo.weave.animation.Easings;

// Fade a component in over 300ms with a sine easing function.
component.setOpacity(0f);
component.animate()
    .duration(300)
    .easing(Easings.EASE_OUT_SINE)
    .opacity(1.0f);

// Create a "bounce" effect on a button and run code when finished.
button.onMouseClick(e -> {
    button.animate()
        .duration(250)
        .easing(Easings.EASE_OUT_BACK)
        .scale(1.1f)
        .then(() -> {
            // Chain another animation to return to the original scale.
            button.animate().duration(150).scale(1.0f);
            System.out.println("Bounce animation complete!");
        });
});
```

### Available Easings

Weave includes a set of common easing functions in the `Easings` class:
- `LINEAR`
- `EASE_IN_SINE`, `EASE_OUT_SINE`, `EASE_IN_OUT_SINE`
- `EASE_IN_QUAD`, `EASE_OUT_QUAD`, `EASE_IN_OUT_QUAD`
- `EASE_OUT_BACK` (creates an overshoot effect)

You can also provide your own by implementing the `EasingFunction` interface.

## Style Transitions (State-Driven)

This is the most powerful way to create a responsive UI. Instead of writing animation code in your event handlers, you define the duration and easing for transitions in your stylesheet. When a component's state changes (e.g., it gains the `:hovered` state), Weave automatically animates any translatable properties to their new values.

**Step 1: Define Transition Behavior in Stylesheet**

This rule applies a 150ms `EASE_OUT_SINE` transition to all interactive components.

```java
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.style.StyleRule;
import tytoo.weave.style.selector.StyleSelector;

stylesheet.addRule(new StyleRule(
    // Target any component with the "interactive-visual" class
    new StyleSelector(tytoo.weave.component.Component.class, null, Set.of("interactive-visual"), null),
    Map.of(
        CommonStyleProperties.TRANSITION_DURATION, 150L,
        CommonStyleProperties.TRANSITION_EASING, Easings.EASE_OUT_SINE
    )
));
```

**Step 2: Define Styles for Different States**

These rules define the background renderer for a button in its normal and hovered states.

```java
import tytoo.weave.style.*;
import tytoo.weave.style.selector.StyleSelector;
import java.awt.Color;

// Default background renderer for buttons
stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.components.interactive.Button.class, null, null, null),
    Map.of(ComponentStyle.Slots.NORMAL_RENDERER, new SolidColorRenderer(new Color(60, 60, 60)))
));

// Background renderer when the button is hovered
stylesheet.addRule(new StyleRule(
    new StyleSelector(tytoo.weave.component.components.interactive.Button.class, null, null, Set.of(StyleState.HOVERED)),
    Map.of(ComponentStyle.Slots.HOVERED_RENDERER, new SolidColorRenderer(new Color(80, 80, 80)))
));
```

Now, when you hover over any button, its background color will smoothly animate from dark gray to light gray over 150ms. No Java animation code is required.

---

**Next Step → [Styles & Themes](styles-themes.md)**
