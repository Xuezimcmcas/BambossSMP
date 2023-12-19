package net.boster.bamboss.smp.user;

import lombok.Data;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
public class SMPTeleportInvite {

    private final long validUntil;
    @NotNull private final SMPWorld world;
    @NotNull private final Player player;

    public boolean isValid() {
        return validUntil > System.currentTimeMillis();
    }
}
