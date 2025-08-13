package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.StyleState;

public class Button extends InteractiveComponent<Button> {

    protected Button() {
        this.setWidth(Constraints.childBased(10));
        this.setHeight(Constraints.childBased(10));
        this.addStyleState(StyleState.NORMAL);
        this.addStyleClass("interactive-visual");
    }

    public static Button create() {
        return new Button();
    }

    public static Button of(String text) {
        return new Button().addChildren(SimpleTextComponent.of(text).setX(Constraints.center()).setY(Constraints.center()).setHittable(false));
    }

    public static Button of(Text text) {
        return new Button().addChildren(SimpleTextComponent.of(text).setX(Constraints.center()).setY(Constraints.center()).setHittable(false));
    }
}