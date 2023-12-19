package net.boster.bamboss.smp.commands.custom.world;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.CommandWrapper;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InviteAcceptCommand extends CommandWrapper {

    public InviteAcceptCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "accept", section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPlayer(sender)) return true;

        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        Player p = (Player) sender;
        List<String> invites = InviteCommand.map.computeIfAbsent(p, o -> new ArrayList<>());

        if(!invites.contains(args[0])) {
            sender.sendMessage(Utils.toColor(config.getString("noInvites").replace("%name%", args[0])));
            return true;
        }

        SMPWorld w = plugin.getWorldManager().getWorld(args[0]);
        if(w == null) return true;

        invites.remove(args[0]);
        p.sendMessage(Utils.toColor(config.getString("success").replace("%name%", args[0])));
        p.teleport(w.getWorld().getSpawnLocation());
        return true;
    }
}
