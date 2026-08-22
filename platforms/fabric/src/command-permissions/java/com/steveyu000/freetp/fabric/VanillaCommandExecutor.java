package com.steveyu000.freetp.fabric;

import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

final class VanillaCommandExecutor {
    private VanillaCommandExecutor() {
    }

    static void execute(
            CommandManager commandManager,
            ServerCommandSource source,
            String command,
            int permissionLevel
    ) {
        PermissionLevel level = PermissionLevel.fromLevel(permissionLevel);
        commandManager.parseAndExecute(
                source.withPermissions(LeveledPermissionPredicate.fromLevel(level)),
                command
        );
    }
}
