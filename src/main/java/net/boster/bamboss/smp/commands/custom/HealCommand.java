package net.boster.bamboss.smp.commands.custom;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.custom.wrappers.SimpleModCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealCommand extends SimpleModCommand {

    public HealCommand(@NotNull BambossSMP plugin, @NotNull ConfigurationSection section) {
        super(plugin, "heal", section);
    }

    @Override
    public void execute(@NotNull Player player) {
        heal(player);
        player.sendMessage(Utils.toColor(config.getString("success")));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Player target) {
        heal(target);
        sender.sendMessage(Utils.toColor(config.getString("others").replace("%player%", target.getName())));
    }

    private void heal(Player p) {
        AttributeInstance i = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        p.setHealth(i != null ? i.getBaseValue() : 20);
    }
}
