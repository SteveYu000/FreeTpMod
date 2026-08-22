package com.steveyu000.freetp.paper;

import java.util.regex.Pattern;

/** Pure Java parser for Minecraft-style absolute, relative and local coordinates. */
final class TeleportCoordinates {
    private static final double HORIZONTAL_LIMIT = 30_000_000.0D;
    private static final double VERTICAL_LIMIT = 20_000_000.0D;
    private static final Pattern NUMBER = Pattern.compile("-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)");
    private static final Pattern INTEGER = Pattern.compile("-?\\d+");

    private TeleportCoordinates() {
    }

    static Position resolve(
            double originX,
            double originY,
            double originZ,
            float yaw,
            float pitch,
            String[] arguments
    ) {
        if (arguments.length != 3) {
            throw new IllegalArgumentException("Exactly three coordinates are required.");
        }

        boolean local = arguments[0].startsWith("^")
                || arguments[1].startsWith("^")
                || arguments[2].startsWith("^");
        Position result = local
                ? resolveLocal(originX, originY, originZ, yaw, pitch, arguments)
                : resolveWorld(originX, originY, originZ, arguments);
        validateBounds(result);
        return result;
    }

    private static Position resolveWorld(
            double originX,
            double originY,
            double originZ,
            String[] arguments
    ) {
        for (String argument : arguments) {
            if (argument.startsWith("^")) {
                throw new IllegalArgumentException("Local and world coordinates cannot be mixed.");
            }
        }

        return new Position(
                parseWorld(arguments[0], originX, true),
                parseWorld(arguments[1], originY, false),
                parseWorld(arguments[2], originZ, true)
        );
    }

    private static Position resolveLocal(
            double originX,
            double originY,
            double originZ,
            float yaw,
            float pitch,
            String[] arguments
    ) {
        for (String argument : arguments) {
            if (!argument.startsWith("^")) {
                throw new IllegalArgumentException("Local coordinates must all use ^ notation.");
            }
        }

        double leftAmount = parseOffset(arguments[0].substring(1));
        double upAmount = parseOffset(arguments[1].substring(1));
        double forwardAmount = parseOffset(arguments[2].substring(1));

        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);

        double forwardX = -Math.cos(pitchRadians) * Math.sin(yawRadians);
        double forwardY = -Math.sin(pitchRadians);
        double forwardZ = Math.cos(pitchRadians) * Math.cos(yawRadians);

        double leftX = Math.cos(yawRadians);
        double leftZ = Math.sin(yawRadians);

        double upX = forwardY * leftZ;
        double upY = forwardZ * leftX - forwardX * leftZ;
        double upZ = -forwardY * leftX;

        return new Position(
                originX + leftX * leftAmount + upX * upAmount + forwardX * forwardAmount,
                originY + upY * upAmount + forwardY * forwardAmount,
                originZ + leftZ * leftAmount + upZ * upAmount + forwardZ * forwardAmount
        );
    }

    private static double parseWorld(String value, double origin, boolean centerInteger) {
        if (value.startsWith("~")) {
            return origin + parseOffset(value.substring(1));
        }
        double parsed = parseNumber(value);
        return centerInteger && INTEGER.matcher(value).matches() ? parsed + 0.5D : parsed;
    }

    private static double parseOffset(String value) {
        return value.isEmpty() ? 0.0D : parseNumber(value);
    }

    private static double parseNumber(String value) {
        if (!NUMBER.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid coordinate.");
        }
        double parsed = Double.parseDouble(value);
        if (!Double.isFinite(parsed)) {
            throw new IllegalArgumentException("Coordinate must be finite.");
        }
        return parsed;
    }

    private static void validateBounds(Position position) {
        if (!Double.isFinite(position.x())
                || !Double.isFinite(position.y())
                || !Double.isFinite(position.z())
                || Math.abs(position.x()) > HORIZONTAL_LIMIT
                || Math.abs(position.y()) > VERTICAL_LIMIT
                || Math.abs(position.z()) > HORIZONTAL_LIMIT) {
            throw new IllegalArgumentException("Coordinate is outside Minecraft's teleport bounds.");
        }
    }

    record Position(double x, double y, double z) {
    }
}
