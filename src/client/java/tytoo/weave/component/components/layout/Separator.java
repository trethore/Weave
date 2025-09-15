package tytoo.weave.component.components.layout;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.NamedPart;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;

public class Separator extends BasePanel<Separator> {
    private final Orientation orientation;
    private final float thickness;
    private final float labelGap;

    @Nullable
    @NamedPart
    private SimpleTextComponent label;
    @Nullable
    @NamedPart
    private Panel leftLine;
    @Nullable
    @NamedPart
    private Panel rightLine;

    private LabelAlignment labelAlignment = LabelAlignment.CENTER;

    protected Separator(Orientation orientation) {
        this.orientation = orientation;

        Stylesheet stylesheet = ThemeManager.getStylesheet();
        this.thickness = stylesheet.get(this, StyleProps.THICKNESS, 1f);
        this.labelGap = stylesheet.get(this, StyleProps.LABEL_GAP, 6f);

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

    public Separator setLabel(@Nullable String text) {
        if (this.orientation != Orientation.HORIZONTAL) {
            return self();
        }

        if (text == null || text.isEmpty()) {
            removeLabelStructure();
            return self();
        }

        ensureLabelStructure();
        this.label.setText(text);
        Float labelScale = ThemeManager.getStylesheet().get(this, StyleProps.LABEL_TEXT_SCALE, 1.0f);
        if (labelScale != null) {
            this.label.setScale(labelScale);
        }
        updateForAlignment();
        return self();
    }

    public Separator setLabelAlignment(LabelAlignment alignment) {
        if (alignment == null) return self();
        this.labelAlignment = alignment;
        updateForAlignment();
        return self();
    }

    public Separator withLabel(String text, LabelAlignment alignment) {
        return setLabelAlignment(alignment).setLabel(text);
    }

    private void ensureLabelStructure() {
        if (this.label != null) return;

        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.START, LinearLayout.CrossAxisAlignment.CENTER, labelGap));
        this.setWidth(Constraints.relative(1.0f));
        this.setHeight(Constraints.childBased());

        Panel left = Panel.create();
        Panel right = Panel.create();
        SimpleTextComponent lbl = SimpleTextComponent.of("");

        this.leftLine = left;
        this.rightLine = right;
        this.label = lbl;

        this.leftLine.setHeight(Constraints.pixels(thickness));
        this.rightLine.setHeight(Constraints.pixels(thickness));

        this.leftLine.setLayoutData(LinearLayout.Data.grow(1f));
        this.rightLine.setLayoutData(LinearLayout.Data.grow(1f));
        this.label.setLayoutData(LinearLayout.Data.grow(0f));
        this.label.setHittable(false);

        Stylesheet ss = ThemeManager.getStylesheet();
        Float labelScale = ss.get(this, StyleProps.LABEL_TEXT_SCALE, 1.0f);
        if (labelScale != null) {
            this.label.setScale(labelScale);
        }

        this.addStyleClass("separator-with-label");
        this.removeAllChildren();
        this.addChildren(this.leftLine, this.label, this.rightLine);
    }

    private void removeLabelStructure() {
        if (this.label == null) return;

        this.removeAllChildren();
        this.removeStyleClass("separator-with-label");

        if (this.orientation == Orientation.HORIZONTAL) {
            this.setHeight(Constraints.pixels(thickness));
            this.setWidth(Constraints.relative(1.0f));
        } else {
            this.setWidth(Constraints.pixels(thickness));
            this.setHeight(Constraints.relative(1.0f));
        }

        this.label = null;
        this.leftLine = null;
        this.rightLine = null;
        this.setLayout(null);
    }

    private void updateForAlignment() {
        if (this.label == null || this.leftLine == null || this.rightLine == null) return;

        float smallRatio = ThemeManager.getStylesheet().get(this, StyleProps.SMALL_LINE_RATIO, 0.15f);
        if (Float.isNaN(smallRatio) || smallRatio < 0f) smallRatio = 0f;

        switch (this.labelAlignment) {
            case CENTER -> {
                this.leftLine.setVisible(true);
                this.rightLine.setVisible(true);
                this.leftLine.setLayoutData(LinearLayout.Data.grow(1f));
                this.rightLine.setLayoutData(LinearLayout.Data.grow(1f));
            }
            case LEFT -> {
                this.leftLine.setVisible(false);
                this.rightLine.setVisible(true);
                this.rightLine.setLayoutData(LinearLayout.Data.grow(1f));
            }
            case RIGHT -> {
                this.leftLine.setVisible(true);
                this.leftLine.setLayoutData(LinearLayout.Data.grow(1f));
                this.rightLine.setVisible(false);
            }
            case LEFT_WITH_LINE -> {
                this.leftLine.setVisible(true);
                this.rightLine.setVisible(true);
                this.leftLine.setLayoutData(LinearLayout.Data.grow(smallRatio));
                this.rightLine.setLayoutData(LinearLayout.Data.grow(1f));
            }
            case RIGHT_WITH_LINE -> {
                this.leftLine.setVisible(true);
                this.rightLine.setVisible(true);
                this.leftLine.setLayoutData(LinearLayout.Data.grow(1f));
                this.rightLine.setLayoutData(LinearLayout.Data.grow(smallRatio));
            }
        }
        invalidateLayout();
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum LabelAlignment {
        LEFT,
        CENTER,
        RIGHT,
        LEFT_WITH_LINE,
        RIGHT_WITH_LINE
    }

    public static final class StyleProps {
        public static final StyleSlot THICKNESS = StyleSlot.of("separator.thickness", Separator.class, Float.class);
        public static final StyleSlot LABEL_GAP = StyleSlot.of("separator.label-gap", Separator.class, Float.class);
        public static final StyleSlot LABEL_TEXT_SCALE = StyleSlot.of("separator.label-text-scale", Separator.class, Float.class);
        public static final StyleSlot COLOR = StyleSlot.of("separator.color", Separator.class, Color.class);
        public static final StyleSlot SMALL_LINE_RATIO = StyleSlot.of("separator.small-line-ratio", Separator.class, Float.class);

        private StyleProps() {
        }
    }
}
