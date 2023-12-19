package net.boster.bamboss.smp.managers;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.files.SMPFile;
import net.boster.bamboss.smp.user.SMPUser;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class SMPUserManager {

    private final Map<UUID, SMPUser> map = new HashMap<>();

    public @NotNull SMPUser update(@NotNull Player player) {
        SMPUser user = map.get(player.getUniqueId());
        if(user == null) {
            user = create(player);
        } else {
            user.setName(player.getName());
        }
        return user;
    }

    public @NotNull SMPUser create(@NotNull Player player) {
        SMPUser user = new SMPUser(player);
        map.put(player.getUniqueId(), user);
        return user;
    }

    public @NotNull SMPUser get(@NotNull Player player) {
        SMPUser user = map.get(player.getUniqueId());
        if(user == null) {
            user = create(player);
        }
        return user;
    }

    public SMPUser get(@NotNull UUID id) {
        return map.get(id);
    }

    public SMPUser get(@NotNull String name) {
        for(SMPUser user : all()) {
            if(user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    public void load() {
        File f = new File(BambossSMP.getInstance().getDataFolder(), "users");
        if(!f.exists()) return;

        for(File uf : f.listFiles()) {
            if(!uf.getName().endsWith(".yml")) continue;

            SMPFile file = new SMPFile(uf);
            file.loadConfig();

            SMPUser user = loadUser(file);

            map.put(user.getUuid(), user);
        }
    }

    public @NotNull SMPUser loadUser(@NotNull SMPFile file) {
        FileConfiguration c = file.getConfiguration();

        String premiumIcons = c.getString("premiumIcons");

        SMPUser user = new SMPUser(Objects.requireNonNull(c.getString("name")),
                UUID.fromString(Objects.requireNonNull(c.getString("uuid"))),
                premiumIcons != null ? Utils.decodeSimple(premiumIcons) : new ArrayList<>(),
                file);

        return user;
    }

    public boolean isPermitted(@NotNull Player player) {
        SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(player);
        if(w == null) return false;

        return w.isPermitted(BambossSMP.getInstance().getUserManager().get(player));
    }

    public void saveUser(@NotNull SMPUser user) {
        SMPFile file = user.getFile();
        FileConfiguration c = file.getConfiguration();

        c.set("name", user.getName());
        c.set("uuid", user.getUuid().toString());
        c.set("premiumIcons", Utils.encode(user.getPremiumIcons()));

        file.save();
    }

    public Collection<SMPUser> all() {
        return new HashMap<>(map).values();
    }
}
