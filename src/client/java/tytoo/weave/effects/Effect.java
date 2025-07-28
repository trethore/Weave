package tytoo.weave.effects;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.Component;

public interface Effect {

    default void beforeDraw(DrawContext context, Component<?> component) {
    }

    default void afterDraw(DrawContext context, Component<?> component) {
    }
}