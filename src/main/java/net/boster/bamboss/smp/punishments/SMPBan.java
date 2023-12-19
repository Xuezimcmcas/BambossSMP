package net.boster.bamboss.smp.punishments;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Data
public class SMPBan implements Serializable {

    private static final long serialVersionUID = 7771721L;

    @NotNull private final UUID playerUUID;
    @NotNull private final String playerName;

    @NotNull private final UUID adminUUID;
    @NotNull private final String adminName;

    @NotNull private final String reason;
}
