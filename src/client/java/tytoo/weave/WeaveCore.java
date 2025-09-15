package tytoo.weave;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tytoo.weave.command.WeaveCommands;
import tytoo.weave.style.contract.StyleContracts;
import tytoo.weave.ui.CursorManager;
import tytoo.weave.utils.FontManager;
import tytoo.weave.utils.ImageManager;

/*
 * Core class for initializing Weave.
 */
public final class WeaveCore {
    public static final String ID = "weave-ui";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    private WeaveCore() {
    }

    /**
     * Initializes Weave by registering debug commands and close event handlers.
     * Call this method in your mod's client initializer.
     */
    public static void init() {
        StyleContracts.bootstrap();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            registerDevCommands();
        }

        registerCloseEvent();

        WeaveCore.LOGGER.info("Weave initialized!");
    }

    /**
     * Registers test commands.
     * /weave testgui (not in published artifacts)
     * /weave demo.
     * /weave reloadtheme
     */
    private static void registerDevCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> WeaveCommands.register(dispatcher));
    }

    /**
     * Registers an event handler to clean up resources when the client is stopping.
     * This includes closing all fonts and destroying the cursor manager.
     */
    private static void registerCloseEvent() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            FontManager.closeAll();
            CursorManager.destroy();
            ImageManager.clearCaches();
        });
    }
}
