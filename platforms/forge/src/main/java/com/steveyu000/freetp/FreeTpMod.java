package com.steveyu000.freetp;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import com.steveyu000.freetp.server.commands.TeleportCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Iterator;

@Mod(FreeTpMod.MOD_ID)
public class FreeTpMod {
	public static final String MOD_ID = "ftp_mod";

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String TELEPORT_ALIAS = "tp";
	private static final String TELEPORT_COMMAND = "teleport";

	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static final class ForgeEventListener {
		private ForgeEventListener() {
		}

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void registerCommands(RegisterCommandsEvent event) {
			RootCommandNode<CommandSourceStack> root = event.getDispatcher().getRoot();
			LiteralCommandNode<CommandSourceStack> replacement =
					TeleportCommand.createNode(TELEPORT_ALIAS);
			CommandNode<CommandSourceStack> currentAlias = root.getChild(TELEPORT_ALIAS);

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
				RootCommandNode<CommandSourceStack> root,
				CommandNode<CommandSourceStack> alias
		) {
			CommandNode<CommandSourceStack> teleport = root.getChild(TELEPORT_COMMAND);
			return teleport != null
					&& alias.getRedirect() == teleport
					&& alias.getCommand() == null
					&& alias.getChildren().isEmpty();
		}

		private static void replaceChild(
				RootCommandNode<CommandSourceStack> root,
				CommandNode<CommandSourceStack> original,
				LiteralCommandNode<CommandSourceStack> replacement
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
				RootCommandNode<CommandSourceStack> root,
				CommandNode<CommandSourceStack> expected
		) {
			Iterator<CommandNode<CommandSourceStack>> iterator = root.getChildren().iterator();
			while (iterator.hasNext()) {
				if (iterator.next() == expected) {
					iterator.remove();
					return true;
				}
			}
			return false;
		}
	}
}
