package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.CommandWrapper;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PvPCommand extends CommandWrapper {

    public PvPCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "pvp", section);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPlayer(sender)) return true;
        if(!checkPermission(sender)) return true;

        Player p = (Player) sender;
        SMPWorld world = BambossSMP.getInstance().getWorldManager().getWorld(p);

        if (world == null) {
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.notOnAnSMP")));
            return true;
        }

        world.getSettings().setPvp(!world.getSettings().isPvp());
        if(world.getSettings().isPvp()) {
            p.sendMessage(Utils.toColor(config.getString("enabled")));
        } else {
            p.sendMessage(Utils.toColor(config.getString("disabled")));
        }
        return true;
    }
}
