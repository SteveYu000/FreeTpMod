package com.steveyu000.freetp.paper;

public final class TeleportCoordinatesTest {
    private static final double EPSILON = 1.0E-9D;

    private TeleportCoordinatesTest() {
    }

    public static void main(String[] arguments) {
        absoluteIntegerXZCoordinatesAreCentered();
        relativeCoordinatesUseTheOrigin();
        localCoordinatesUsePlayerRotation();
        pitchedLocalCoordinatesUseThePlayerFrame();
        mixedLocalCoordinatesAreRejected();
        nonFiniteCoordinatesAreRejected();
    }

    private static void absoluteIntegerXZCoordinatesAreCentered() {
        TeleportCoordinates.Position position = TeleportCoordinates.resolve(
                0.0D, 0.0D, 0.0D, 0.0F, 0.0F,
                new String[]{"10", "64", "-3"}
        );
        assertPosition(position, 10.5D, 64.0D, -2.5D);
    }

    private static void relativeCoordinatesUseTheOrigin() {
        TeleportCoordinates.Position position = TeleportCoordinates.resolve(
                5.25D, 70.0D, -4.5D, 0.0F, 0.0F,
                new String[]{"~1", "~", "~-2"}
        );
        assertPosition(position, 6.25D, 70.0D, -6.5D);
    }

    private static void localCoordinatesUsePlayerRotation() {
        TeleportCoordinates.Position position = TeleportCoordinates.resolve(
                10.0D, 64.0D, 20.0D, 0.0F, 0.0F,
                new String[]{"^1", "^2", "^3"}
        );
        assertPosition(position, 11.0D, 66.0D, 23.0D);
    }

    private static void pitchedLocalCoordinatesUseThePlayerFrame() {
        TeleportCoordinates.Position position = TeleportCoordinates.resolve(
                0.0D, 64.0D, 0.0D, 0.0F, 90.0F,
                new String[]{"^0", "^1", "^1"}
        );
        assertPosition(position, 0.0D, 63.0D, 1.0D);
    }

    private static void mixedLocalCoordinatesAreRejected() {
        assertRejected(new String[]{"^1", "~", "^3"});
    }

    private static void nonFiniteCoordinatesAreRejected() {
        assertRejected(new String[]{"1e309", "64", "0"});
    }

    private static void assertRejected(String[] coordinates) {
        try {
            TeleportCoordinates.resolve(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, coordinates);
            throw new AssertionError("Expected coordinates to be rejected");
        }
        catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private static void assertPosition(
            TeleportCoordinates.Position actual,
            double expectedX,
            double expectedY,
            double expectedZ
    ) {
        assertClose("x", expectedX, actual.x());
        assertClose("y", expectedY, actual.y());
        assertClose("z", expectedZ, actual.z());
    }

    private static void assertClose(String axis, double expected, double actual) {
        if (Math.abs(expected - actual) > EPSILON) {
            throw new AssertionError(axis + ": expected " + expected + ", got " + actual);
        }
    }
}
