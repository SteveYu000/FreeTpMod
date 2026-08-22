package com.steveyu000.freetp.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public final class FreeTpFabric implements ModInitializer {
    public static final String MOD_ID = "ftp_mod";

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String TELEPORT_ALIAS = "tp";
    private static final String TELEPORT_COMMAND = "teleport";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                replaceVanillaAlias(dispatcher));
    }

    private static void replaceVanillaAlias(CommandDispatcher<ServerCommandSource> dispatcher) {
        RootCommandNode<ServerCommandSource> root = dispatcher.getRoot();
        LiteralCommandNode<ServerCommandSource> replacement =
                SelfTeleportCommand.createNode(TELEPORT_ALIAS);
        CommandNode<ServerCommandSource> currentAlias = root.getChild(TELEPORT_ALIAS);

        if (currentAlias == null) {
            root.addChild(replacement);
            LOGGER.info("Registered self-only /{} command", TELEPORT_ALIAS);
            return;
        }

        if (!isVanillaTeleportAlias(root, currentAlias)) {
            LOGGER.error(
                    "Command /{} was already modified by another mod; Free TP will leave it untouched",
                    TELEPORT_ALIAS
            );
            return;
        }

        try {
            replaceChild(root, currentAlias, replacement);
            LOGGER.info("Replaced vanilla /{} alias with the self-only command", TELEPORT_ALIAS);
        }
        catch (RuntimeException exception) {
            if (root.getChild(TELEPORT_ALIAS) != currentAlias) {
                throw new IllegalStateException("Failed to restore the vanilla /tp command", exception);
            }

            LOGGER.error("Could not safely replace /{}; leaving vanilla behavior intact", TELEPORT_ALIAS, exception);
        }
    }

    private static boolean isVanillaTeleportAlias(
            RootCommandNode<ServerCommandSource> root,
            CommandNode<ServerCommandSource> alias
    ) {
        CommandNode<ServerCommandSource> teleport = root.getChild(TELEPORT_COMMAND);
        return teleport != null
                && alias.getRedirect() == teleport
                && alias.getCommand() == null
                && alias.getChildren().isEmpty();
    }

    private static void replaceChild(
            RootCommandNode<ServerCommandSource> root,
            CommandNode<ServerCommandSource> original,
            LiteralCommandNode<ServerCommandSource> replacement
    ) {
        if (!removeChildByIdentity(root, original) || root.getChild(TELEPORT_ALIAS) != null) {
            throw new IllegalStateException("The vanilla /tp alias could not be removed atomically");
        }

        try {
            root.addChild(replacement);
            if (root.getChild(TELEPORT_ALIAS) != replacement) {
                throw new IllegalStateException("The self-only /tp command was not installed");
            }
        }
        catch (RuntimeException exception) {
            removeChildByIdentity(root, replacement);
            root.addChild(original);
            throw exception;
        }
    }

    private static boolean removeChildByIdentity(
            RootCommandNode<ServerCommandSource> root,
            CommandNode<ServerCommandSource> expected
    ) {
        Iterator<CommandNode<ServerCommandSource>> iterator = root.getChildren().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == expected) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
