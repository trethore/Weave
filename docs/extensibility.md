# Extensibility: Components, Effects, Properties, Themes

Weave is designed to be pluggable and non-intrusive. Add new components, effects, style properties, renderers, and transitions without editing core.

New component

Skeleton

```
import tytoo.weave.component.Component;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.constraint.constraints.Constraints;

public final class Badge extends Component<Badge> {
    public static final class StyleProps {
        public static final StyleProperty<java.awt.Color> COLOR = new StyleProperty<>("badge.color", java.awt.Color.class);
        public static final StyleProperty<Float> RADIUS = new StyleProperty<>("badge.radius", Float.class);
    }

    private Badge() {
        Stylesheet ss = ThemeManager.getStylesheet();
        float size = 10f; // or ss.get(this, someDefaultProp, 10f)
        setWidth(Constraints.pixels(size));
        setHeight(Constraints.pixels(size));
    }

    public static Badge create() { return new Badge(); }
}
```

Expose typed `StyleProperty` keys in an inner `StyleProps` class so themes can target your component.

Style defaults in a theme

```
stylesheet.addRule(new StyleRule(
    new StyleSelector(Badge.class, null, null, null),
    Map.of(
        Badge.StyleProps.COLOR, new java.awt.Color(40, 160, 220),
        Badge.StyleProps.RADIUS, 4f
    )
));
```

New effect

```
import net.minecraft.client.gui.DrawContext;
import tytoo.weave.effects.Effect;
import tytoo.weave.component.Component;

public record GlowEffect(int strength) implements Effect {
    @Override public void beforeDraw(DrawContext ctx, Component<?> c) { /* setup */ }
    @Override public void afterDraw(DrawContext ctx, Component<?> c)  { /* teardown */ }
}

// Usage: component.addEffect(new GlowEffect(8));
```

New renderer
- Implement `ComponentRenderer` (optionally `ColorableRenderer` / `CloneableRenderer`) and assign via stylesheet `ComponentStyle.StyleProps.*RENDERER`.

New style property transitions

If your component/property should animate when style state changes, register it:

```
import tytoo.weave.animation.StyleTransitionRegistry;
import tytoo.weave.animation.Interpolators;

StyleTransitionRegistry.registerStyleProperty(
    Badge.class,
    Badge.StyleProps.RADIUS,
    0.0f,
    Interpolators.FLOAT,
    (badge, r) -> badge.getStyle().setBaseRenderer(/* renderer that uses r */),
    null
);
```

Custom animation builder
- Provide richer `animate()` APIs for your component:

```
import tytoo.weave.animation.Animator;
import tytoo.weave.animation.AnimationBuilder;

public final class BadgeAnimationBuilder extends AnimationBuilder<Badge> {
    public BadgeAnimationBuilder(Badge badge) { super(badge); }
    public BadgeAnimationBuilder pulse() { return (BadgeAnimationBuilder) scale(1.1f).then(() -> scale(1.0f)); }
}

// Register once (e.g., in your init):
Animator.registerBuilder(Badge.class, BadgeAnimationBuilder::new);
```

New theme
- Implement `Theme` with your own `Stylesheet` and `TextRenderer`.
- Apply with `ThemeManager.setTheme(new MyTheme())`. In dev, `/weave reloadtheme` reinstantiates the default theme; use your own swap logic for custom themes.

Variables and runtime customization
- Global: `ThemeManager.setGlobalVar("key", value)` → invalidates styles for all screens.
- Component-local: `setVar("key", value)` → affects resolution within that subtree.

Version & API targets
- Keep your code aligned with Minecraft 1.21.4 and Yarn 1.21.4+build.8 (the versions configured by Weave).

Example: custom SolidBadgeRenderer

```
import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;
import tytoo.weave.style.renderer.ComponentRenderer;

public final class SolidBadgeRenderer implements ComponentRenderer {
    private final java.awt.Color color;
    private final float radius;
    public SolidBadgeRenderer(java.awt.Color color, float radius) {
        this.color = color;
        this.radius = radius;
    }
    @Override
    public void render(DrawContext ctx, Component<?> c) {
        // draw a rounded rect using c.getLeft()/getTop()/getWidth()/getHeight()
        // with `color` and `radius` via your render utils
    }
}
```

Attach in theme

```
stylesheet.addRule(new StyleRule(
    new StyleSelector(Badge.class, null, null, null),
    Map.of(ComponentStyle.StyleProps.BASE_RENDERER, new SolidBadgeRenderer(new java.awt.Color(40,160,220), 4f))
));
```
