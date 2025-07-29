package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.BaseImage;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.style.ComponentState;

import java.awt.*;
import java.util.function.Consumer;

public class ImageButton extends BasePanel<ImageButton> {
    private final float padding = 5;
    protected BaseImage<?> image;
    protected TextComponent label;
    private float gap = 4;

    public ImageButton(Identifier imageId) {
        this(imageId, (Text) null);
    }

    public ImageButton(Identifier imageId, String text) {
        this(imageId, Text.of(text));
    }

    public ImageButton(Identifier imageId, Text text) {
        this.setFocusable(true);

        this.image = new BaseImage<>(imageId)
                .setY(Constraints.center())
                .setWidth(Constraints.pixels(16))
                .setHeight(Constraints.pixels(16))
                .setParent(this);

        if (text != null) {
            this.label = TextComponent.of(text)
                    .setY(Constraints.center())
                    .setParent(this);
        }

        this.image.setX((c, pW, cW) -> {
            float contentWidth = image.getMeasuredWidth();
            if (label != null) {
                contentWidth += gap + label.getMeasuredWidth();
            }
            return (pW - contentWidth) / 2;
        });

        if (this.label != null) {
            this.label.setX((c, pW, cW) -> image.getMeasuredWidth() + gap);
        }

        this.setWidth((c, p) -> {
            float contentWidth = image.getMeasuredWidth();
            if (label != null) {
                contentWidth += gap + label.getMeasuredWidth();
            }
            return contentWidth + padding * 2;
        });
        this.setHeight((c, p) -> {
            float contentHeight = image.getMeasuredHeight();
            if (label != null) {
                contentHeight = Math.max(contentHeight, label.getMeasuredHeight());
            }
            return contentHeight + padding * 2;
        });

        this.style.setColor(ComponentState.NORMAL, new Color(100, 100, 100));
        this.style.setColor(ComponentState.HOVERED, new Color(120, 120, 120));
        this.style.setColor(ComponentState.FOCUSED, new Color(120, 120, 120).brighter());
    }

    public static ImageButton of(Identifier imageId) {
        return new ImageButton(imageId);
    }

    public static ImageButton of(Identifier imageId, String text) {
        return new ImageButton(imageId, text);
    }

    public static ImageButton of(Identifier imageId, Text text) {
        return new ImageButton(imageId, text);
    }

    @Override
    protected void updateClonedChildReferences(Component<ImageButton> original) {
        super.updateClonedChildReferences(original);
        ImageButton originalButton = (ImageButton) original;

        if (originalButton.image != null) {
            int imageIndex = originalButton.getChildren().indexOf(originalButton.image);
            if (imageIndex != -1) {
                this.image = (BaseImage<?>) this.getChildren().get(imageIndex);
            }
        }

        if (originalButton.label != null) {
            int labelIndex = originalButton.getChildren().indexOf(originalButton.label);
            if (labelIndex != -1) {
                this.label = (TextComponent) this.getChildren().get(labelIndex);
            }
        }
    }

    public ImageButton onClick(Consumer<ImageButton> action) {
        this.onMouseClick(e -> {
            if (e.getButton() == 0) action.accept(this);
        });
        return this;
    }

    public ImageButton setImageSize(float width, float height) {
        this.image.setWidth(Constraints.pixels(width));
        this.image.setHeight(Constraints.pixels(height));
        return this;
    }

    public ImageButton setGap(float gap) {
        this.gap = gap;
        return this;
    }
}