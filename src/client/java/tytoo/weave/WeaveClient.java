package tytoo.weave;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;


public class WeaveClient implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "weave-ui";

    @Override
    public void onInitializeClient() {
        WeaveCore.init();
        LOGGER.info("Weave initialized!");
    }
}
