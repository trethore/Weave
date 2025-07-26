package tytoo.weave.component.components.interactive;

import net.minecraft.util.Identifier;
import tytoo.weave.component.components.display.BaseImage;

import java.awt.*;
import java.util.function.Consumer;

public class ImageButton extends BaseImage<ImageButton> {

    public ImageButton(Identifier imageId) {
        super(imageId);
        this.setFocusable(true);

        final Color normalColor = Color.WHITE;
        final Color hoverColor = new Color(220, 220, 220);

        this.onMouseEnter(e -> {
            if (!isFocused()) setColor(hoverColor);
        });

        this.onMouseLeave(e -> {
            if (!isFocused()) setColor(normalColor);
        });

        this.onFocusGained(e -> setColor(hoverColor.darker()));
        this.onFocusLost(e -> {
            if (isHovered()) {
                setColor(hoverColor);
            } else {
                setColor(normalColor);
            }
        });
    }

    public static ImageButton of(Identifier imageId) {
        return new ImageButton(imageId);
    }

    public ImageButton onClick(Consumer<ImageButton> action) {
        this.onMouseClick(e -> {
            if (e.getButton() == 0) action.accept(this);
        });
        return this;
    }
}