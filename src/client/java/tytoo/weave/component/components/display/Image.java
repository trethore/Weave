package tytoo.weave.component.components.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.constraint.HeightConstraint;
import tytoo.weave.constraint.WidthConstraint;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.Styling;
import tytoo.weave.utils.ImageManager;

import java.awt.*;
import java.io.File;
import java.net.URL;

public class Image extends Component<Image> {
    private BaseImage<?> imagePart;
    private TextComponent labelPart;
    private float gap = 2;

    public Image(Identifier imageId) {
        this.imagePart = new BaseImage<>(imageId)
                .setX(Constraints.center())
                .setY(Constraints.pixels(0))
                .setWidth(Constraints.pixels(16))
                .setHeight(Constraints.pixels(16))
                .setParent(this);

        super.setWidth(c -> {
            float imageW = imagePart.getWidth();
            float labelW = labelPart != null ? labelPart.getWidth() : 0;
            return Math.max(imageW, labelW);
        });

        super.setHeight(c -> {
            float imageH = imagePart.getHeight();
            if (labelPart == null) return imageH;
            return imageH + gap + labelPart.getHeight();
        });
    }

    public static Image from(Identifier imageId) {
        return new Image(imageId);
    }

    public static Image from(File file) {
        return ImageManager.getIdentifierForFile(file)
                .map(Image::from)
                .orElseGet(() -> Image.from(ImageManager.getPlaceholder()).setColor(new Color(128, 0, 128)));
    }

    public static Image from(URL url) {
        Image image = Image.from(ImageManager.getPlaceholder()).setColor(new Color(128, 0, 128));
        ImageManager.getIdentifierForUrl(url)
                .thenAccept(id -> image.setImage(id).setColor(Color.WHITE))
                .exceptionally(t -> {
                    image.setColor(new Color(200, 0, 0));
                    return null;
                });
        return image;
    }

    @Override
    protected void updateClonedChildReferences(Component<Image> original) {
        super.updateClonedChildReferences(original);
        Image originalImage = (Image) original;

        if (originalImage.imagePart != null) {
            int imageIndex = originalImage.getChildren().indexOf(originalImage.imagePart);
            if (imageIndex != -1) {
                this.imagePart = (BaseImage<?>) this.getChildren().get(imageIndex);
            }
        }

        if (originalImage.labelPart != null) {
            int labelIndex = originalImage.getChildren().indexOf(originalImage.labelPart);
            if (labelIndex != -1) {
                this.labelPart = (TextComponent) this.getChildren().get(labelIndex);
            }
        }
    }

    @Override
    public void draw(DrawContext context) {
        drawChildren(context);
    }

    @Override
    public Image setWidth(WidthConstraint constraint) {
        this.imagePart.setWidth(constraint);
        return this;
    }

    @Override
    public Image setHeight(HeightConstraint constraint) {
        this.imagePart.setHeight(constraint);
        return this;
    }

    public Image setLabel(String text, Styling style) {
        return setLabel(TextComponent.of(text).setStyle(style));
    }

    public Image setLabel(Text text, Styling style) {
        return setLabel(TextComponent.of(text).setStyle(style));
    }

    public Image clearLabel() {
        return setLabel((TextComponent) null);
    }

    public Image setLabelScale(float scale) {
        if (this.labelPart != null) {
            this.labelPart.setScale(scale);
        }
        return this;
    }

    @Nullable
    public TextComponent getLabel() {
        return this.labelPart;
    }

    public Image setLabel(TextComponent label) {
        if (this.labelPart != null) {
            this.removeChild(this.labelPart);
        }
        this.labelPart = label;
        if (this.labelPart != null) {
            this.labelPart.setX(Constraints.center());
            this.labelPart.setY(Constraints.sibling(gap));
            this.labelPart.setParent(this);
        }
        return this;
    }

    public Image setLabel(String text) {
        return setLabel(TextComponent.of(text));
    }

    public Image setLabel(Text text) {
        return setLabel(TextComponent.of(text));
    }

    public Image setGap(float gap) {
        this.gap = gap;
        return this;
    }

    public Image setColor(Color color) {
        this.imagePart.setColor(color);
        return this;
    }

    public Image setRotation(int rotation) {
        this.imagePart.setRotation(rotation);
        return this;
    }

    public Image setParity(boolean parity) {
        this.imagePart.setParity(parity);
        return this;
    }

    public Image setImage(Identifier imageId) {
        this.imagePart.setImage(imageId);
        return this;
    }
}