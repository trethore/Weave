package tytoo.weave.ui.tooltip;

import tytoo.weave.component.components.layout.Panel;

public class TooltipView extends Panel {
    protected TooltipView() {
        super();
        this.setManagedByLayout(false);
        this.setOpacity(0f);
    }

    public static TooltipView create() {
        return new TooltipView();
    }

    @Override
    public void measure(float availableWidth, float availableHeight) {
        super.measure(availableWidth, availableHeight);
        float horizontalPadding = getLayoutState().getPadding().left() + getLayoutState().getPadding().right();
        float verticalPadding = getLayoutState().getPadding().top() + getLayoutState().getPadding().bottom();
        getLayoutState().setMeasuredWidth(getLayoutState().getMeasuredWidth() + horizontalPadding);
        getLayoutState().setMeasuredHeight(getLayoutState().getMeasuredHeight() + verticalPadding);
    }
}
