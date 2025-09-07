package tytoo.weave.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import tytoo.weave.command.commands.DemoCommand;
import tytoo.weave.command.commands.ReloadThemeCommand;
import tytoo.weave.command.commands.TestGuiCommand;

public final class WeaveCommands {
    private WeaveCommands() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("weave")
                .then(TestGuiCommand.build())
                .then(DemoCommand.build())
                .then(ReloadThemeCommand.build());
        dispatcher.register(root);
    }
}
