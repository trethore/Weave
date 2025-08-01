package tytoo.weave.component.components.interactive;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tytoo.weave.component.components.display.BaseImage;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;

public class ImageButton extends InteractiveComponent<ImageButton> {
    private final float padding = 5;
    private final float gap = 4;
    protected BaseImage<?> image;
    protected TextComponent label;

    protected ImageButton() {
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

    @Override
    protected void updateVisualState() {
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.getProperty(this.getClass(), "animation.duration", 150L);

        Color normalColor = stylesheet.getProperty(this.getClass(), "color.normal", new Color(100, 100, 100, 180));
        Color hoveredColor = stylesheet.getProperty(this.getClass(), "color.hovered", new Color(120, 120, 120, 180));
        Color focusedColor = stylesheet.getProperty(this.getClass(), "color.focused", new Color(140, 140, 140, 180));

        Color targetColor = isFocused() ? focusedColor : (isHovered() ? hoveredColor : normalColor);

        this.animate().duration(duration).color(targetColor);
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
        if (this.image != null) this.removeChild(this.image);
        this.image = BaseImage.of(imageId)
                .setWidth(Constraints.pixels(16))
                .setHeight(Constraints.pixels(16));
        this.addChild(this.image);
        updateLayout();
        return this;
    }

    public ImageButton setLabel(Text text) {
        if (this.label != null) this.removeChild(this.label);
        this.label = TextComponent.of(text);
        this.addChild(this.label);
        updateLayout();
        return this;
    }

    public ImageButton setImageSize(float width, float height) {
        this.image.setWidth(Constraints.pixels(width));
        this.image.setHeight(Constraints.pixels(height));
        invalidateLayout();
        return this;
    }
}