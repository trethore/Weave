package tytoo.weave.ui.toast;

import tytoo.weave.component.components.layout.Panel;

public class ToastView extends Panel {
    protected ToastView() {
        super();
        this.setManagedByLayout(false);
        this.setOpacity(0f);
        this.addStyleClass("toast");
    }

    public static ToastView create() {
        return new ToastView();
    }
}
