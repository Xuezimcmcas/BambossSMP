package net.boster.bamboss.smp.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ReflectionUtils {
    public static final String version;

    private static SimpleCommandMap commandMap;

    static {
        version = Version.getCurrentVersion().name();

        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void registerCommand(@NotNull Command command) {
        try {
            commandMap.register("smp", command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregisterCommand(@NotNull Command command) {
        try {
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            Map<String, Command> map = (Map<String, Command>) knownCommands.get(commandMap);
            map.remove(command.getName());
            map.remove( "smp:" + command.getName());
            for(String alias : command.getAliases()) {
                map.remove(alias);
                map.remove("smp:" + alias);
            }
            command.unregister(commandMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncCommands() {
        try {
            if(Version.getCurrentVersion().getVersionInteger() >= 12) {
                Method m = Bukkit.getServer().getClass().getMethod("syncCommands");
                m.setAccessible(true);
                m.invoke(Bukkit.getServer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
