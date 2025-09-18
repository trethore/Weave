package tytoo.weave.component.components.interactive;

import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.event.mouse.MouseReleaseEvent;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.contract.ComponentStyleProperties;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

import java.util.function.Consumer;

public abstract class InteractiveComponent<T extends InteractiveComponent<T>> extends BasePanel<T> {

    private boolean enabled = true;


    protected InteractiveComponent() {
        this.setFocusable(true);

        this.onEvent(e -> {
            if (!isEnabled() && !(e instanceof tytoo.weave.event.focus.FocusLostEvent)) {
                e.cancel();
            }
        });

        this.onMouseEnter(e -> {
            setStyleState(StyleState.HOVERED, true);
            updateVisualState();
        });
        this.onMouseLeave(e -> {
            setStyleState(StyleState.HOVERED, false);
            updateVisualState();
        });
        this.onFocusGained(e -> {
            setStyleState(StyleState.FOCUSED, true);
            updateVisualState();
        });
        this.onFocusLost(e -> {
            setStyleState(StyleState.FOCUSED, false);
            setStyleState(StyleState.ACTIVE, false);
            updateVisualState();
        });

        updateVisualState(0L);
    }

    @Override
    protected void onStyleStateChanged() {
        super.onStyleStateChanged();
        updateVisualState();
    }

    protected void updateVisualState() {
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.get(this, ComponentStyleProperties.InteractiveStyles.ANIMATION_DURATION, 100L);
        updateVisualState(duration);
    }

    protected void updateVisualState(long duration) {
        // This method is now a hook for subclasses. The actual visual
        // change is handled by the stylesheet resolving the correct
        // renderer for the component's current state.
        // We keep the call to ensure components depending on it still work,
        // but the base implementation is now empty.
    }

    public T onClick(Consumer<T> action) {
        if (action != null) {
            this.onEvent(MouseReleaseEvent.TYPE, e -> {
                if (e.getButton() == 0) action.accept(self());
            });
        }
        return self();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public T setEnabled(boolean enabled) {
        this.enabled = enabled;
        setFocusable(enabled);
        setStyleState(StyleState.DISABLED, !enabled);
        if (!enabled) {
            setStyleState(StyleState.HOVERED, false);
            setStyleState(StyleState.FOCUSED, false);
            setStyleState(StyleState.ACTIVE, false);
        }
        applyDisabledToDescendantTexts(this, !enabled);
        updateVisualState();
        return self();
    }

    private void applyDisabledToDescendantTexts(Component<?> component, boolean disabled) {
        if (component instanceof TextComponent<?> textComponent) {
            textComponent.setStyleState(StyleState.DISABLED, disabled);
        }
        for (Component<?> child : component.getChildren()) {
            applyDisabledToDescendantTexts(child, disabled);
        }
    }
}
