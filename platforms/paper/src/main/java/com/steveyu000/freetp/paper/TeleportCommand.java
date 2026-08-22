package com.steveyu000.freetp.paper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implements the two self-only {@code /tp} forms with the public Bukkit API.
 * No Minecraft or CraftBukkit implementation classes are linked or copied.
 */
public final class TeleportCommand {
    private static final String LABEL = "tp";
    private static final String USAGE = "Usage: /tp <x> <y> <z> or /tp <target entity>";

    private TeleportCommand() {
    }

    public static boolean isTeleportCommand(String message) {
        if (message == null || message.length() < 3 || message.charAt(0) != '/') {
            return false;
        }

        int labelEnd = firstWhitespace(message, 1);
        String label = message.substring(1, labelEnd);
        return LABEL.equals(label);
    }

    public static void execute(Player player, String message) {
        String arguments = argumentsOf(message);
        if (arguments.isEmpty()) {
            player.sendMessage(USAGE);
            return;
        }

        String[] parts = arguments.split("\\s+");
        if (parts.length == 1) {
            teleportToEntity(player, parts[0]);
            return;
        }
        if (parts.length == 3) {
            teleportToPosition(player, parts);
            return;
        }

        player.sendMessage(USAGE);
    }

    private static void teleportToPosition(Player player, String[] arguments) {
        final TeleportCoordinates.Position position;
        try {
            Location origin = player.getLocation();
            position = TeleportCoordinates.resolve(
                    origin.getX(),
                    origin.getY(),
                    origin.getZ(),
                    origin.getYaw(),
                    origin.getPitch(),
                    arguments
            );
        }
        catch (IllegalArgumentException exception) {
            player.sendMessage("Invalid coordinates. " + USAGE);
            return;
        }

        Location destination = player.getLocation();
        destination.setX(position.x());
        destination.setY(position.y());
        destination.setZ(position.z());

        if (player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND)) {
            player.sendMessage("Teleported to "
                    + coordinate(position.x()) + ", "
                    + coordinate(position.y()) + ", "
                    + coordinate(position.z()));
        }
        else {
            player.sendMessage("Teleport was cancelled.");
        }
    }

    private static void teleportToEntity(Player player, String selector) {
        final List<Entity> destinations;
        try {
            destinations = Bukkit.selectEntities(player, selector);
        }
        catch (IllegalArgumentException exception) {
            player.sendMessage("Invalid target entity. " + USAGE);
            return;
        }

        if (destinations.size() != 1) {
            player.sendMessage(destinations.isEmpty()
                    ? "No entity was found."
                    : "The target selector must select exactly one entity.");
            return;
        }

        Entity destination = destinations.get(0);
        if (player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND)) {
            player.sendMessage("Teleported to " + destination.getName());
        }
        else {
            player.sendMessage("Teleport was cancelled.");
        }
    }

    private static String argumentsOf(String message) {
        int labelEnd = firstWhitespace(message, 1);
        if (labelEnd == message.length()) {
            return "";
        }
        return message.substring(labelEnd).trim();
    }

    private static int firstWhitespace(String value, int start) {
        for (int index = start; index < value.length(); index++) {
            if (Character.isWhitespace(value.charAt(index))) {
                return index;
            }
        }
        return value.length();
    }

    private static String coordinate(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
