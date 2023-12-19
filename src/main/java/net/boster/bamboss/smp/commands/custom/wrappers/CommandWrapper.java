package net.boster.bamboss.smp.commands.custom.wrappers;

import lombok.Getter;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.commands.BosterCommand;
import net.boster.bamboss.smp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class CommandWrapper extends BosterCommand {

    @NotNull protected final ConfigurationSection config;
    @Getter @Nullable protected String permissionRequirement;

    public CommandWrapper(@NotNull BambossSMP plugin, @NotNull String name, @NotNull ConfigurationSection section) {
        super(plugin, section.getString("name", "null"), section.getStringList("aliases"));

        this.config = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("Messages." + name));
        this.permissionRequirement = "modcommands.command." + name.toLowerCase();
    }

    public void setPermissionRequirement(@Nullable String permission) {
        permissionRequirement = permission;
    }

    protected boolean hasPermission(@NotNull CommandSender sender) {
        return permissionRequirement == null || ((sender instanceof Player && plugin.getUserManager().isPermitted((Player) sender)) ||
                sender.hasPermission("modcommands.command.*") || sender.hasPermission(permissionRequirement));
    }

    public boolean checkPermission(@NotNull CommandSender sender) {
        if(!hasPermission(sender)) {
            sendNoPermission(sender);
            return false;
        } else {
            return true;
        }
    }

    public void sendNoPermission(@NotNull CommandSender sender) {
        sender.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noPermission")));
    }

    public void sendUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Utils.toColor(config.getString("usage")));
    }
}
