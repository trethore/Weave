package tytoo.weave.event.focus;

import tytoo.weave.event.EventType;

public class FocusGainedEvent extends FocusEvent {
    public static final EventType<FocusGainedEvent> TYPE = new EventType<>();

    @Override
    public EventType<FocusGainedEvent> getType() {
        return TYPE;
    }
}