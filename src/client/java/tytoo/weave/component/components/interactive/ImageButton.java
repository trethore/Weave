package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tytoo.weave.component.components.display.BaseImage;
import tytoo.weave.component.components.display.SimpleTextComponent;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

public class ImageButton extends InteractiveComponent<ImageButton> {
    private float padding;
    private float gap;
    private BaseImage<?> image;
    private TextComponent<?> label;

    protected ImageButton() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        this.padding = stylesheet.get(this.getClass(), StyleProps.IMAGE_BUTTON_PADDING, 5f);
        this.gap = stylesheet.get(this.getClass(), StyleProps.IMAGE_BUTTON_GAP, 4f);

        this.setWidth((c, p) -> {
            float contentWidth = 0f;
            if (this.image != null) contentWidth += this.image.getMeasuredWidth();
            if (this.label != null) {
                if (this.image != null) contentWidth += this.gap;
                contentWidth += this.label.getMeasuredWidth();
            }
            return contentWidth + this.padding * 2;
        });
        this.setHeight((c, p) -> {
            float contentHeight = 0f;
            if (this.image != null) {
                contentHeight = this.image.getMeasuredHeight();
            }
            if (this.label != null) {
                contentHeight = Math.max(contentHeight, this.label.getMeasuredHeight());
            }
            return contentHeight + this.padding * 2;
        });
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

    private void updateLayout() {
        if (this.getImage() != null) {
            this.getImage().setY(Constraints.center());
            this.getImage().setX((c, pW, cW) -> {
                float contentWidth = this.getImage().getMeasuredWidth();
                if (this.getLabel() != null) {
                    contentWidth += this.getGap() + this.getLabel().getMeasuredWidth();
                }
                return (pW - contentWidth) / 2f;
            });
        }
        if (this.getLabel() != null) {
            this.getLabel().setY(Constraints.center());
            this.getLabel().setX((c, pW, cW) -> {
                float imageWidth = this.getImage() != null ? this.getImage().getMeasuredWidth() + this.getGap() : 0;
                float totalContentWidth = imageWidth + this.getLabel().getMeasuredWidth();
                float startOffset = (pW - totalContentWidth) / 2f;
                return startOffset + imageWidth;
            });
        }
    }

    public ImageButton setImageComponent(BaseImage<?> imageComponent) {
        if (this.image != null) {
            this.removeChild(this.image);
        }
        this.image = imageComponent;
        if (this.image != null) {
            this.addChild(this.image);
        }
        updateLayout();
        return this;
    }

    public ImageButton setLabelComponent(TextComponent<?> labelComponent) {
        if (this.label != null) this.removeChild(this.label);
        this.label = labelComponent;
        if (this.label != null) this.addChild(this.label);
        updateLayout();
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
        return padding;
    }

    public ImageButton setPadding(float padding) {
        this.padding = padding;
        invalidateLayout();
        return this;
    }

    public float getGap() {
        return gap;
    }

    public ImageButton setGap(float gap) {
        this.gap = gap;
        updateLayout();
        invalidateLayout();
        return this;
    }

    public static final class StyleProps {
        public static final StyleProperty<Float> IMAGE_BUTTON_PADDING = new StyleProperty<>("imageButton.padding", Float.class);
        public static final StyleProperty<Float> IMAGE_BUTTON_GAP = new StyleProperty<>("imageButton.gap", Float.class);

        private StyleProps() {
        }
    }
}