package net.boster.bamboss.smp.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Constants {

    public static String SERVER_FULL_MESSAGE;
    public static String LOBBY_WORLD_PREFIX;
    public static String BAN_REASON;
    public static String KICK_REASON;

    public static void load(@NotNull ConfigurationSection cfg) {
        Defaults.load(cfg);

        SERVER_FULL_MESSAGE = Utils.toColor(Utils.parseList(cfg.getStringList("Settings.ServerFullMessage")));
        LOBBY_WORLD_PREFIX = Utils.toColor(cfg.getString("Settings.LobbyWorldPrefix"));
        BAN_REASON = Utils.toColor(cfg.getString("Settings.DefaultBanReason"));
        KICK_REASON = Utils.toColor(cfg.getString("Settings.DefaultKickReason"));
    }
}
