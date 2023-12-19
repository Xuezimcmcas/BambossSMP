package net.boster.bamboss.smp.listeners;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLoad(WorldLoadEvent e) {
        BambossSMP.getInstance().getWorldManager().loadWorld(e.getWorld().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent e) {
        SMPWorld world = BambossSMP.getInstance().getWorldManager().getWorld(e.getWorld().getName());
        if(world != null && world.getSettings().isWeatherLocked()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && e.getFrom().getWorld().getName().endsWith("_the_end")) {
            SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(e.getFrom().getWorld().getName());
            if(w == null) return;

            Bukkit.getScheduler().runTask(BambossSMP.getInstance(), () -> {
                w.getWorld().getSpawnLocation();
            });
        }
    }
}
