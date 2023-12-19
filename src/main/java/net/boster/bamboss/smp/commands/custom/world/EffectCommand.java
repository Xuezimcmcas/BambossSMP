package net.boster.bamboss.smp.commands.custom.world;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.CommandWrapper;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EffectCommand extends CommandWrapper {

    public EffectCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "effect", section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPlayer(sender)) return true;
        if(!checkPermission(sender)) return true;

        Player p = (Player) sender;

        if(args.length < 4) {
            sender.sendMessage(Utils.toColor(config.getString("usage")));
            return true;
        }

        Player a = Bukkit.getPlayer(args[0]);
        if(a == null) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer")
                    .replace("%name%", args[0])));
            return true;
        }

        PotionEffectType type = PotionEffectType.getByName(args[1]);
        if(type == null) {
            p.sendMessage(Utils.toColor(config.getString("noEffect").replace("%arg%", args[1])));
            return true;
        }

        int level;
        try {
            level = Math.max(0, Integer.parseInt(args[2]));
        } catch (Exception e) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.notNumber")
                    .replace("%arg%", args[2])));
            return true;
        }

        int time;
        try {
            time = Integer.parseInt(args[3]);
        } catch (Exception e) {
            sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.notNumber")
                    .replace("%arg%", args[3])));
            return true;
        }

        a.addPotionEffect(new PotionEffect(type, level, time));
        p.sendMessage(Utils.toColor(config.getString("success").replace("%player%", a.getName())
                .replace("%effect%", type.getName())));
        return true;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 2) {
            return Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toList());
        }

        return null;
    }
}
