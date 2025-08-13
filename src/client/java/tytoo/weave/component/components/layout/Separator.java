package tytoo.weave.component.components.layout;

import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;

public class Separator extends BasePanel<Separator> {
    protected Separator(Orientation orientation) {
        var stylesheet = ThemeManager.getStylesheet();
        float thickness = stylesheet.get(this, StyleProps.THICKNESS, 1f);

        if (orientation == Orientation.HORIZONTAL) {
            this.setHeight(Constraints.pixels(thickness));
            this.setWidth(Constraints.relative(1.0f));
        } else {
            this.setWidth(Constraints.pixels(thickness));
            this.setHeight(Constraints.relative(1.0f));
        }
    }

    public static Separator horizontal() {
        return new Separator(Orientation.HORIZONTAL);
    }

    public static Separator vertical() {
        return new Separator(Orientation.VERTICAL);
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> THICKNESS = new StyleProperty<>("separator.thickness", Float.class);

        private StyleProps() {
        }
    }
}