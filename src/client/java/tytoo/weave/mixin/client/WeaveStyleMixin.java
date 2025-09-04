package tytoo.weave.mixin.client;

import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tytoo.weave.mixin.interfaces.IStyle;

@Mixin(Style.class)
public class WeaveStyleMixin implements IStyle {
    @Shadow
    @Final
    @Nullable Boolean bold;
    @Shadow
    @Final
    @Nullable Boolean italic;
    @Shadow
    @Final
    @Nullable Boolean underlined;
    @Shadow
    @Final
    @Nullable Boolean strikethrough;
    @Shadow
    @Final
    @Nullable Boolean obfuscated;

    @Override
    public @Nullable Boolean weave$getBoldRaw() {
        return this.bold;
    }

    @Override
    public @Nullable Boolean weave$getItalicRaw() {
        return this.italic;
    }

    @Override
    public @Nullable Boolean weave$getUnderlinedRaw() {
        return this.underlined;
    }

    @Override
    public @Nullable Boolean weave$getStrikethroughRaw() {
        return this.strikethrough;
    }

    @Override
    public @Nullable Boolean weave$getObfuscatedRaw() {
        return this.obfuscated;
    }
}