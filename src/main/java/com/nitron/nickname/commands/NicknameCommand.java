package com.nitron.nickname.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.nitron.nickname.cca.PlayerNickComponent;
import com.nitron.nickname.config.Config;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NicknameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess acc, CommandManager.RegistrationEnvironment ignoredDedicated) {
        dispatcher.register(literal("nick")
                .then(literal("set")
                        .then(argument("nick", TextArgumentType.text(acc))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayerOrThrow();
                                    PlayerNickComponent component = PlayerNickComponent.KEY.get(player);
                                    Text nick = TextArgumentType.getTextArgument(context, "nick");
                                    if (nick.getString().equals(" ") || nick.getString().isEmpty() || nick.equals(Text.empty())) {
                                        component.nick = Optional.empty();
                                        component.sync();
                                        player.sendMessage(Text.literal("Removed your nickname").formatted(Formatting.GRAY), false);
                                    } else {
                                        int length = nick.getString().length();
                                        if (length > Config.maxNick) {
                                            player.sendMessage(Text.literal("Nickname exceeds character limit").formatted(Formatting.RED), false);
                                        } else if (nick.getString().equals(" ") || nick.getString().isEmpty()) {
                                            player.sendMessage(Text.literal("Nickname cannot be empty").formatted(Formatting.RED), false);
                                        } else {
                                            component.nick = Optional.of(nick);
                                            component.sync();
                                            player.sendMessage(Text.literal("Set nickname to: " + nick.getString()).formatted(Formatting.GRAY), false);
                                        }
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).then(literal("clear")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayerOrThrow();
                            PlayerNickComponent component = PlayerNickComponent.KEY.get(player);

                            component.nick = Optional.empty();
                            component.sync();

                            player.sendMessage(Text.literal("Removed your nickname").formatted(Formatting.GRAY), false);

                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}