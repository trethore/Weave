package tytoo.weave.ui.toast;

public final class ToastOptions {
    private long durationMs = 3000;
    private long fadeInMs = 160;
    private long fadeOutMs = 180;
    private float margin = 8f;
    private float gap = 6f;
    private Position position = Position.TOP_RIGHT;

    public long getDurationMs() {
        return durationMs;
    }

    public ToastOptions setDurationMs(long durationMs) {
        this.durationMs = Math.max(0, durationMs);
        return this;
    }

    public long getFadeInMs() {
        return fadeInMs;
    }

    public ToastOptions setFadeInMs(long fadeInMs) {
        this.fadeInMs = Math.max(0, fadeInMs);
        return this;
    }

    public long getFadeOutMs() {
        return fadeOutMs;
    }

    public ToastOptions setFadeOutMs(long fadeOutMs) {
        this.fadeOutMs = Math.max(0, fadeOutMs);
        return this;
    }

    public float getMargin() {
        return margin;
    }

    public ToastOptions setMargin(float margin) {
        this.margin = Math.max(0f, margin);
        return this;
    }

    public float getGap() {
        return gap;
    }

    public ToastOptions setGap(float gap) {
        this.gap = Math.max(0f, gap);
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public ToastOptions setPosition(Position position) {
        if (position != null) this.position = position;
        return this;
    }

    public enum Position {TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT}
}

