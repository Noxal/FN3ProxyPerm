package net.noxal.f3nproxyperm;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandResult;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class F3NPermCommand {
    public static BrigadierCommand command() {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("f3nproxyperm")
                .requires(source -> source.hasPermission("f3nperm.forceupdate"))
                .then(BrigadierCommand.literalArgumentBuilder("forceupdate")
                        .executes(F3NPermCommand::updateAll)
                        .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                                .suggests(F3NPermCommand::suggestPlayers)
                                .executes(F3NPermCommand::updatePlayer)
                        )
                )
                .build());
    }

    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSource> ctx, SuggestionsBuilder suggestionsBuilder) {
        F3NProxyPerm.proxy.getAllPlayers().stream()
                .map(Player::getUsername)
                .filter(username -> username.toLowerCase().startsWith(suggestionsBuilder.getRemaining().toLowerCase()))
                .forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    private static int updateAll(CommandContext<CommandSource> ctx) {
        for (Player player : F3NProxyPerm.proxy.getAllPlayers()) {
            F3NProxyPerm.instance.updateOpLevel(player);
        }

        ctx.getSource().sendMessage(Component.text("Force updated all players"));
        return CommandResult.EXECUTED.ordinal();
    }

    private static int updatePlayer(CommandContext<CommandSource> ctx) {
        String playerName = StringArgumentType.getString(ctx, "player");
        Optional<Player> player = F3NProxyPerm.proxy.getPlayer(playerName);

        if (player.isPresent()) {
            F3NProxyPerm.instance.updateOpLevel(player.get());
            ctx.getSource().sendMessage(Component.text("Force updated " + playerName));
        } else {
            ctx.getSource().sendMessage(Component.text("Player not found", NamedTextColor.RED));
        }
        return CommandResult.EXECUTED.ordinal();
    }
}
