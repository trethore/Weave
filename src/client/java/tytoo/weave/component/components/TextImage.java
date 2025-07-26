package tytoo.weave.component.components;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tytoo.weave.constraint.constraints.Constraints;

public class TextImage extends Container {
    protected Image image;
    protected TextComponent text;
    private float gap = 4;

    public TextImage(Identifier imageId, String textContent) {
        this(imageId, Text.of(textContent));
    }

    public TextImage(Identifier imageId, Text textContent) {
        this.image = Image.of(imageId)
                .setY(Constraints.center())
                .setX(Constraints.pixels(0))
                .setWidth(Constraints.pixels(16))
                .setHeight(Constraints.pixels(16))
                .setParent(this);

        this.text = TextComponent.of(textContent)
                .setY(Constraints.center())
                .setX(c -> image.getLeft() + image.getWidth() + gap)
                .setParent(this);

        this.setWidth(c -> image.getWidth() + gap + text.getWidth());
        this.setHeight(c -> Math.max(image.getHeight(), text.getHeight()));
    }

    public static TextImage of(Identifier imageId, String text) {
        return new TextImage(imageId, text);
    }

    public static TextImage of(Identifier imageId, Text text) {
        return new TextImage(imageId, text);
    }

    public TextImage setText(Text text) {
        this.text.setText(text);
        return this;
    }

    public TextImage setImage(Identifier imageId) {
        this.image.setImage(imageId);
        return this;
    }

    public TextImage setGap(float gap) {
        this.gap = gap;
        return this;
    }
}