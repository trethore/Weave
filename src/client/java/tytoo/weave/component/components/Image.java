package tytoo.weave.component.components;

import net.minecraft.util.Identifier;

public class Image extends BaseImage<Image> {
    public Image(Identifier imageId) {
        super(imageId);
    }

    public static Image of(Identifier imageId) {
        return new Image(imageId);
    }
}