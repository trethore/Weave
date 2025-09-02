package tytoo.weave;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import tytoo.weave.command.WeaveCommands;
import tytoo.weave.ui.CursorManager;
import tytoo.weave.utils.FontManager;

public class WeaveClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "weave-ui";

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> WeaveCommands.register(dispatcher));
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            FontManager.closeAll();
            CursorManager.destroy();
        });
        LOGGER.info("Weave initialized!");
    }
}
