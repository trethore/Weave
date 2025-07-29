package tytoo.weave.layout;

import tytoo.weave.component.Component;

public interface Layout {
    void arrangeChildren(Component<?> parent);
}