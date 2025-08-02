package tytoo.weave.component.components.interactive;

import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.style.StyleState;

import java.util.function.Consumer;

public abstract class InteractiveComponent<T extends InteractiveComponent<T>> extends BasePanel<T> {

    protected InteractiveComponent() {
        this.setFocusable(true);
        this.addStyleState(StyleState.NORMAL);

        this.onMouseEnter(e -> {
            addStyleState(StyleState.HOVERED);
            updateVisualState();
        });
        this.onMouseLeave(e -> {
            removeStyleState(StyleState.HOVERED);
            updateVisualState();
        });
        this.onFocusGained(e -> {
            addStyleState(StyleState.FOCUSED);
            updateVisualState();
        });
        this.onFocusLost(e -> {
            removeStyleState(StyleState.FOCUSED);
            updateVisualState();
        });
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