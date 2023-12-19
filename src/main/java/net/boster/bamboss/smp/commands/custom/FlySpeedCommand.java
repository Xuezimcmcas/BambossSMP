package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class FlySpeedCommand extends SpeedChangeCommand {

    public FlySpeedCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "flySpeed", section, Attribute.GENERIC_FLYING_SPEED);
    }
}
