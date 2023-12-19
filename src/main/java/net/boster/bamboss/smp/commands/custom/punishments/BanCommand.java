package net.boster.bamboss.smp.commands.custom.punishments;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.PunishmentCommand;
import net.boster.bamboss.smp.punishments.SMPBan;
import net.boster.bamboss.smp.utils.Constants;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BanCommand extends PunishmentCommand<OfflinePlayer> {

    public BanCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "ban", section);
    }

    @Override
    public OfflinePlayer validate(@NotNull String s) {
        return Bukkit.getOfflinePlayer(s);
    }

    @Override
    public @NotNull String getDefReason() {
        return Constants.BAN_REASON;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull SMPWorld smp, @NotNull OfflinePlayer punished, @NotNull String reason) {
        SMPBan ban = new SMPBan(punished.getUniqueId(), punished.getName(), player.getUniqueId(), player.getName(), reason);
        smp.getPunishments().getBanList().put(punished.getUniqueId(), ban);

        player.sendMessage(Utils.toColor(config.getString("success").replace("%player%", punished.getName())
                .replace("%reason%", reason)));

        if(punished.isOnline()) {
            Player p = punished instanceof Player ? (Player) punished : Bukkit.getPlayer(punished.getUniqueId());
            p.teleport(plugin.getWorldManager().getLobbyWorld().getSpawnLocation());
            Utils.sendBanMessage(p, smp, ban);
        }
    }
}
