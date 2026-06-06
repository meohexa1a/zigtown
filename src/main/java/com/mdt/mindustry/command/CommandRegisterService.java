package com.mdt.mindustry.command;

import java.util.*;

import javax.inject.*;

import org.jetbrains.annotations.NotNull;

import lombok.Locked;
import lombok.extern.slf4j.Slf4j;

import arc.util.CommandHandler;

import mindustry.gen.Player;

import com.mdt.common.type.Pair;

@Slf4j
@Singleton
public final class CommandRegisterService {
    private final Map<String, Pair<Set<ClientCommand>, Set<ConsoleCommand>>> registered = new HashMap<>();

    private final CommandHandler clientHandler;
    private final CommandHandler serverHandler;

    @Inject
    public CommandRegisterService(
        @Named("client") CommandHandler clientHandler,
        @Named("server") CommandHandler serverHandler) {

        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
    }

    // !---------------------------------------------------------------------!

    @Locked
    public void register(
        String group,
        @NotNull Set<ClientCommand> clientCommands,
        @NotNull Set<ConsoleCommand> consoleCommands) {

        if (registered.containsKey(group)) unregister(group);

        registered.put(group, Pair.of(clientCommands, consoleCommands));
        clientCommands.forEach(this::registerClient);
        consoleCommands.forEach(this::registerConsole);
    }

    @Locked
    public void unregister(String group) {
        var pair = registered.remove(group);
        if (pair == null) return;

        pair.first().forEach(cmd -> cmd.prefixes().forEach(clientHandler::removeCommand));
        pair.second().forEach(cmd -> cmd.prefixes().forEach(serverHandler::removeCommand));
    }

    // !--------------------------------------------------------!

    private void registerClient(ClientCommand cmd) {
        for (var prefix : cmd.prefixes())
            clientHandler.<Player>register(prefix, cmd.args(), cmd.description(), (args, player) -> {
                try {
                    cmd.action().accept(args, player);
                } catch (Exception e) {
                    log.error("Error while executing client command | {}", prefix, e);
                }
            });
    }

    private void registerConsole(ConsoleCommand cmd) {
        for (String prefix : cmd.prefixes())
            serverHandler.register(prefix, cmd.args(), cmd.description(), (args) -> {
                try {
                    cmd.action().accept(args);
                } catch (Exception ex) {
                    log.error("Error while executing console command | {}", prefix, ex);
                }
            });
    }
}
