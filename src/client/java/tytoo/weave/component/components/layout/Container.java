package tytoo.weave.component.components.layout;

import tytoo.weave.component.Component;

public class Container extends BasePanel<Container> {
    public static Container of(Component<?>... components) {
        return new Container().addChildren(components);
    }
}