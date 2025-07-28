package tytoo.weave.component.components.layout;

import tytoo.weave.component.Component;
import tytoo.weave.style.ComponentState;

import java.awt.*;

public class BasePanel<T extends BasePanel<T>> extends Component<T> {
    public BasePanel() {
    }

    @SuppressWarnings("unchecked")
    public T setColor(Color color) {
        this.style.setColor(color);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setNormalColor(Color color) {
        this.style.setColor(ComponentState.NORMAL, color);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setHoveredColor(Color color) {
        this.style.setColor(ComponentState.HOVERED, color);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setFocusedColor(Color color) {
        this.style.setColor(ComponentState.FOCUSED, color);
        return (T) this;
    }
}