package net.boster.bamboss.smp.utils;

import lombok.RequiredArgsConstructor;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class PermissionRequirement {

    @Nullable private final String permission;
    @Nullable private final String permissionAlert;

    public PermissionRequirement(@NotNull ConfigurationSection section) {
        this(section.getString("permission"), Utils.toColor(section.getString("permissionAlert",
                BambossSMP.getInstance().getConfig().getString("Messages.noPermission"))));
    }

    public boolean check(@NotNull Player player) {
        if(permission != null && !player.hasPermission(permission)) {
            if(permissionAlert != null) {
                player.sendMessage(permissionAlert);
            }
            return false;
        }

        return true;
    }
}
