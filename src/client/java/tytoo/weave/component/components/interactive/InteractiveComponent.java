package tytoo.weave.component.components.interactive;

import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
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

    protected void updateVisualState() {
        var stylesheet = ThemeManager.getStylesheet();
        long duration = stylesheet.get(this.getClass(), StyleProps.ANIMATION_DURATION, 150L);

        Color normalColor = stylesheet.get(this.getClass(), StyleProps.COLOR_NORMAL, new Color(80, 80, 80));
        Color hoveredColor = stylesheet.get(this.getClass(), StyleProps.COLOR_HOVERED, new Color(100, 100, 100));
        Color focusedColor = stylesheet.get(this.getClass(), StyleProps.COLOR_FOCUSED, new Color(120, 120, 120));

        Color targetColor = isFocused() ? focusedColor : (isHovered() ? hoveredColor : normalColor);

        this.animate().duration(duration).color(targetColor);
    }

    public T onClick(Consumer<T> action) {
        if (action != null) {
            this.onMouseClick(e -> {
                if (e.getButton() == 0) action.accept(self());
            });
        }
        return self();
    }

    public static final class StyleProps {
        public static final StyleProperty<Long> ANIMATION_DURATION = new StyleProperty<>("animation.duration", Long.class);
        public static final StyleProperty<Color> COLOR_NORMAL = new StyleProperty<>("color.normal", Color.class);
        public static final StyleProperty<Color> COLOR_HOVERED = new StyleProperty<>("color.hovered", Color.class);
        public static final StyleProperty<Color> COLOR_FOCUSED = new StyleProperty<>("color.focused", Color.class);

        private StyleProps() {
        }
    }
}