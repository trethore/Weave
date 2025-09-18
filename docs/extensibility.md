# Extensibility

Weave is designed from the ground up to be extended. You can add your own components, effects, style properties, and themes without modifying the core library. This ensures your custom code remains compatible with future Weave updates.

---

## What this page covers

- The core philosophy behind extending Weave.
- A step-by-step guide to creating a brand new, styleable component.
- How to implement custom effects and renderers.
- How to register your own style properties for automatic transitions.
- How to create a custom `AnimationBuilder` for a richer animation API.
- Instructions for creating and applying a new `Theme`.

---

## The Extensibility Mindset

- **Prefer Public APIs:** Build on top of Weave's contracts (`Component`, `Effect`, `ComponentRenderer`, etc.) rather than forking or modifying internals.
- **Expose Style Slots:** For custom components, define public static `StyleSlot` constants and register a `ComponentThemeContract`. This creates a clear API for theming and allows others to style your component.
- **Stylesheet-Driven Visuals:** Define your component's default appearance in your theme's stylesheet. This allows users to easily override the look and feel without changing your component's logic.
- **Register When Needed:** If you create new concepts that should interact with Weave's systems—like animatable style properties—be sure to register them with the appropriate registry (e.g., `StyleTransitionRegistry`).

## Creating a Custom Component

Here is the basic skeleton for a new `Badge` component.

```java
import tytoo.weave.component.Component;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.constraint.constraints.Constraints;
import java.awt.Color;

public final class Badge extends Component<Badge> {
    // 1. Expose typed StyleSlot keys in a nested class for organization.
    public static final class StyleSlots {
        public static final StyleSlot BADGE_COLOR = StyleSlot.of("badge.color", Badge.class, Color.class);
        public static final StyleSlot CORNER_RADIUS = StyleSlot.of("badge.radius", Badge.class, Float.class);
    }

    private Badge() {
        // 2. Set default layout constraints in the constructor.
        // Here, we'll make it a fixed 10x10 square by default.
        setWidth(Constraints.pixels(10f));
        setHeight(Constraints.pixels(10f));
    }

    // 3. Provide a static factory method for creation.
    public static Badge create() {
        return new Badge();
    }
}
```

Now, in your theme's `Stylesheet`, you can define the default appearance for all `Badge` components.

```java
stylesheet.addRule(new StyleRule(
    new StyleSelector(Badge.class, null, null, null),
    Map.of(
        Badge.StyleSlots.BADGE_COLOR, new Color(40, 160, 220),
        Badge.StyleSlots.CORNER_RADIUS, 4f,
        // We can also assign a custom renderer.
        ComponentStyle.Slots.BASE_RENDERER, new SolidBadgeRenderer()
    )
));
```

Don't forget to register your properties so themes and tools can discover them:

```java
import tytoo.weave.style.contract.ComponentStyleRegistry;
import tytoo.weave.style.contract.StyleProperty;

public final class Badge {
    public static final StyleProperty<Color> BADGE_COLOR;
    public static final StyleProperty<Float> CORNER_RADIUS;

    static {
        ComponentStyleRegistry.Builder<Badge> builder = ComponentStyleRegistry.component(Badge.class, "badge");
        BADGE_COLOR = builder.required("color", Color.class);
        CORNER_RADIUS = builder.optional("corner-radius", Float.class);
        builder.register();
    }
}
```

## Creating a Custom Renderer

A `ComponentRenderer` is responsible for drawing a component. Implement the interface and use the `DrawContext` and component dimensions to render anything you want.

```java
public final class SolidBadgeRenderer implements ComponentRenderer {
    @Override
    public void render(DrawContext ctx, Component<?> c) {
        if (!(c instanceof Badge badge)) return;

        // Get style values from the stylesheet at render time.
        Stylesheet ss = ThemeManager.getStylesheet();
        Color color = ss.get(badge, Badge.BADGE_COLOR, Color.RED);
        float radius = ss.get(badge, Badge.CORNER_RADIUS, 0f);

        // Use your rendering logic.
        Render2DUtils.drawRoundedRect(
            ctx,
            badge.getLeft(), badge.getTop(),
            badge.getWidth(), badge.getHeight(),
            radius, color
        );
    }
}
```

## Registering Custom Transitions

If your custom component has style properties that should animate smoothly between states, register them with the `StyleTransitionRegistry`.

```java
import tytoo.weave.animation.StyleTransitionRegistry;
import tytoo.weave.animation.Interpolators;

// In your mod's initializer:
StyleTransitionRegistry.registerStyleProperty(
    Badge.class,                        // The component type this applies to
    Badge.StyleSlots.CORNER_RADIUS,     // The StyleSlot to animate
    0.0f,                               // The default value if none is found
    Interpolators.FLOAT,                // How to interpolate between two float values
    (badge, radius) -> {                // A function to apply the value during animation
        // This is a simplified example. In a real scenario, you would
        // update a renderer or internal state that uses this radius.
    },
    null                                // An optional function to run on finish
);
```

## Creating a New Theme

Implement the `Theme` interface to provide your own `Stylesheet` and default `TextRenderer`. Then, apply it using the `ThemeManager`.

```java
public class MyCoolTheme implements Theme {
    private final Stylesheet stylesheet;

    public MyCoolTheme() {
        this.stylesheet = new Stylesheet();
        // ... call a method to populate it with StyleRules ...
    }

    @Override
    public Stylesheet getStylesheet() { return this.stylesheet; }

    @Override
    public TextRenderer getTextRenderer() { /* return a custom or default font */ }
}

// Apply the theme in your mod's initializer
ThemeManager.setTheme(new MyCoolTheme());
```

---

**Next Step → [State, Events & Constraints](state-events-constraints.md)**
