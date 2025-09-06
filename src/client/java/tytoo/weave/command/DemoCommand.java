package tytoo.weave.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import tytoo.weave.screen.screens.DemoWeaveScreen;

public final class DemoCommand {
    private DemoCommand() {
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("demo").executes(ctx -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.send(() -> new DemoWeaveScreen().open());
                ctx.getSource().sendFeedback(Text.literal("Opened Weave Demo."));
                return 1;
            }
            return 0;
        });
    }
}

