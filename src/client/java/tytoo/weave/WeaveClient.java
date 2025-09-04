package tytoo.weave;

import net.fabricmc.api.ClientModInitializer;

public class WeaveClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WeaveCore.init();
        WeaveCore.LOGGER.info("Weave initialized!");
    }
}
