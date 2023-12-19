package net.boster.bamboss.smp.listeners;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.chat.ChatInputHandler;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(ChatInputHandler.contains(e.getPlayer())) {
            e.setCancelled(true);
            ChatInputHandler.handle(e.getPlayer(), e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if(ChatInputHandler.contains(e.getPlayer())) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(BambossSMP.getInstance(), () -> {
                ChatInputHandler.handle(e.getPlayer(), e.getMessage());
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPvP(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            SMPWorld world = BambossSMP.getInstance().getWorldManager().getWorld(e.getEntity().getWorld().getName());
            if(world != null && !world.getSettings().isPvp()) {
                e.setCancelled(true);
            }
        }
    }
}
