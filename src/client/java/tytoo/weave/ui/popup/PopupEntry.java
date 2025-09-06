package tytoo.weave.ui.popup;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.layout.Panel;

public record PopupEntry(@Nullable Panel backdrop, Panel mount, Component<?> content, Anchor anchor,
                         PopupOptions options, @Nullable Component<?> priorFocus) {
}

