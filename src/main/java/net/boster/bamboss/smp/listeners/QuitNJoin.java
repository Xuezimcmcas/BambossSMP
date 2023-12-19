package net.boster.bamboss.smp.listeners;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.world.SMPUserData;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class QuitNJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent e) {
        if(!BambossSMP.getInstance().getWorldManager().completelyLoaded()) {
            e.disallow(null, "Server hasn't loaded yet.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        BambossSMP.getInstance().getUserManager().update(p);

        SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(p.getWorld().getName());
        if(w != null) {
            e.setJoinMessage(null);

            if((!w.canJoin(p, true) || !w.getPunishments().checkBanned(p)) &&
                    BambossSMP.getInstance().getWorldManager().getLobbyWorld() != null) {
                p.teleport(BambossSMP.getInstance().getWorldManager().getLobbyWorld().getSpawnLocation());
                return;
            }

            w.getSettings().onJoin(p);

            SMPUserData data = w.getUserData().get(p.getUniqueId());
            if(data != null) {
                if(data.getQuitLocation() != null) {
                    p.teleport(data.getQuitLocation());
                }
                if(data.getGameMode() != null) {
                    p.setGameMode(data.getGameMode());
                }
                data.applyEffects(p);
            } else {
                p.setGameMode(w.getSettings().getGameMode(GameMode.SURVIVAL));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(p.getWorld().getName());
        if(w != null) {
            e.setQuitMessage(null);
            w.getSettings().onQuit(p);

            SMPUserData d = w.getUserData().getOrCreate(p.getUniqueId());
            d.setQuitLocation(p.getLocation());
            d.setGameMode(p.getGameMode());
            d.saveEffects(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        if(e.getTo() != null) {
            if(e.getFrom().getWorld() == e.getTo().getWorld()) return;

            SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(e.getFrom().getWorld().getName());
            SMPWorld t = BambossSMP.getInstance().getWorldManager().getWorld(e.getTo().getWorld().getName());

            if(w == t) return;

            if(t != null) {
                if(!t.canJoin(p, true) || !t.getPunishments().checkBanned(p)) {
                    e.setCancelled(true);
                    return;
                }

                if(w != null) {
                    w.getSettings().onQuit(p);
                    SMPUserData d = w.getUserData().getOrCreate(p.getUniqueId());
                    d.setQuitLocation(e.getFrom());
                    d.setGameMode(p.getGameMode());
                    d.saveEffects(p);
                }

                t.getSettings().onJoin(p);
                SMPUserData d = t.getUserData().get(p.getUniqueId());
                if(d != null) {
                    if(d.getQuitLocation() != null) {
                        e.setTo(d.getQuitLocation());
                    }
                    if(d.getGameMode() != null) {
                        p.setGameMode(d.getGameMode());
                    }
                    d.applyEffects(p);
                } else {
                    p.setGameMode(t.getSettings().getGameMode(GameMode.SURVIVAL));
                }
            }
        }
    }
}
