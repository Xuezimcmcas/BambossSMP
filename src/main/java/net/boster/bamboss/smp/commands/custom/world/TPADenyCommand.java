package net.boster.bamboss.smp.commands.custom.world;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.TypedCommand;
import net.boster.bamboss.smp.user.SMPTeleportInvite;
import net.boster.bamboss.smp.user.SMPUser;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPADenyCommand extends TypedCommand<Player> {

    public TPADenyCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "tpadeny", section);
    }

    @Override
    public Player get(@NotNull Player p, @NotNull String argument) {
        SMPWorld world = plugin.getWorldManager().getWorld(p);
        if(world == null) return null;

        return world.getPlayer(argument);
    }

    @Override
    public void execute(@NotNull Player p, @NotNull Player p2) {
        SMPUser user = plugin.getUserManager().get(p);
        SMPTeleportInvite i = user.getTeleportInvites().get(p2);
        if(i == null || !i.isValid()) {
            p.sendMessage(Utils.toColor(config.getString("noRequests")));
            return;
        }

        user.getTeleportInvites().remove(p2);
        p.sendMessage(Utils.toColor(config.getString("success")));
        p2.sendMessage(Utils.toColor(config.getString("requester").replace("%player%", p.getName())));
    }
}
