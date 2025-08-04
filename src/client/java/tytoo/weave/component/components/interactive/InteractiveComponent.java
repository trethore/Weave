package tytoo.weave.component.components.interactive;

import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.event.mouse.MouseReleaseEvent;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.function.Consumer;

public abstract class InteractiveComponent<T extends InteractiveComponent<T>> extends BasePanel<T> {

    private boolean enabled = true;

    protected InteractiveComponent() {
        this.setFocusable(true);
        this.addStyleState(StyleState.NORMAL);

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
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.get(this.getClass(), StyleProps.ANIMATION_DURATION, 100L);
        updateVisualState(duration);
    }

    protected void updateVisualState(long duration) {
        var stylesheet = ThemeManager.getStylesheet();
        Color normalColor = stylesheet.get(this.getClass(), StyleProps.COLOR_NORMAL, new Color(80, 80, 80));
        Color hoveredColor = stylesheet.get(this.getClass(), StyleProps.COLOR_HOVERED, new Color(100, 100, 100));
        Color focusedColor = stylesheet.get(this.getClass(), StyleProps.COLOR_FOCUSED, new Color(120, 120, 120));
        Color activeColor = stylesheet.get(this.getClass(), StyleProps.COLOR_ACTIVE, new Color(60, 60, 60));
        Color disabledColor = stylesheet.get(this.getClass(), StyleProps.COLOR_DISABLED, new Color(50, 50, 50, 150));

        Color targetColor;
        if (!isEnabled()) {
            targetColor = disabledColor;
        } else if (this.getActiveStyleStates().contains(StyleState.ACTIVE)) {
            targetColor = activeColor;
        } else if (isFocused()) {
            targetColor = focusedColor;
        } else if (isHovered()) {
            targetColor = hoveredColor;
        } else {
            targetColor = normalColor;
        }

        this.animate().duration(duration).color(targetColor);
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
        updateVisualState();
        return self();
    }

    public static final class StyleProps {
        public static final StyleProperty<Long> ANIMATION_DURATION = new StyleProperty<>("animation.duration", Long.class);
        public static final StyleProperty<Color> COLOR_NORMAL = new StyleProperty<>("color.normal", Color.class);
        public static final StyleProperty<Color> COLOR_HOVERED = new StyleProperty<>("color.hovered", Color.class);
        public static final StyleProperty<Color> COLOR_FOCUSED = new StyleProperty<>("color.focused", Color.class);
        public static final StyleProperty<Color> COLOR_ACTIVE = new StyleProperty<>("color.active", Color.class);
        public static final StyleProperty<Color> COLOR_DISABLED = new StyleProperty<>("color.disabled", Color.class);

        private StyleProps() {
        }
    }
}