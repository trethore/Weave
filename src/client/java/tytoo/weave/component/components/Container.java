package tytoo.weave.component.components;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

public class Container extends Component<Container> {
    public static Container of(Component<?>... components) {
        return new Container().addChildren(components);
    }

    @Override
    public void draw(DrawContext context) {
        for (Component<?> child : children) {
            child.draw(context);
        }
    }

    public Container addChildren(Component<?>... components) {
        for (Component<?> component : components) {
            this.addChild(component);
        }
        return this;
    }
}