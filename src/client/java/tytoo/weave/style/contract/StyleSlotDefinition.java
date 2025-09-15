package tytoo.weave.style.contract;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class StyleSlotDefinition {
    private final StyleSlot slot;
    private final SlotRequirement requirement;
    @Nullable
    private final StyleSlotDefaultProvider defaultProvider;

    public StyleSlotDefinition(StyleSlot slot, SlotRequirement requirement, @Nullable StyleSlotDefaultProvider defaultProvider) {
        this.slot = Objects.requireNonNull(slot, "slot");
        this.requirement = Objects.requireNonNull(requirement, "requirement");
        this.defaultProvider = defaultProvider;
    }

    public StyleSlot slot() {
        return slot;
    }

    public SlotRequirement requirement() {
        return requirement;
    }

    public @Nullable StyleSlotDefaultProvider defaultProvider() {
        return defaultProvider;
    }
}
