package tytoo.weave.component;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.RotationAxis;
import tytoo.weave.effects.Effect;
import tytoo.weave.state.State;

import java.util.ArrayList;
import java.util.List;

public class RenderState {
    public final State<Float> rotation = new State<>(0.0f);
    public final State<Float> scaleX = new State<>(1.0f);
    public final State<Float> scaleY = new State<>(1.0f);
    public final State<Float> opacity = new State<>(1.0f);
    private final Component<?> owner;
    private boolean visible = true;
    private List<Effect> effects = new ArrayList<>();

    public RenderState(Component<?> owner) {
        this.owner = owner;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    public void applyTransformations(DrawContext context) {
        if (rotation.get() == 0.0f && scaleX.get() == 1.0f && scaleY.get() == 1.0f) return;

        float pivotX = owner.getLeft() + owner.getWidth() / 2;
        float pivotY = owner.getTop() + owner.getHeight() / 2;

        context.getMatrices().translate(pivotX, pivotY, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get()));
        context.getMatrices().scale(scaleX.get(), scaleY.get(), 1.0f);
        context.getMatrices().translate(-pivotX, -pivotY, 0);
    }
}