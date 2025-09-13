package tytoo.weave_debug;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import tytoo.weave_debug.command.WeaveDebugCommands;

public final class WeaveDebugCore {
    private WeaveDebugCore() {
    }

    public static void init() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            registerDebugCommands();
        }
    }

    private static void registerDebugCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> WeaveDebugCommands.register(dispatcher));
    }
}