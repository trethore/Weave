package tytoo.weave.style.contract;

import tytoo.weave.component.Component;

@FunctionalInterface
public interface StyleSlotDefaultProvider {
    Object provide(Component<?> component);
}
