package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.ModCommand;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TPCommand extends ModCommand<Player> {

    public TPCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "teleport", section);
    }

    @Override
    protected @Nullable Player request(@NotNull CommandSender sender, @NotNull String arg) {
        Player p = Bukkit.getPlayer(arg);
        if(p == null) {
            sendNullPlayer(sender, arg);
            return null;
        }

        return p;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull Player player2) {
        if(plugin.getWorldManager().getWorld(player) != plugin.getWorldManager().getWorld(player2)) {
            sendNullPlayer(player, player2.getName());
            return;
        }

        player.teleport(player2);
        player.sendMessage(Utils.toColor(config.getString("success").replace("%player%", player2.getName())));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Player player, @NotNull Player target) {
        if(sender instanceof Player) {
            SMPWorld w = plugin.getWorldManager().getWorld((Player) sender);
            if(w != plugin.getWorldManager().getWorld(player)) {
                sendNullPlayer(sender, player.getName());
                return;
            }

            if(w != plugin.getWorldManager().getWorld(target)) {
                sendNullPlayer(sender, target.getName());
                return;
            }
        }

        player.teleport(target);
        sender.sendMessage(Utils.toColor(config.getString("others").replace("%player%", player.getName())
                .replace("%target%", target.getName())));
    }

    private void sendNullPlayer(@NotNull CommandSender sender, @NotNull String arg) {
        sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer").replace("%name%", arg)));
    }
}
