package net.boster.bamboss.smp.commands.custom.punishments;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.PunishmentCommand;
import net.boster.bamboss.smp.utils.Constants;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends PunishmentCommand<Player> {

    public KickCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "kick", section);
    }

    @Override
    public Player validate(@NotNull String s) {
        return null;
    }

    @Override
    public @NotNull String getDefReason() {
        return Constants.KICK_REASON;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull SMPWorld smp, @NotNull Player punished, @NotNull String reason) {
        player.sendMessage(Utils.toColor(config.getString("success").replace("%player%", punished.getName())
                .replace("%reason%", reason)));

        punished.teleport(plugin.getWorldManager().getLobbyWorld().getSpawnLocation());
        for(String s : BambossSMP.getInstance().getConfig().getStringList("Messages.kick.playerKicked")) {
            player.sendMessage(Utils.toColor(s.replace("%server%", smp.getSettings().getPrefixOrName())
                    .replace("%player%", player.getName())
                    .replace("%reason%", reason)));
        }
    }
}
