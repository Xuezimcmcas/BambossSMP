package net.boster.bamboss.smp.commands.custom.world;

import com.google.common.collect.ImmutableList;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.TypedModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TimeCommand extends TypedModCommand<Integer> {

    private final List<String> tabCompletions = ImmutableList.of("day", "night");

    public TimeCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "time", section);
    }

    @Override
    public Integer get(@NotNull String argument) {
        try {
            int i = Integer.parseInt(argument);
            if(i < 0) {
                i = 0;
            } else if(i > 24000) {
                i = 24000;
            }
            return i;
        } catch (Exception e) {
            if(argument.equalsIgnoreCase("day") || argument.equalsIgnoreCase("d")) {
                return 1000;
            } else if(argument.equalsIgnoreCase("night") || argument.equalsIgnoreCase("n")) {
                return 14000;
            }
        }
        return null;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull Integer i) {
        String time;
        if(i <= 13000 || i > 23000) {
            time = config.getString("Day");
        } else {
            time = config.getString("Night");
        }

        player.getWorld().setTime(i);
        player.sendMessage(Utils.toColor(config.getString("success").replace("%time%", time)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return tabCompletions;
    }
}
