package tytoo.weave.ui.popup;

import org.jetbrains.annotations.NotNull;
import tytoo.weave.component.Component;

public record PopupCloseEvent(@NotNull Component<?> content,
                              @NotNull Anchor anchor,
                              @NotNull PopupOptions options) {
}

