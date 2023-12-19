package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.SimpleModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand extends SimpleModCommand implements Listener {

    private final List<Player> players = new ArrayList<>();

    public VanishCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "vanish", section);
    }

    @Override
    public void execute(@NotNull Player player) {
        if(players.contains(player)) {
            showPlayer(player);
            player.sendMessage(Utils.toColor(config.getString("selfOff")));
        } else {
            hidePlayer(player);
            player.sendMessage(Utils.toColor(config.getString("selfOn")));
        }
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Player player) {
        if(players.contains(player)) {
            showPlayer(player);
            sender.sendMessage(Utils.toColor(config.getString("othersOff").replace("%player%", player.getName())));
        } else {
            hidePlayer(player);
            sender.sendMessage(Utils.toColor(config.getString("othersOn").replace("%player%", player.getName())));
        }
    }

    protected void hidePlayer(@NotNull Player p) {
        players.add(p);
        Bukkit.getOnlinePlayers().forEach(o -> o.hidePlayer(plugin, p));
    }

    protected void showPlayer(@NotNull Player p) {
        players.remove(p);
        Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(plugin, p));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        players.forEach(i -> p.hidePlayer(plugin, i));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }
}
