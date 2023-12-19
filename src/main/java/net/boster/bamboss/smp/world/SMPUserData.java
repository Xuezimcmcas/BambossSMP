package net.boster.bamboss.smp.world;

import lombok.Data;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class SMPUserData {

    @NotNull private final UUID uuid;

    @Nullable private Location quitLocation;
    @Nullable private GameMode gameMode;

    @NotNull private List<PotionEffect> effects = new ArrayList<>();

    public void save(@NotNull ConfigurationSection section) {
        section.set("quitLocation", quitLocation != null ? Utils.locationToString(quitLocation, true) : null);
        section.set("gameMode", gameMode != null ? gameMode.name() : null);

        for(PotionEffect e : effects) {
            ConfigurationSection c = section.createSection("effects." + e.getType());
            c.set("amplifier", e.getAmplifier());
            c.set("duration", e.getDuration());
        }
    }

    public void load(@NotNull ConfigurationSection section) {
        quitLocation = Utils.getLocation(section.getString("quitLocation"));

        String gm = section.getString("gameMode");
        if(gm != null) {
            gameMode = GameMode.valueOf(gm);
        }

        ConfigurationSection ec = section.getConfigurationSection("effects");
        if(ec != null) {
            for(String s : ec.getKeys(false)) {
                ConfigurationSection c = ec.getConfigurationSection(s);
                if(c == null) continue;

                PotionEffectType type = PotionEffectType.getByName(s);
                if(type == null) continue;

                effects.add(new PotionEffect(type, c.getInt("amplifier"), c.getInt("duration")));
            }
        }
    }

    public void saveEffects(@NotNull Player p) {
        effects.clear();
        effects.addAll(p.getActivePotionEffects());
    }

    public void applyEffects(@NotNull Player p) {
        effects.forEach(p::addPotionEffect);
    }
}
