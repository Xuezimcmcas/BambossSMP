package net.boster.bamboss.smp.utils.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChatInputHandler {

    public static final Map<Player, IChatInputHandler> map = new HashMap<>();

    public static void addPlayer(@NotNull Player player, @NotNull IChatInputHandler iChatInputHandler) {
        map.put(player, iChatInputHandler);
    }

    public static boolean contains(@NotNull Player player) {
        return map.containsKey(player);
    }

    public static void handle(@NotNull Player player, @NotNull String message) {
        IChatInputHandler i = map.get(player);
        if(i == null) return;

        if(i.handle(message)) {
           return;
        }

        map.remove(player);
    }
}
