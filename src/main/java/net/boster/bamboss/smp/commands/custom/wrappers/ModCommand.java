package net.boster.bamboss.smp.commands.custom.wrappers;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModCommand<T> extends CommandWrapper {

    public ModCommand(@NotNull BambossSMP plugin, @NotNull String name, @NotNull ConfigurationSection section) {
        super(plugin, name, section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPermission(sender)) return true;

        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        T t = request(sender, args[0]);
        if(t == null) return true;

        if(args.length < 2) {
            if(!checkPlayer(sender)) return true;

            execute((Player) sender, t);
        } else {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null) {
                sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer").replace("%name%", args[1])));
                return true;
            }

            execute(sender, t, p);
        }

        return true;
    }

    protected abstract @Nullable T request(@NotNull CommandSender sender, @NotNull String arg);

    public abstract void execute(@NotNull Player player, @NotNull T t);
    public abstract void execute(@NotNull CommandSender sender, @NotNull T t, @NotNull Player target);
}
