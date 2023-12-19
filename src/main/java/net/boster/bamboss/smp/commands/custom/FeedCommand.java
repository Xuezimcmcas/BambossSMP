package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.SimpleModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommand extends SimpleModCommand {

    public FeedCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "feed", section);
    }

    @Override
    public void execute(@NotNull Player player) {
        player.setFoodLevel(25);
        player.sendMessage(Utils.toColor(config.getString("success")));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Player target) {
        target.setFoodLevel(25);
        sender.sendMessage(Utils.toColor(config.getString("others").replace("%player%", target.getName())));
    }
}
