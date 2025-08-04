package tytoo.weave.component.components.display;

import net.minecraft.text.Text;

public final class SimpleTextComponent extends TextComponent<SimpleTextComponent> {
    private SimpleTextComponent(Text text) {
        super(text);
    }

    public static SimpleTextComponent of(String text) {
        return new SimpleTextComponent(Text.of(text));
    }

    public static SimpleTextComponent of(Text text) {
        return new SimpleTextComponent(Text.of(text));
    }
}
