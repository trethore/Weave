package tytoo.weave.event.focus;

import tytoo.weave.event.EventType;

public class FocusLostEvent extends FocusEvent {
    public static final EventType<FocusLostEvent> TYPE = new EventType<>();

    @Override
    public EventType<FocusLostEvent> getType() {
        return TYPE;
    }
}