package net.boster.bamboss.smp.commands.custom.wrappers;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class TypedCommand<T> extends CommandWrapper {

    public TypedCommand(@NotNull BambossSMP plugin, @NotNull String name, @NotNull ConfigurationSection section) {
        super(plugin, name, section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!checkPlayer(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage(Utils.toColor(config.getString("usage")));
            return true;
        }

        Player p = (Player) sender;

        T t = get(p, args[0]);
        if (t == null) {
            sender.sendMessage(Utils.toColor(config.getString("invalidArg").replace("%arg%", args[0])));
            return true;
        }

        execute(p, t);
        return true;
    }

    public abstract T get(@NotNull Player p, @NotNull String argument);

    public abstract void execute(@NotNull Player player, @NotNull T t);
}
