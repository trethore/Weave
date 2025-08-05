package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tytoo.weave.component.components.display.BaseImage;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;

public class ImageButton extends InteractiveComponent<ImageButton> {
    private BaseImage<?> image;
    private TextComponent<?> label;

    protected ImageButton() {
        var stylesheet = ThemeManager.getStylesheet();
        float padding = stylesheet.get(this, StyleProps.IMAGE_BUTTON_PADDING, 5f);
        float gap = stylesheet.get(this, StyleProps.IMAGE_BUTTON_GAP, 4f);
        this.setPadding(padding);
        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, gap));
    }

    public static ImageButton of(Identifier imageId) {
        return new ImageButton().setImage(imageId);
    }

    public static ImageButton of(Identifier imageId, String text) {
        return new ImageButton().setImage(imageId).setLabel(Text.of(text));
    }

    public static ImageButton of(Identifier imageId, Text text) {
        return new ImageButton().setImage(imageId).setLabel(text);
    }

    public ImageButton setImageComponent(BaseImage<?> imageComponent) {
        if (this.image != null) {
            this.removeChild(this.image);
        }
        this.image = imageComponent;
        if (this.image != null) {
            int labelIndex = this.label != null ? this.getChildren().indexOf(this.label) : -1;
            if (labelIndex != -1) {
                this.children.add(labelIndex, this.image);
            } else {
                this.addChild(this.image);
            }
        }
        return this;
    }

    public ImageButton setLabelComponent(TextComponent<?> labelComponent) {
        if (this.label != null) this.removeChild(this.label);
        this.label = labelComponent;
        if (this.label != null) this.addChild(this.label);
        return this;
    }

    public ImageButton setImageSize(float width, float height) {
        this.image.setWidth(Constraints.pixels(width));
        this.image.setHeight(Constraints.pixels(height));
        invalidateLayout();
        return this;
    }

    public BaseImage<?> getImage() {
        return image;
    }

    public ImageButton setImage(Identifier imageId) {
        if (this.image == null) {
            return setImageComponent(BaseImage.of(imageId)
                    .setWidth(Constraints.pixels(16))
                    .setHeight(Constraints.pixels(16)));
        } else {
            this.image.setImage(imageId);
            invalidateLayout();
        }
        return this;
    }

    public TextComponent<?> getLabel() {
        return label;
    }

    public ImageButton setLabel(Text text) {
        if (this.label == null) {
            return setLabelComponent(SimpleTextComponent.of(text));
        } else {
            this.label.setText(text);
            invalidateLayout();
        }
        return this;
    }

    public float getPadding() {
        return this.getMargin().top();
    }

    public ImageButton setPadding(float padding) {
        super.setPadding(padding);
        return this;
    }

    public float getGap() {
        return this.getLayout() instanceof LinearLayout linearLayout ? linearLayout.getGap() : 0;
    }

    public ImageButton setGap(float gap) {
        this.setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER, LinearLayout.CrossAxisAlignment.CENTER, gap));
        return this;
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> IMAGE_BUTTON_PADDING = new StyleProperty<>("imageButton.padding", Float.class);
        public static final StyleProperty<Float> IMAGE_BUTTON_GAP = new StyleProperty<>("imageButton.gap", Float.class);

        private StyleProps() {
        }
    }
}