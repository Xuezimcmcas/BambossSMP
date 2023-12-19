package net.boster.bamboss.smp.listeners;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(p.getWorld().getName());
        if(w != null) {
            e.setDeathMessage(null);

            if(p.getKiller() != null) {
                w.getSettings().onDeathByKiller(p, p.getKiller());
            } else {
                w.getSettings().onDeath(p);
            }
        }
    }
}
