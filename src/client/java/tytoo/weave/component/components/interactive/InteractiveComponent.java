package tytoo.weave.component.components.interactive;

import tytoo.weave.component.components.layout.BasePanel;

import java.util.function.Consumer;

public abstract class InteractiveComponent<T extends InteractiveComponent<T>> extends BasePanel<T> {

    protected InteractiveComponent() {
        this.setFocusable(true);

        this.onMouseEnter(e -> updateVisualState());
        this.onMouseLeave(e -> updateVisualState());
        this.onFocusGained(e -> updateVisualState());
        this.onFocusLost(e -> updateVisualState());
    }

    protected abstract void updateVisualState();

    public T onClick(Consumer<T> action) {
        if (action != null) {
            this.onMouseClick(e -> {
                if (e.getButton() == 0) action.accept(self());
            });
        }
        return self();
    }
}