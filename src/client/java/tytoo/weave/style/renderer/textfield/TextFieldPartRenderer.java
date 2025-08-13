package tytoo.weave.style.renderer.textfield;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.component.components.interactive.BaseTextInput;

public interface TextFieldPartRenderer {
    void render(DrawContext context, BaseTextInput<?> textInput);
}