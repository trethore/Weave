package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.constraint.constraints.Constraints;

public class Button extends InteractiveComponent<Button> {

    protected Button() {
        this.setWidth(Constraints.childBased(10));
        this.setHeight(Constraints.childBased(10));
    }

    public static Button create() {
        return new Button();
    }

    public static Button of(String text) {
        return new Button().addChildren(TextComponent.of(text).setX(Constraints.center()).setY(Constraints.center()));
    }

    public static Button of(Text text) {
        return new Button().addChildren(TextComponent.of(text).setX(Constraints.center()).setY(Constraints.center()));
    }
}