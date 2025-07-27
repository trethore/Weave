package tytoo.weave.component.components.layout;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

public class Container extends Component<Container> {
    public static Container of(Component<?>... components) {
        return new Container().addChildren(components);
    }

    @Override
    public void draw(DrawContext context) {
        drawChildren(context);
    }
}