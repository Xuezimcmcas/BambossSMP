package net.boster.bamboss.smp.commands.custom.punishments;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.CommandWrapper;
import net.boster.bamboss.smp.punishments.SMPBan;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnbanCommand extends CommandWrapper {

    public UnbanCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "unban", section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPermission(sender)) return true;
        if(!checkPlayer(sender)) return true;

        Player p = (Player) sender;

        if(args.length == 0) {
            sender.sendMessage(Utils.toColor(config.getString("usage")));
            return true;
        }

        SMPWorld smp = plugin.getWorldManager().getWorld(p);
        if(smp == null) {
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.notOnAnSMP")));
            return true;
        }

        SMPBan ban = smp.getPunishments().getBan(args[0]);
        if(ban == null) {
            sender.sendMessage(Utils.toColor(config.getString("notBanned").replace("%name%", args[0])));
            return true;
        }

        smp.getPunishments().getBanList().remove(ban.getPlayerUUID());
        sender.sendMessage(Utils.toColor(config.getString("success").replace("%player%", args[0])));
        return true;
    }
}
