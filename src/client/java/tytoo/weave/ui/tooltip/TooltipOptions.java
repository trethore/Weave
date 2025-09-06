package tytoo.weave.ui.tooltip;

public class TooltipOptions {
    private long delayMs = 500;
    private boolean followMouse = true;
    private Placement placement = Placement.TOP;
    private float maxWidth = 240f;
    private long fadeInMs = 120;
    private long fadeOutMs = 100;

    public long getDelayMs() {
        return delayMs;
    }

    public TooltipOptions setDelayMs(long delayMs) {
        this.delayMs = Math.max(0, delayMs);
        return this;
    }

    public boolean isFollowMouse() {
        return followMouse;
    }

    public TooltipOptions setFollowMouse(boolean followMouse) {
        this.followMouse = followMouse;
        return this;
    }

    public Placement getPlacement() {
        return placement;
    }

    public TooltipOptions setPlacement(Placement placement) {
        if (placement != null) {
            this.placement = placement;
        }
        return this;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public TooltipOptions setMaxWidth(float maxWidth) {
        this.maxWidth = Math.max(1f, maxWidth);
        return this;
    }

    public long getFadeInMs() {
        return fadeInMs;
    }

    public TooltipOptions setFadeInMs(long fadeInMs) {
        this.fadeInMs = Math.max(0, fadeInMs);
        return this;
    }

    public long getFadeOutMs() {
        return fadeOutMs;
    }

    public TooltipOptions setFadeOutMs(long fadeOutMs) {
        this.fadeOutMs = Math.max(0, fadeOutMs);
        return this;
    }

    public enum Placement {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }
}

