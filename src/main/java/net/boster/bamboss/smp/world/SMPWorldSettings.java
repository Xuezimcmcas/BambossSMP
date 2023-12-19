package net.boster.bamboss.smp.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.boster.bamboss.smp.utils.Defaults;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SMPWorldSettings {

    @NotNull private final SMPWorld world;

    @Nullable private String joinMessage = Defaults.JOIN_MESSAGE;
    @Nullable private String quitMessage = Defaults.QUIT_MESSAGE;
    @Nullable private String deathMessage = Defaults.DEATH_MESSAGE;
    @Nullable private String deathByPlayerMessage = Defaults.DEATH_BY_PLAYER_MESSAGE;
    @Nullable private String prefix;

    private boolean weatherLocked = false;
    private boolean accessible = true;
    private boolean pvp = true;

    private int maxPlayers = Defaults.MAXIMUM_PLAYERS;

    @Nullable private Material icon;
    @Nullable private GameMode gameMode;

    @NotNull private List<String> lore = new ArrayList<>();

    public SMPWorldSettings(@NotNull SMPWorld world, @NotNull ConfigurationSection section) {
        this.world = world;

        deserialize(section);
    }

    @Contract("!null -> !null")
    public GameMode getGameMode(@Nullable GameMode def) {
        return gameMode != null ? gameMode : def;
    }

    public @NotNull String getPrefixOrName() {
        return prefix != null ? prefix : world.getName();
    }

    public void onJoin(@NotNull Player p) {
        if(joinMessage != null) {
            String s = Utils.toColor(joinMessage).replace("%player%", p.getName());

            for(Player o : world.getOnlinePlayers()) {
                o.sendMessage(s);
            }
        }
    }

    public void onQuit(@NotNull Player p) {
        if(quitMessage != null) {
            String s = Utils.toColor(quitMessage).replace("%player%", p.getName());

            for(Player o : world.getOnlinePlayers()) {
                o.sendMessage(s);
            }
        }
    }

    public void onDeath(@NotNull Player p) {
        if(deathMessage != null) {
            String s = Utils.toColor(deathMessage).replace("%player%", p.getName());

            for(Player o : world.getOnlinePlayers()) {
                o.sendMessage(s);
            }
        }
    }

    public void onDeathByKiller(@NotNull Player player, @NotNull Player killer) {
        if(deathByPlayerMessage != null) {
            String s = Utils.toColor(deathByPlayerMessage).replace("%player%", player.getName())
                    .replace("%killer%", killer.getName());

            for(Player o : world.getOnlinePlayers()) {
                o.sendMessage(s);
            }
        }
    }

    private void deserialize(@NotNull ConfigurationSection section) {
        for(Field f : SMPWorldSettings.class.getDeclaredFields()) {
            try {
                if(Modifier.isFinal(f.getModifiers())) continue;

                String s = f.getName();
                Class<?> c = f.getType();

                if(c.isEnum()) {
                    String d = section.getString(s);
                    if(d != null) {
                        f.set(this, c.getMethod("valueOf", String.class).invoke(null, d));
                    }
                } else if(c == String.class || c == boolean.class || c == int.class || c == double.class || c == float.class) {
                    String className = c.getSimpleName();
                    f.set(this, MemorySection.class.getMethod("get" + className.substring(0, 1).toUpperCase() + className.substring(1), String.class)
                            .invoke(section, s));
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        lore = section.getStringList("lore");
    }

    public void serialize(@NotNull ConfigurationSection section) {
        for(Field f : SMPWorldSettings.class.getDeclaredFields()) {
            try {
                if(Modifier.isFinal(f.getModifiers())) continue;

                Object o = f.get(this);
                Class<?> c = f.getType();

                if(o instanceof List) {
                    section.set(f.getName(), o);
                } else if(o != null && (c == String.class || c == boolean.class || c == int.class || c == double.class || c == float.class)) {
                    section.set(f.getName(), o);
                } else {
                    section.set(f.getName(), o != null ? o.toString() : null);
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}
