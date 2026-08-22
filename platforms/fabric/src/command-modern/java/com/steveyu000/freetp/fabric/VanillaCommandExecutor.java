package com.steveyu000.freetp.fabric;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

final class VanillaCommandExecutor {
    private VanillaCommandExecutor() {
    }

    static void execute(CommandManager commandManager, ServerCommandSource source, String command) {
        commandManager.parseAndExecute(source, command);
    }
}
