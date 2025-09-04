package tytoo.weave;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tytoo.weave.command.WeaveCommands;
import tytoo.weave.ui.CursorManager;
import tytoo.weave.utils.FontManager;

/*
 * Core class for initializing Weave.
 */
public final class WeaveCore {
    public static final String MOD_ID = "weave-ui";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    private WeaveCore() {
    }

    /**
     * Initializes Weave by registering debug commands and close event handlers.
     * Call this method in your mod's client initializer.
     */

    public static void init() {
        registerDebugCommands();
        registerCloseEvent();
    }

    /**
     * Registers debug commands if the environment is a development environment.
     * /weave testgui
     * /weave reloadtheme
     */
    private static void registerDebugCommands() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> WeaveCommands.register(dispatcher));
        }
    }

    /**
     * Registers an event handler to clean up resources when the client is stopping.
     * This includes closing all fonts and destroying the cursor manager.
     */
    private static void registerCloseEvent() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            FontManager.closeAll();
            CursorManager.destroy();
        });
    }
}
