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

import java.util.concurrent.TimeUnit;

public class TPACommand extends TypedCommand<Player> {

    private final long requestValidTime;

    public TPACommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "tpa", section);
        this.requestValidTime = TimeUnit.SECONDS.toMillis(section.getInt("requestValidTime", 90));
    }

    @Override
    public Player get(@NotNull Player p, @NotNull String argument) {
        SMPWorld world = plugin.getWorldManager().getWorld(p);
        if(world == null) return null;

        return world.getPlayer(argument);
    }

    @Override
    public void execute(@NotNull Player p, @NotNull Player p2) {
        SMPUser user = plugin.getUserManager().get(p2);
        SMPTeleportInvite i = user.getTeleportInvites().get(p);
        if(i != null && i.isValid()) {
            p.sendMessage(Utils.toColor(config.getString("already")));
            return;
        }

        user.getTeleportInvites().put(p, new SMPTeleportInvite(System.currentTimeMillis() + requestValidTime,
                plugin.getWorldManager().getWorld(p), p));
        p.sendMessage(Utils.toColor(config.getString("success").replace("%name%", user.getName())));
        for(String s : config.getStringList("receive")) {
            Utils.sendTextComponent(p2, Utils.toColor(s.replace("%player%", p.getName())));
        }
    }
}
