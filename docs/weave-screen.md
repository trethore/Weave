# Creating a WeaveScreen

`WeaveScreen` is a convenience base that wires Weave’s UI tree into a Minecraft `Screen` via mixins. It manages a root `Window` component and delegates input/render events through `UIManager`.

When to use it
- Use `WeaveScreen` whenever you want a full-screen UI powered by Weave. It provides a managed lifecycle (init → input → layout → render) and a ready-to-use root `window` container so you can focus on composing components and styles rather than plumbing events and sizing by hand.

Minimal screen

```
import net.minecraft.text.Text;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.constraint.constraints.Constraints;

public final class MyScreen extends WeaveScreen {
    public MyScreen() {
        super(Text.literal("My Screen"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 8));
        window.setPadding(10);

        Panel header = Panel.create()
            .setWidth(Constraints.relative(1.0f))
            .setHeight(Constraints.pixels(28));

        header.addChildren(
            SimpleTextComponent.of("Hello Weave").setX(Constraints.center()).setY(Constraints.center())
        );

        window.addChildren(header);
    }
}
```

Opening a Weave screen

- From code: `new MyScreen().open();`
- From a client command (in dev): `/weave testgui` opens the built-in demo.

Root `Window`
- Centered by default with width/height from the active theme.
- Acts as the container for your layout tree; add children to `window`.

Constraints and layout
- Use `Constraints.pixels`, `Constraints.relative`, and `Constraints.center` to size/position children.
- Set a layout on containers (`LinearLayout`, `GridLayout`) to auto-arrange managed children.

Full example with input, button, and animation

```
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

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 10));
        window.setPadding(12);

        SimpleTextComponent title = SimpleTextComponent.of("Welcome")
            .setScale(1.4f)
            .setX(Constraints.center());

        TextField username = TextField.create()
            .setPlaceholder("Username");

        Button submit = Button.of("Continue")
            .onMouseClick(e -> submit.animate().duration(150).easing(Easings.EASE_OUT_BACK).scale(1.08f).then(() -> submit.animate().duration(120).scale(1.0f)));

        Panel form = Panel.create()
            .setWidth(Constraints.relative(1.0f))
            .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 8))
            .addChildren(username, submit);

        window.addChildren(title, form);

        window.setOpacity(0f);
        window.animate().duration(180).opacity(1f);
    }
}
```

Next Step: [Components & Layout](https://github.com/trethore/Weave/blob/main/docs/components.md)
