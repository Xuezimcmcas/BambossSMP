package net.boster.bamboss.smp.commands.custom.wrappers;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PunishmentCommand<T> extends CommandWrapper {

    public PunishmentCommand(@NotNull BambossSMP plugin, @NotNull String name, @NotNull ConfigurationSection section) {
        super(plugin, name, section);
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

        T punished = validate(args[0]);
        if(punished == null) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer").replace("%name%", args[0])));
            return true;
        }

        execute(p, smp, punished, args.length >= 2 ? args[1] : getDefReason());
        return true;
    }

    public abstract T validate(@NotNull String s);
    public abstract @NotNull String getDefReason();
    public abstract void execute(@NotNull Player player, @NotNull SMPWorld smp, @NotNull T punished, @NotNull String reason);
}
