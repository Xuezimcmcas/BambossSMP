package net.boster.bamboss.smp.world;

import lombok.Data;
import net.boster.bamboss.smp.punishments.SMPBan;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class SMPWorldPunishments {

    @NotNull private final SMPWorld world;

    @NotNull private Map<UUID, SMPBan> banList = new HashMap<>();

    public void load(@NotNull ConfigurationSection section) {
        String bs = section.getString("BanList");
        if(bs != null) {
            banList = Utils.decodeSimple(bs);
        }
    }

    public void save(@NotNull ConfigurationSection section) {
        section.set("BanList", Utils.encode(banList));
    }

    public boolean checkBanned(@NotNull Player p) {
        SMPBan ban = banList.get(p.getUniqueId());
        if(ban != null) {
            Utils.sendBanMessage(p, world, ban);
            return false;
        }

        return true;
    }

    public @Nullable SMPBan getBan(@NotNull String player) {
        for(SMPBan ban : banList.values()) {
            if(ban.getPlayerName().equals(player)) {
                return ban;
            }
        }

        return null;
    }
}
