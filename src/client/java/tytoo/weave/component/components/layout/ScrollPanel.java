package tytoo.weave.component.components.layout;

import tytoo.weave.component.Component;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effects;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;

public class ScrollPanel extends BasePanel<ScrollPanel> {
    private final BasePanel<?> contentPanel;
    private final State<Float> scrollY = new State<>(0f);
    private float scrollSpeed = 10f;
    private float gap = 2f;

    public ScrollPanel() {
        this(createDefaultContentPanel());
        this.contentPanel.setHeight(Constraints.sumOfChildrenHeight(0, this.gap));
        this.contentPanel.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, this.gap));
    }

    public ScrollPanel(BasePanel<?> contentPanel) {
        this.setWidth(Constraints.relative(1.0f));
        this.setHeight(Constraints.relative(1.0f));

        this.addEffect(Effects.scissor());

        this.contentPanel = contentPanel;
        this.contentPanel
                .setX(Constraints.pixels(0))
                .setWidth(Constraints.relative(1.0f));
        super.addChild(this.contentPanel);

        this.contentPanel.setY((c, parentHeight, componentHeight) -> this.scrollY.get());
        this.scrollY.addListener((v) -> this.contentPanel.invalidateLayout());

        this.onMouseScroll(event -> {
            float contentHeight = this.contentPanel.getFinalHeight();
            float viewHeight = this.getInnerHeight();

            if (contentHeight <= viewHeight) return;

            float newScroll = scrollY.get() + (float) (event.getScrollY() * this.scrollSpeed);
            float maxScroll = Math.min(0, -(contentHeight - viewHeight));

            scrollY.set(Math.max(maxScroll, Math.min(0, newScroll)));
        });
    }

    private static Panel createDefaultContentPanel() {
        return Panel.create();
    }

    public BasePanel<?> getContentPanel() {
        return contentPanel;
    }

    public float getScrollY() {
        return this.scrollY.get();
    }

    public ScrollPanel setScrollY(float value) {
        this.scrollY.set(value);
        return this;
    }

    @Override
    public void addChild(Component<?> child) {
        this.contentPanel.addChild(child);
    }

    @Override
    public void removeChild(Component<?> child) {
        this.contentPanel.removeChild(child);
    }

    @Override
    public ScrollPanel addChildren(Component<?>... components) {
        this.contentPanel.addChildren(components);
        return this;
    }

    public ScrollPanel setGap(float gap) {
        this.gap = gap;
        if (this.contentPanel.getLayout() instanceof LinearLayout) {
            this.contentPanel.setHeight(Constraints.sumOfChildrenHeight(0, this.gap));
            this.contentPanel.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, this.gap));
        }
        return this;
    }

    public ScrollPanel setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        return this;
    }

    @Override
    public Component<?> hitTest(float x, float y) {
        if (!isPointInside(x, y)) {
            return null;
        }
        return super.hitTest(x, y);
    }
}
