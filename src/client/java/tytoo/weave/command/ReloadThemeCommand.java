package tytoo.weave.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import tytoo.weave.theme.DefaultTheme;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.UIManager;
import tytoo.weave.utils.ImageManager;

public final class ReloadThemeCommand {
    private ReloadThemeCommand() {
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("reloadtheme").executes(ctx -> {
            ThemeManager.setTheme(new DefaultTheme());
            ImageManager.clearCaches();
            UIManager.invalidateAllStyles();
            ctx.getSource().sendFeedback(Text.literal("Weave theme reloaded."));
            return 1;
        });
    }
}
