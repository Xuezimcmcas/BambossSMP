package net.boster.bamboss.smp.listeners;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class SpawnListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBed(PlayerBedLeaveEvent e) {
        e.setSpawnLocation(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(e.getPlayer());
        if(w != null) {
            e.setRespawnLocation(w.getWorld().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(PlayerSpawnLocationEvent e) {
        SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(e.getPlayer());
        if(w != null) {
            e.setSpawnLocation(w.getWorld().getSpawnLocation());
        }
    }
}
