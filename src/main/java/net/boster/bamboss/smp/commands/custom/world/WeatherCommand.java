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

public class WeatherCommand extends TypedModCommand<Boolean> {

    private final List<String> tabCompletions = ImmutableList.of("clear", "rain");

    public WeatherCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "weather", section);
    }

    @Override
    public Boolean get(@NotNull String a) {
        if(a.equalsIgnoreCase("clear") || a.equalsIgnoreCase("sun") || a.equalsIgnoreCase("sunny")) {
            return true;
        } else if(a.equalsIgnoreCase("rain") || a.equalsIgnoreCase("storm")) {
            return false;
        }
        return null;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull Boolean a) {
        String weather;
        if(a) {
            weather = config.getString("Clear");
        } else {
            weather = config.getString("Storm");
        }

        player.getWorld().setStorm(!a);
        player.sendMessage(Utils.toColor(config.getString("success").replace("%weather%", weather)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return tabCompletions;
    }
}
