package net.boster.bamboss.smp.hook;

public class PAPIHook {

    public static void load() {
        try {
            Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");
            new PAPIExpansion().register();
        } catch (Throwable ignored) {}
    }
}
