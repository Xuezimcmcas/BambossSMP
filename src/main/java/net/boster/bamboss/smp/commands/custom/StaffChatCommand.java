package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.CommandWrapper;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatCommand extends CommandWrapper {

    public StaffChatCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "staffChat", section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPermission(sender)) return true;

        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        StringBuilder msg = new StringBuilder();
        String a = "";
        for(String arg : args) {
            msg.append(a).append(arg);
            a = " ";
        }

        String s = Utils.toColor(config.getString("format").replace("%sender%", sender.getName()).replace("%message%", msg.toString()));

        if(!(sender instanceof Player)) {
            sender.sendMessage(s);
        }
        Bukkit.getOnlinePlayers().stream().filter(this::hasPermission).forEach(p -> p.sendMessage(s));
        return true;
    }
}
