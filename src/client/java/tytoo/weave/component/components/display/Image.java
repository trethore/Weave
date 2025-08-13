package tytoo.weave.component.components.display;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.WeaveClient;
import tytoo.weave.component.NamedPart;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.layout.LinearLayout.Alignment;
import tytoo.weave.layout.LinearLayout.CrossAxisAlignment;
import tytoo.weave.layout.LinearLayout.Orientation;
import tytoo.weave.style.Styling;
import tytoo.weave.utils.ImageManager;
import tytoo.weave.utils.McUtils;

import java.awt.*;
import java.io.File;
import java.net.URL;

public class Image extends BasePanel<Image> {
    @NamedPart
    private final BaseImage<?> imagePart;
    @NamedPart
    private TextComponent<?> labelPart;
    private float gap = 2;

    public Image(BaseImage<?> imagePart) {
        this.setLayout(LinearLayout.of(Orientation.VERTICAL, Alignment.CENTER, CrossAxisAlignment.STRETCH, gap));
        this.setHeight(Constraints.childBased());
        this.imagePart = imagePart;
        this.imagePart.setLayoutData(LinearLayout.Data.grow(1));
        this.addChild(this.imagePart);
    }

    public static Image from(Identifier imageId) {
        return new Image(BaseImage.of(imageId));
    }

    public static Image from(File file) {
        return ImageManager.getIdentifierForFile(file)
                .map(Image::from)
                .orElseGet(() -> Image.from(ImageManager.getPlaceholder()).setColor(new Color(128, 0, 128)));
    }

    public static Image from(URL url) {
        Image image = Image.from(ImageManager.getPlaceholder()).setColor(Color.WHITE);
        ImageManager.getIdentifierForUrl(url).whenCompleteAsync((id, throwable) -> {
            if (throwable != null) {
                WeaveClient.LOGGER.error("Failed to load image from URL {}.", url, throwable);
                image.setImage(ImageManager.getPlaceholder()).setColor(Color.WHITE);
            } else {
                image.setImage(id).setColor(Color.WHITE);
                WeaveClient.LOGGER.info("Loaded image from URL {} with ID {}", url, id);
            }
        }, McUtils.getMc().orElseThrow());
        return image;
    }

    @Nullable
    public BaseImage<?> getImagePart() {
        return this.imagePart;
    }

    public Image setLabel(String text, Styling style) {
        return setLabel(SimpleTextComponent.of(text).setStyle(style));
    }

    public Image setLabel(Text text, Styling style) {
        return setLabel(SimpleTextComponent.of(text).setStyle(style));
    }

    public Image clearLabel() {
        return setLabel((TextComponent<?>) null);
    }

    public Image setLabelScale(float scale) {
        if (this.labelPart != null) {
            this.labelPart.setScale(scale);
        }
        return this;
    }

    @Nullable
    public TextComponent<?> getLabel() {
        return this.labelPart;
    }

    public Image setLabel(TextComponent<?> label) {
        if (this.labelPart != null) {
            this.removeChild(this.labelPart);
        }
        this.labelPart = label;
        if (this.labelPart != null) {
            this.addChild(this.labelPart);
        }
        return this;
    }

    public Image setLabel(String text) {
        return setLabel(SimpleTextComponent.of(text));
    }

    public Image setLabel(Text text) {
        return setLabel(SimpleTextComponent.of(text));
    }

    public Image setGap(float gap) {
        this.gap = gap;
        this.setLayout(LinearLayout.of(Orientation.VERTICAL, Alignment.CENTER, CrossAxisAlignment.STRETCH, gap));
        return this;
    }

    public Image setColor(Color color) {
        this.imagePart.setColor(color);
        return this;
    }

    public Image setImageRotation(int rotation) {
        this.imagePart.setImageRotation(rotation);
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