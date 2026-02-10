package com.steveyu000.freetp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.steveyu000.freetp.server.commands.TeleportCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FreeTpMod.MOD_ID)
public class FreeTpMod {
	// Define mod id in a common place for everything to reference
	public static final String MOD_ID = "ftp_mod";
	// Directly reference a slf4j logger


	@Mod.EventBusSubscriber(modid = FreeTpMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public class ForgeEventListener {
		@SubscribeEvent
		public static void RegisterCommand(RegisterCommandsEvent event) {
			CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
			RootCommandNode<CommandSourceStack> root = dispatcher.getRoot();
			removeCommand(root,"tp");
//			removeCommand(root,	"teleport");
			TeleportCommand.register(event.getDispatcher());
		}

		private static void removeCommand(RootCommandNode<CommandSourceStack> root, String name)
		{
			try {
				// CommandNode 内部有两个关键字段:
				// 1. children: Map<String, CommandNode>
				// 2. literals: Map<String, LiteralCommandNode>
				// 3. arguments: Map<String, ArgumentCommandNode>
				// 需要从这些 map 中全部移除

				Field childrenField = CommandNode.class.getDeclaredField("children");
				childrenField.setAccessible(true);
				Map<String, ?> children = (Map<String, ?>) childrenField.get(root);
				children.remove(name);

				Field literalsField = CommandNode.class.getDeclaredField("literals");
				literalsField.setAccessible(true);
				Map<String, ?> literals = (Map<String, ?>) literalsField.get(root);
				literals.remove(name);

				Field argumentsField = CommandNode.class.getDeclaredField("arguments");
				argumentsField.setAccessible(true);
				Map<String, ?> arguments = (Map<String, ?>) argumentsField.get(root);
				arguments.remove(name);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
