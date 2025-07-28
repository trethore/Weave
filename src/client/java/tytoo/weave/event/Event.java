package tytoo.weave.event;

public abstract class Event {
    public static final EventType<Event> ANY = new EventType<>();
    private boolean cancelled = false;

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public abstract EventType<? extends Event> getType();
}