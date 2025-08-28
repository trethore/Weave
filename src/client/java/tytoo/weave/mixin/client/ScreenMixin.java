package tytoo.weave.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tytoo.weave.ui.UIManager;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Unique
    private Screen self() {
        return (Screen) (Object) this;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        UIManager.onRender(self(), context);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        UIManager.onInit(self());
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        UIManager.onClose(self());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (UIManager.onKeyPressed(self(), keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
    }
}