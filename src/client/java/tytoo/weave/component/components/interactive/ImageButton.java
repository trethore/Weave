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
    protected float padding;
    protected float gap;
    protected BaseImage<?> image;
    protected TextComponent<?> label;

    protected ImageButton() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        this.padding = stylesheet.get(this.getClass(), StyleProps.IMAGE_BUTTON_PADDING, 5f);
        this.gap = stylesheet.get(this.getClass(), StyleProps.IMAGE_BUTTON_GAP, 4f);

        this.setWidth((c, p) -> {
            float contentWidth = 0;
            if (image != null) contentWidth += image.getMeasuredWidth();
            if (label != null) {
                if (image != null) contentWidth += gap;
                contentWidth += label.getMeasuredWidth();
            }
            return contentWidth + padding * 2;
        });
        this.setHeight((c, p) -> {
            float contentHeight = 0;
            if (image != null) {
                contentHeight = image.getMeasuredHeight();
            }
            if (label != null) {
                contentHeight = Math.max(contentHeight, label.getMeasuredHeight());
            }
            return contentHeight + padding * 2;
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
        if (this.image != null) {
            this.image.setY(Constraints.center());
            this.image.setX((c, pW, cW) -> {
                float contentWidth = image.getMeasuredWidth();
                if (label != null) {
                    contentWidth += gap + label.getMeasuredWidth();
                }
                return (pW - contentWidth) / 2;
            });
        }
        if (this.label != null) {
            this.label.setY(Constraints.center());
            this.label.setX((c, pW, cW) -> {
                float imageWidth = image != null ? image.getMeasuredWidth() + gap : 0;
                float totalContentWidth = imageWidth + label.getMeasuredWidth();
                float startOffset = (pW - totalContentWidth) / 2;
                return startOffset + imageWidth;
            });
        }
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

    public ImageButton setLabel(Text text) {
        if (this.label == null) {
            return setLabelComponent(SimpleTextComponent.of(text));
        } else {
            this.label.setText(text);
            invalidateLayout();
        }
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

    public ImageButton setPadding(float padding) {
        this.padding = padding;
        invalidateLayout();
        return this;
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