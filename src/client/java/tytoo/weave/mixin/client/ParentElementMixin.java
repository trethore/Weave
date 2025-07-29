package tytoo.weave.mixin.client;

import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tytoo.weave.ui.UIManager;

@Mixin(ParentElement.class)
public interface ParentElementMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Screen screen) {
            if (UIManager.onMouseClicked(screen, mouseX, mouseY, button)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Screen screen) {
            if (UIManager.onMouseReleased(screen)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Screen screen) {
            if (UIManager.onMouseDragged(screen, mouseX, mouseY, button, deltaX, deltaY)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Screen screen) {
            if (UIManager.onMouseScrolled(screen, mouseX, mouseY, horizontalAmount, verticalAmount)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Screen screen) {
            if (UIManager.onCharTyped(screen, chr, modifiers)) {
                cir.setReturnValue(true);
            }
        }
    }
}