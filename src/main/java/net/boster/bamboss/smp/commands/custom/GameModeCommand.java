package net.boster.bamboss.smp.commands.custom;

import com.google.common.collect.ImmutableList;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.ModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GameModeCommand extends ModCommand<GameMode> {

    private final List<String> modes = ImmutableList.of("0", "1", "2", "3");

    public GameModeCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "gameMode", section);
    }

    @Override
    protected @Nullable GameMode request(@NotNull CommandSender sender, @NotNull String arg) {
        GameMode mode = null;

        try {
            mode = GameMode.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            switch (arg) {
                case "0":
                    mode = GameMode.SURVIVAL;
                    break;
                case "1":
                    mode = GameMode.CREATIVE;
                    break;
                case "2":
                    mode = GameMode.ADVENTURE;
                    break;
                case "3":
                    mode = GameMode.SPECTATOR;
            }
        }

        if(mode == null) {
            sender.sendMessage(Utils.toColor(config.getString("noSuchGameMode").replace("%arg%", arg)));
        }

        return mode;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull GameMode gameMode) {
        player.setGameMode(gameMode);
        player.sendMessage(Utils.toColor(config.getString("success").replace("%mode%", getGameModeName(gameMode))));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull GameMode gameMode, @NotNull Player target) {
        target.setGameMode(gameMode);
        sender.sendMessage(Utils.toColor(config.getString("others")
                .replace("%target%", target.getName()).replace("%mode%", getGameModeName(gameMode))));
    }

    private String getGameModeName(GameMode mode) {
        return config.getString(mode.name(), mode.name());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length < 2) {
            return modes;
        }

        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
