package net.boster.bamboss.smp.commands.custom.world;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.CommandWrapper;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteCommand extends CommandWrapper {

    public static final Map<Player, List<String>> map = new HashMap<>();

    public InviteCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "invite", section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPermission(sender)) return true;
        if(!checkPlayer(sender)) return true;

        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        Player p = (Player) sender;
        SMPWorld w = plugin.getWorldManager().getWorld(p);
        if(w == null) {
            sendNoPermission(sender);
            return true;
        }

        Player pa = Bukkit.getPlayer(args[0]);
        if(pa == null) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer").replace("%name%", args[0])));
            return true;
        }

        List<String> invites = map.computeIfAbsent(pa, o -> new ArrayList<>());
        if(invites.contains(w.getName())) {
            sender.sendMessage(Utils.toColor(config.getString("already").replace("%name%", args[0])));
            return true;
        }

        invites.add(w.getName());
        p.sendMessage(Utils.toColor(config.getString("success").replace("%player%", pa.getName())));
        for(String s : config.getStringList("receive")) {
            Utils.sendTextComponent(pa, Utils.toColor(s.replace("%player%", p.getName())));
        }
        return true;
    }
}
