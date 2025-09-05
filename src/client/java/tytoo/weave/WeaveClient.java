package tytoo.weave;

import net.fabricmc.api.ClientModInitializer;

public class WeaveClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WeaveCore.LOGGER.info("Hello developer! Weave is initializing...");
        WeaveCore.init();
    }
}
