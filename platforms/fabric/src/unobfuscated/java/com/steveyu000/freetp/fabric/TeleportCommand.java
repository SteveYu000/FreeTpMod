package com.steveyu000.freetp.fabric;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;

/**
 * Defines only self-teleport syntax and delegates the actual movement to the
 * server's existing vanilla teleport command.
 */
public final class TeleportCommand {
    private static final String LOCATION_ARGUMENT = "location";
    private static final String DESTINATION_ARGUMENT = "destination";

    private TeleportCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createNode(String commandName) {
        return Commands.literal(commandName)
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.argument(LOCATION_ARGUMENT, Vec3Argument.vec3())
                        .executes(TeleportCommand::teleportToPosition))
                .then(Commands.argument(DESTINATION_ARGUMENT, EntityArgument.entity())
                        .executes(TeleportCommand::teleportToEntity))
                .build();
    }

    private static int teleportToPosition(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        Vec3 destination = Vec3Argument.getVec3(context, LOCATION_ARGUMENT);

        String command = "teleport "
                + player.getStringUUID() + " "
                + coordinate(destination.x) + " "
                + coordinate(destination.y) + " "
                + coordinate(destination.z);
        return executeVanillaTeleport(source, command);
    }

    private static int teleportToEntity(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        Entity destination = EntityArgument.getEntity(context, DESTINATION_ARGUMENT);

        String command = "teleport " + player.getStringUUID() + " " + destination.getStringUUID();
        return executeVanillaTeleport(source, command);
    }

    private static int executeVanillaTeleport(CommandSourceStack source, String command) {
        source.getServer().getCommands().performPrefixedCommand(
                source.withMaximumPermission(LevelBasedPermissionSet.GAMEMASTER),
                command
        );
        return 1;
    }

    private static String coordinate(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
