package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;

import java.awt.*;
import java.util.function.Consumer;

public class Button extends BasePanel<Button> {
    protected TextComponent label;

    public Button(String text) {
        this(Text.of(text));
    }

    public Button(Text text) {
        this.setFocusable(true);

        this.label = TextComponent.of(text)
                .setX(Constraints.center())
                .setY(Constraints.center())
                .setParent(this);

        this.setWidth(Constraints.childBased(10));
        this.setHeight(Constraints.childBased(10));

        this.setNormalColor(new Color(100, 100, 100));
        this.setHoveredColor(new Color(120, 120, 120));
        this.setFocusedColor(new Color(120, 120, 120).brighter());
    }

    public static Button of(String text) {
        return new Button(text);
    }

    public static Button of(Text text) {
        return new Button(text);
    }

    public Button setText(Text text) {
        this.label.setText(text);
        return this;
    }

    public Button onClick(Consumer<Button> action) {
        this.onMouseClick(e -> {
            if (e.getButton() == 0) action.accept(this);
        });
        return this;
    }
}