package tytoo.weave.mixin.interfaces;

import org.jetbrains.annotations.Nullable;

public interface IStyle {
    @Nullable
    Boolean weave$getBoldRaw();

    @Nullable
    Boolean weave$getItalicRaw();

    @Nullable
    Boolean weave$getUnderlinedRaw();

    @Nullable
    Boolean weave$getStrikethroughRaw();

    @Nullable
    Boolean weave$getObfuscatedRaw();
}
