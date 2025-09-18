package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.EdgeInsets;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.contract.ComponentStyleProperties;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

public class Button extends InteractiveComponent<Button> {

    protected Button() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float minWidth = stylesheet.get(this, ComponentStyleProperties.ButtonStyles.MIN_WIDTH, 20f);
        float minHeight = stylesheet.get(this, ComponentStyleProperties.ButtonStyles.MIN_HEIGHT, 20f);
        float padding = stylesheet.get(this, ComponentStyleProperties.ButtonStyles.PADDING, 5f);

        this.setWidth(Constraints.childBased(padding));
        this.setHeight(Constraints.childBased(padding));
        this.setMinWidth(minWidth);
        this.setMinHeight(minHeight);
        this.setPadding(padding);

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

    @Override
    protected void applyComponentStylesFromStylesheet() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();

        Float padding = stylesheet.get(this, ComponentStyleProperties.ButtonStyles.PADDING, null);
        if (padding != null) {
            this.getLayoutState().setPadding(new EdgeInsets(padding));
        }

        Float minWidth = stylesheet.get(this, ComponentStyleProperties.ButtonStyles.MIN_WIDTH, null);
        if (minWidth != null) this.getConstraints().setMinWidth(minWidth);

        Float minHeight = stylesheet.get(this, ComponentStyleProperties.ButtonStyles.MIN_HEIGHT, null);
        if (minHeight != null) this.getConstraints().setMinHeight(minHeight);
    }
}
