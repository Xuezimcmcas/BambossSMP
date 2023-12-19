package net.boster.bamboss.smp.utils;

import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Defaults {

    public static int MAXIMUM_PLAYERS;
    public static int WORLD_BORDER;

    public static Difficulty DIFFICULTY;

    public static String JOIN_MESSAGE;
    public static String QUIT_MESSAGE;
    public static String DEATH_MESSAGE;
    public static String DEATH_BY_PLAYER_MESSAGE;

    public static void load(@NotNull ConfigurationSection cfg) {
        for(Field f : Defaults.class.getFields()) {
            try {
                String s = f.getName();
                Class<?> c = f.getType();
                if(c.isEnum()) {
                    try {
                        f.set(null, c.getMethod("valueOf", String.class).invoke(null, cfg.getString("Defaults." + s)));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        f.set(null, ((Object[]) c.getMethod("values").invoke(null))[0]);
                    }
                } else {
                    f.set(null, cfg.get("Defaults." + f.getName()));
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}
