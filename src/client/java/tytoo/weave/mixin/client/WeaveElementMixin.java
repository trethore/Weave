package tytoo.weave.mixin.client;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tytoo.weave.ui.UIManager;

@Mixin(Element.class)
public interface WeaveElementMixin {
    @Inject(method = "mouseMoved", at = @At("HEAD"))
    private void onMouseMoved(double mouseX, double mouseY, CallbackInfo ci) {
        if (this instanceof Screen screen) {
            UIManager.onMouseMoved(screen, mouseX, mouseY);
        }
    }
}