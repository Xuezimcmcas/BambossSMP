package net.boster.bamboss.smp.world;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class SMPUsers {

    @NotNull private final SMPWorld world;

    @NotNull private Map<UUID, SMPUserData> users = new HashMap<>();

    public @Nullable SMPUserData get(@NotNull UUID uuid) {
        return users.get(uuid);
    }

    public @NotNull SMPUserData getOrCreate(@NotNull UUID uuid) {
        return users.computeIfAbsent(uuid, u -> new SMPUserData(uuid));
    }

    public void save(@NotNull ConfigurationSection section) {
        for(SMPUserData d : users.values()) {
            d.save(section.createSection(d.getUuid().toString()));
        }
    }

    public void load(@NotNull ConfigurationSection section) {
        for(String s : section.getKeys(false)) {
            try {
                ConfigurationSection c = section.getConfigurationSection(s);
                if(c == null) continue;

                SMPUserData data = new SMPUserData(UUID.fromString(s));
                data.load(c);

                users.put(data.getUuid(), data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
