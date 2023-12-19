package net.boster.bamboss.smp.commands.custom.wrappers;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleModCommand extends CommandWrapper {

    public SimpleModCommand(@NotNull BambossSMP plugin, @NotNull String name, @NotNull ConfigurationSection section) {
        super(plugin, name, section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPermission(sender)) return true;

        if(args.length < 1) {
            if(!checkPlayer(sender)) return true;

            execute((Player) sender);
        } else {
            Player p = Bukkit.getPlayer(args[0]);
            if(p == null) {
                sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer").replace("%name%", args[0])));
                return true;
            }

            execute(sender, p);
        }

        return true;
    }

    public abstract void execute(@NotNull Player player);
    public abstract void execute(@NotNull CommandSender sender, @NotNull Player target);
}
