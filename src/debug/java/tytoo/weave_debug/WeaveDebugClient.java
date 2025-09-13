package tytoo.weave_debug;

import net.fabricmc.api.ClientModInitializer;
import tytoo.weave.WeaveCore;

public class WeaveDebugClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WeaveCore.LOGGER.info("Hello developer! Weave is initializing...");
        WeaveCore.init();
        WeaveDebugCore.init();
    }
}
