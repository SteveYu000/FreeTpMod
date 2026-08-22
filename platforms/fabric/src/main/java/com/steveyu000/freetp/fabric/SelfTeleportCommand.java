package com.steveyu000.freetp.fabric;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;

/**
 * Defines only self-teleport syntax and delegates the actual movement to the
 * server's existing vanilla teleport command.
 */
public final class SelfTeleportCommand {
    private static final int TELEPORT_PERMISSION_LEVEL = 2;
    private static final String LOCATION_ARGUMENT = "location";
    private static final String DESTINATION_ARGUMENT = "destination";

    private SelfTeleportCommand() {
    }

    public static LiteralCommandNode<ServerCommandSource> createNode(String commandName) {
        return CommandManager.literal(commandName)
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(CommandManager.argument(LOCATION_ARGUMENT, Vec3ArgumentType.vec3())
                        .executes(SelfTeleportCommand::teleportToPosition))
                .then(CommandManager.argument(DESTINATION_ARGUMENT, EntityArgumentType.entity())
                        .executes(SelfTeleportCommand::teleportToEntity))
                .build();
    }

    private static int teleportToPosition(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        Vec3d destination = Vec3ArgumentType.getVec3(context, LOCATION_ARGUMENT);

        String command = "teleport "
                + player.getUuidAsString() + " "
                + coordinate(destination.x) + " "
                + coordinate(destination.y) + " "
                + coordinate(destination.z);
        return executeVanillaTeleport(source, command);
    }

    private static int teleportToEntity(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        Entity destination = EntityArgumentType.getEntity(context, DESTINATION_ARGUMENT);

        String command = "teleport " + player.getUuidAsString() + " " + destination.getUuidAsString();
        return executeVanillaTeleport(source, command);
    }

    private static int executeVanillaTeleport(ServerCommandSource source, String command) {
        VanillaCommandExecutor.execute(
                source.getServer().getCommandManager(),
                source,
                command,
                TELEPORT_PERMISSION_LEVEL
        );
        return 1;
    }

    private static String coordinate(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
