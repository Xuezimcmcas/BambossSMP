package net.boster.bamboss.smp.commands.custom;

import com.google.common.collect.ImmutableList;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.ModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class SpeedChangeCommand extends ModCommand<Double> {

    protected final @NotNull Attribute attribute;
    private final List<String> tabCompletions = ImmutableList.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    public SpeedChangeCommand(@NotNull BambossSMP plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull Attribute attribute) {
        super(plugin, name, section);
        this.attribute = attribute;
    }

    @Override
    protected @Nullable Double request(@NotNull CommandSender sender, @NotNull String arg) {
        try {
            double d = Double.parseDouble(arg);

            if(d < 0 || d > 10) {
                sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.illegalSpeed").replace("%arg%", arg)));
                return null;
            }

            if(d == 10) {
                d = 1;
            }

            if(d > 1) {
                d = Double.parseDouble("0." + d);
            }

            return d;
        } catch (Exception e) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.notNumber").replace("%arg%", arg)));
            return null;
        }
    }

    @Override
    public void execute(@NotNull Player player, @NotNull Double aDouble) {
        player.getAttribute(attribute).setBaseValue(aDouble);
        player.sendMessage(Utils.toColor(config.getString("success").replace("%speed%", aDouble + "")));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Double aDouble, @NotNull Player target) {
        target.getAttribute(attribute).setBaseValue(aDouble);
        sender.sendMessage(Utils.toColor(config.getString("others").replace("%speed%", aDouble + "")));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if(args.length < 2) {
            return tabCompletions;
        }

        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
