package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.SimpleModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand extends SimpleModCommand {

    public FlyCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "fly", section);
    }

    @Override
    public void execute(@NotNull Player player) {
        if(player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(Utils.toColor(config.getString("selfOff")));
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(Utils.toColor(config.getString("selfOn")));
        }
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Player player) {
        if(player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            sender.sendMessage(Utils.toColor(config.getString("othersOff").replace("%player%", player.getName())));
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            sender.sendMessage(Utils.toColor(config.getString("othersOn").replace("%player%", player.getName())));
        }
    }
}
