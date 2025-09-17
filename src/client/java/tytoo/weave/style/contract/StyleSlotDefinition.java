package tytoo.weave.style.contract;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record StyleSlotDefinition(StyleSlot slot, SlotRequirement requirement,
                                  @Nullable StyleSlotDefaultProvider defaultProvider) {
    public StyleSlotDefinition(StyleSlot slot, SlotRequirement requirement, @Nullable StyleSlotDefaultProvider defaultProvider) {
        this.slot = Objects.requireNonNull(slot, "slot");
        this.requirement = Objects.requireNonNull(requirement, "requirement");
        this.defaultProvider = defaultProvider;
    }
}
