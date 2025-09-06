package tytoo.weave.component;

import tytoo.weave.event.Event;
import tytoo.weave.event.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventState {
    private final Map<EventType<?>, List<Consumer<?>>> eventListeners = new HashMap<>();
    private boolean focusable = false;
    private boolean hittable = true;
    private int tabIndex = 0;

    public Map<EventType<?>, List<Consumer<?>>> getEventListeners() {
        return eventListeners;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isHittable() {
        return hittable;
    }

    public void setHittable(boolean hittable) {
        this.hittable = hittable;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void fireEvent(E event) {
        EventType<E> type = (EventType<E>) event.getType();

        List<Consumer<?>> listeners = eventListeners.get(type);
        if (listeners != null) {
            for (Consumer<?> listener : new ArrayList<>(listeners)) {
                ((Consumer<E>) listener).accept(event);
            }
        }

        if (!event.isCancelled()) {
            List<Consumer<?>> anyListeners = eventListeners.get(Event.ANY);
            if (anyListeners != null) {
                for (Consumer<?> listener : new ArrayList<>(anyListeners)) {
                    ((Consumer<Event>) listener).accept(event);
                }
            }
        }
    }
}
