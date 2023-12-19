package net.boster.bamboss.smp.utils.chat;

import org.jetbrains.annotations.NotNull;

public interface IChatInputHandler {

    boolean handle(@NotNull String message);
}
