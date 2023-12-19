package net.boster.bamboss.smp.managers;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import lombok.Getter;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.files.SMPFile;
import net.boster.bamboss.smp.user.SMPUser;
import net.boster.bamboss.smp.utils.Defaults;
import net.boster.bamboss.smp.utils.WorldUtils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class SMPWorldManager {

    @Getter private final List<String> potentialWorlds = new ArrayList<>();
    private final Map<String, SMPWorld> map = new HashMap<>();

    @Getter private World lobbyWorld;

    public @NotNull SMPWorld create(@NotNull Player player) {
        return create(BambossSMP.getInstance().getUserManager().get(player));
    }

    public @NotNull SMPWorld create(@NotNull SMPUser owner) {
        return create(owner, owner.getName());
    }

    public @NotNull SMPWorld create(@NotNull SMPUser owner, @NotNull String name) {
        MultiverseWorld w = WorldUtils.createWorld(name);

        SMPFile file = new SMPFile(SMPWorld.getFile(name));
        file.createFile();

        SMPWorld world = new SMPWorld(file, owner, new ArrayList<>(), Objects.requireNonNull(Bukkit.getWorld(name)), w);
        map.put(name, world);

        world.setWorldBorder(Defaults.WORLD_BORDER);
        world.setupChat();

        return world;
    }

    public SMPWorld get(@NotNull Player player) {
        return get(BambossSMP.getInstance().getUserManager().get(player));
    }

    public SMPWorld getWorld(@NotNull Player player) {
        return getWorld(player.getWorld().getName());
    }

    public SMPWorld getWorld(@NotNull String world) {
        return get(removeWorldExtensionIfExists(world));
    }

    public @NotNull String removeWorldExtensionIfExists(@NotNull String world) {
        return world.replace("_nether", "").replace("_the_end", "");
    }

    public SMPWorld get(@NotNull SMPUser user) {
        for(SMPWorld w : all()) {
            if(w.getOwner() == user) return w;
        }

        return null;
    }

    public SMPWorld get(@NotNull String name) {
        return map.get(name);
    }

    public void remove(@NotNull String name) {
        map.remove(name);
    }

    public void remove(@NotNull SMPWorld world) {
        remove(world.getName());
    }

    public void load() {
        File f = new File(BambossSMP.getInstance().getDataFolder(), "worlds");
        if(!f.exists()) return;

        for(File uf : f.listFiles()) {
            if(!uf.getName().endsWith(".yml")) continue;

            potentialWorlds.add(uf.getName().split(".yml")[0]);
        }
    }

    public void loadWorlds() {
        lobbyWorld = Bukkit.getWorld(Objects.requireNonNull(BambossSMP.getInstance().getConfig().getString("Settings.LobbyWorld")));

        for(String s : new ArrayList<>(potentialWorlds)) {
            if(Bukkit.getWorld(s) != null) {
                loadWorld(s);
            }
        }
    }

    public void loadWorld(@NotNull String name) {
        if(!potentialWorlds.contains(name)) return;

        SMPFile file = new SMPFile(SMPWorld.getFile(name));
        file.loadConfig();

        SMPWorld world = new SMPWorld(file);
        map.put(world.getName(), world);

        world.setupChat();

        potentialWorlds.remove(name);
    }

    public boolean completelyLoaded() {
        return potentialWorlds.isEmpty();
    }

    public Collection<SMPWorld> all() {
        return new HashMap<>(map).values();
    }
}
