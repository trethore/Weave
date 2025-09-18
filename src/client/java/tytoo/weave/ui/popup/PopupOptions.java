package tytoo.weave.ui.popup;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class PopupOptions {
    private boolean modal = false;
    private boolean trapFocus = true;
    private boolean closeOnBackdropClick = true;
    private boolean closeOnEsc = true;
    private boolean closeOnFocusLoss = true;
    private boolean autoFlip = true;
    private Boolean clickThroughBackdrop = null;
    private float gap = 6f;
    @Nullable
    private Consumer<PopupCloseEvent> onClose;

    public boolean isModal() {
        return modal;
    }

    public PopupOptions setModal(boolean modal) {
        this.modal = modal;
        if (modal) this.trapFocus = true;
        return this;
    }

    public boolean isTrapFocus() {
        return trapFocus;
    }

    public PopupOptions setTrapFocus(boolean trapFocus) {
        this.trapFocus = trapFocus;
        return this;
    }

    public boolean isCloseOnBackdropClick() {
        return closeOnBackdropClick;
    }

    public PopupOptions setCloseOnBackdropClick(boolean closeOnBackdropClick) {
        this.closeOnBackdropClick = closeOnBackdropClick;
        return this;
    }

    public boolean isCloseOnEsc() {
        return closeOnEsc;
    }

    public PopupOptions setCloseOnEsc(boolean closeOnEsc) {
        this.closeOnEsc = closeOnEsc;
        return this;
    }

    public boolean isCloseOnFocusLoss() {
        return closeOnFocusLoss;
    }

    public PopupOptions setCloseOnFocusLoss(boolean closeOnFocusLoss) {
        this.closeOnFocusLoss = closeOnFocusLoss;
        return this;
    }

    public boolean isAutoFlip() {
        return autoFlip;
    }

    public PopupOptions setAutoFlip(boolean autoFlip) {
        this.autoFlip = autoFlip;
        return this;
    }

    public Boolean getClickThroughBackdrop() {
        return clickThroughBackdrop;
    }

    public PopupOptions setClickThroughBackdrop(Boolean clickThroughBackdrop) {
        this.clickThroughBackdrop = clickThroughBackdrop;
        return this;
    }

    public float getGap() {
        return gap;
    }

    public PopupOptions setGap(float gap) {
        this.gap = gap;
        return this;
    }

    @Nullable
    public Consumer<PopupCloseEvent> getOnClose() {
        return onClose;
    }

    public PopupOptions setOnClose(@Nullable Consumer<PopupCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }
}
