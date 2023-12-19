package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class WalkSpeedCommand extends SpeedChangeCommand {

    public WalkSpeedCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "walkSpeed", section, Attribute.GENERIC_MOVEMENT_SPEED);
    }
}
