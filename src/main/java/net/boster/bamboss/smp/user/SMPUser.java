package net.boster.bamboss.smp.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.files.SMPFile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class SMPUser {

    @NotNull private String name;
    @NotNull private final UUID uuid;
    @NotNull private final List<Material> premiumIcons;
    @NotNull private final SMPFile file;

    @NotNull private Map<Player, SMPTeleportInvite> teleportInvites = new HashMap<>();

    public SMPUser(@NotNull Player player) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.premiumIcons = new ArrayList<>();

        this.file = new SMPFile(getFile(player.getUniqueId()));

        file.createFile();
    }

    public static @NotNull File getFile(@NotNull UUID id) {
        return new File(BambossSMP.getInstance().getDataFolder(), "users/" + id + ".yml");
    }
}
