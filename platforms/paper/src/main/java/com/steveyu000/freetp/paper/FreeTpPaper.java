package com.steveyu000.freetp.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Paper entry point. The plugin deliberately does not register a command, so
 * Bukkit cannot create namespaced or fallback aliases for it.
 */
public final class FreeTpPaper extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled self-only /tp interception without registering aliases.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!TeleportCommand.isTeleportCommand(event.getMessage())) {
            return;
        }

        event.setCancelled(true);
        TeleportCommand.execute(event.getPlayer(), event.getMessage());
    }
}
