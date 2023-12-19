package net.boster.bamboss.smp.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

public class WorldUtils {

    private static MultiverseCore mvCore;
    private static MultiverseNetherPortals mvPortals;

    public static @NotNull MultiverseCore getMvCore() {
        if(mvCore == null) {
            mvCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        }

        return mvCore;
    }

    public static @NotNull MultiverseNetherPortals getMvPortals() {
        if(mvPortals == null) {
            mvPortals = (MultiverseNetherPortals) Bukkit.getPluginManager().getPlugin("Multiverse-NetherPortals");
        }

        return mvPortals;
    }

    public static @NotNull MultiverseWorld createWorld(@NotNull String name) {
        MVWorldManager wm = getMvCore().getMVWorldManager();
        if(wm.getMVWorld(name) != null) {
            deleteWorld(name);
        }

        wm.addWorld(name, World.Environment.NORMAL, null, WorldType.NORMAL, true, null ,false);
        wm.addWorld(name + "_nether", World.Environment.NETHER, null, WorldType.NORMAL, true, null ,false);
        wm.addWorld(name + "_the_end", World.Environment.THE_END, null, WorldType.NORMAL, true, null ,false);

        MultiverseWorld w = wm.getMVWorld(name);
        MultiverseWorld wn = wm.getMVWorld(name + "_nether");
        MultiverseWorld we = wm.getMVWorld(name + "_the_end");

        w.setDifficulty(Defaults.DIFFICULTY);
        wn.setDifficulty(Defaults.DIFFICULTY);
        we.setDifficulty(Defaults.DIFFICULTY);

        w.getCBWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        wn.getCBWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        we.getCBWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        wn.setRespawnToWorld(name);
        we.setRespawnToWorld(name);

        MultiverseNetherPortals m = getMvPortals();
        m.addWorldLink(name, name + "_nether", PortalType.NETHER);
        m.addWorldLink(name + "_nether", name, PortalType.NETHER);

        m.addWorldLink(name, name + "_the_end", PortalType.ENDER);
        m.addWorldLink(name + "_the_end", name, PortalType.ENDER);

        return w;
    }

    public static void deleteWorld(@NotNull String name) {
        getMvCore().getMVWorldManager().deleteWorld(name);
    }

    public static void setWorldBorder(@NotNull World world, double size) {
        WorldBorder border = world.getWorldBorder();
        border.setSize(size);
        Location loc = world.getSpawnLocation();
        border.setCenter(loc.getX(), loc.getZ());
    }

    public static MultiverseWorld getWorld(@NotNull String name) {
        return getMvCore().getMVWorldManager().getMVWorld(name);
    }
}
