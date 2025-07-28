package tytoo.weave;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class WeaveClient implements ClientModInitializer {
    private static KeyBinding openTestGuiKeybind;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            test();
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openTestGuiKeybind != null && openTestGuiKeybind.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new tytoo.weave.screen.screens.TestGui());
            }
        });
        System.out.println("WeaveClient initialized!");
    }

    private void test() {
        openTestGuiKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.weave.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.weave.test"
        ));
    }
}