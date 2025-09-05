# Creating a WeaveScreen

**`WeaveScreen` is the bridge between your Weave UI and Minecraft.** \
It's a specialized `net.minecraft.client.gui.screen.Screen` that handles the boilerplate of setting up a UI root, managing the render loop, and delegating user input to Weave's `UIManager`.

---

## What this page covers

- The purpose of `WeaveScreen` and when to use it.
- How to create a minimal screen with a root `window`.
- A complete example demonstrating layout, components, and animations.
- How to open your new screen in-game.

---

## The Role of `WeaveScreen`

Use `WeaveScreen` as the base for any full-screen GUI you build. It provides a managed lifecycle so you can focus on what your UI looks like, not how it's drawn or updated.

- **Initialization:** Automatically sets up the `UIManager`.
- **Root Container:** Provides a pre-configured `window` component to which you add all other UI elements.
- **Event Handling:** Mixins ensure that `render`, `mouseClicked`, `keyPressed`, etc., are correctly forwarded to your UI tree.
- **Cleanup:** Handles resource disposal when the screen is closed.

## A Minimal Example

Every `WeaveScreen` starts with a root `window`. You compose your UI by adding children to it and defining their layout and constraints.

```java
import net.minecraft.text.Text;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.constraint.constraints.Constraints;

public final class MyScreen extends WeaveScreen {
    public MyScreen() {
        // The screen title, visible in Minecraft's UI hierarchy.
        super(Text.literal("My Screen"));

        // The root `window` is centered by default.
        // We can configure its layout and padding.
        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, 8));
        window.setPadding(10);

        // Create a simple text component and add it to the window.
        SimpleTextComponent greeting = SimpleTextComponent.of("Hello, Weave!")
            .setX(Constraints.center()) // Center horizontally within its allocated space.
            .setY(Constraints.center()); // Center vertically.

        window.addChildren(greeting);
    }
}
```

## Opening Your Screen

To display your screen, instantiate it and call the `.open()` helper method. This is safe to call from any client-side context, like a keybinding handler or command.

```java
// From your mod's code (e.g., a command or key press event)
new MyScreen().open();
```

## Building a Complete UI Tree

As your UI grows, you'll compose multiple components. The `window` acts as the single root of this tree. The following example builds a simple login form with a title, an input field, a button, and a fade-in animation.

```java
import net.minecraft.text.Text;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.animation.Easings;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.constraint.constraints.Constraints;

public final class LoginScreen extends WeaveScreen {
    public LoginScreen() {
        super(Text.literal("Login"));

        // 1. Configure the root window layout
        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 10));
        window.setPadding(12);

        // 2. Create components
        SimpleTextComponent title = SimpleTextComponent.of("Welcome")
            .setScale(1.4f)
            .setX(Constraints.center());

        TextField username = TextField.create()
            .setPlaceholder("Username");

        Button submit = Button.of("Continue")
            .onMouseClick(e -> {
                // Add a simple "bounce" animation on click
                submit.animate()
                    .duration(150).easing(Easings.EASE_OUT_BACK).scale(1.08f)
                    .then(() -> submit.animate().duration(120).scale(1.0f));
            });

        // 3. Arrange components in a container
        Panel form = Panel.create()
            .setWidth(Constraints.relative(1.0f)) // Take up 100% of parent width
            .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 8))
            .addChildren(username, submit);

        // 4. Add all top-level elements to the window
        window.addChildren(title, form);

        // 5. Add a fade-in animation when the screen opens
        window.setOpacity(0f);
        window.animate().duration(180).opacity(1f);
    }
}
```

---

**Next Step → [Components & Layout](components.md)**
