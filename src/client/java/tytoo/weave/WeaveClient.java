package tytoo.weave;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import tytoo.weave.screen.screens.TestGui;
import tytoo.weave.ui.CursorManager;
import tytoo.weave.utils.FontManager;

public class WeaveClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static String MOD_ID = "weave-ui";
    private static KeyBinding openTestGuiKeybind;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            test();
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openTestGuiKeybind != null && openTestGuiKeybind.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new TestGui());
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            FontManager.closeAll();
            CursorManager.destroy();
        });
        LOGGER.info("Weave initialized!");
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