package net.boster.bamboss.smp.commands.custom.world;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.TypedModCommand;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DifficultyCommand extends TypedModCommand<Difficulty> {

    public DifficultyCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "difficulty", section);
    }

    @Override
    public Difficulty get(@NotNull String argument) {
        try {
            return Difficulty.valueOf(argument.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void execute(@NotNull Player player, @NotNull Difficulty difficulty) {
        SMPWorld world = plugin.getWorldManager().getWorld(player);
        world.setDifficulty(difficulty);
        player.sendMessage(Utils.toColor(config.getString("success").replace("%arg%", config.getString(difficulty.name(), difficulty.name()))));
    }
}
