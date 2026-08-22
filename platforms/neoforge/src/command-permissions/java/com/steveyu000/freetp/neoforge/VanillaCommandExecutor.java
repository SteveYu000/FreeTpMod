package com.steveyu000.freetp.neoforge;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.LevelBasedPermissionSet;

final class VanillaCommandExecutor {
    private VanillaCommandExecutor() {
    }

    static void execute(
            Commands commands,
            CommandSourceStack source,
            String command,
            int permissionLevel
    ) {
        commands.performPrefixedCommand(
                source.withMaximumPermission(LevelBasedPermissionSet.GAMEMASTER),
                command
        );
    }
}
