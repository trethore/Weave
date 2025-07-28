package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.ComponentState;

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

        this.style.setColor(ComponentState.NORMAL, new Color(100, 100, 100));
        this.style.setColor(ComponentState.HOVERED, new Color(120, 120, 120));
        this.style.setColor(ComponentState.FOCUSED, new Color(120, 120, 120).brighter());
    }

    public static Button of(String text) {
        return new Button(text);
    }

    public static Button of(Text text) {
        return new Button(text);
    }

    @Override
    protected void updateClonedChildReferences(Component<Button> original) {
        super.updateClonedChildReferences(original);
        Button originalButton = (Button) original;
        if (originalButton.label != null) {
            int labelIndex = originalButton.getChildren().indexOf(originalButton.label);
            if (labelIndex != -1) {
                this.label = (TextComponent) this.getChildren().get(labelIndex);
            }
        }
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