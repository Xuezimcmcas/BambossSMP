package net.boster.bamboss.smp.commands;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SMPCommand extends BosterCommand {

    public SMPCommand(@NotNull BambossSMP plugin) {
        super(plugin, plugin.getConfig().getString("Command.name", "smp"),
                plugin.getConfig().getStringList("Command.aliases"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!checkPlayer(sender)) return true;

        Player p = (Player) sender;

        if(args.length == 0) {
            sendHelp(p);
            return true;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if (!p.hasPermission("bamboss.smp.create")) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noPermission")));
                return true;
            }

            if (plugin.getWorldManager().get(p) != null) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.create.already")));
                return true;
            }

            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.create.creating")));
            p.sendTitle(Utils.toColor(plugin.getConfig().getString("Messages.create.title", "&cNull")), "", 5, Integer.MAX_VALUE, 10);
            SMPWorld world = plugin.getWorldManager().create(p);
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.create.created")));
            p.resetTitle();
            p.teleport(world.getWorld().getSpawnLocation());
        } else if(args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
            SMPWorld world = plugin.getWorldManager().get(p);
            if(world == null) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noSMP")));
                return true;
            }

            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.teleporting")));
            p.teleport(world.getWorld().getSpawnLocation());
        } else if(args[0].equalsIgnoreCase("settings")) {
            SMPWorld world = checkSMP(p);
            if(world == null) return true;

            if(!world.isPermitted(plugin.getUserManager().get(p))) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noPermission")));
                return true;
            }

            plugin.getGuiManager().getSetupServerGUI().open(p);
        } else if(args[0].equalsIgnoreCase("delete")) {
            SMPWorld world = checkSMP(p);
            if (world == null) return true;

            if(world.getOwner() != plugin.getUserManager().get(p.getUniqueId())) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noPermission")));
                return true;
            }

            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.delete.deleting")));
            world.delete();
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.delete.deleted")));
        } else if(args[0].equalsIgnoreCase("view")) {
            plugin.getGuiManager().getSmpListGUI().open(p);
        }  else if(args[0].equalsIgnoreCase("op")) {
            SMPWorld world = checkSMP(p);
            if(world == null) return true;

            if(args.length < 2) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.op.usage")));
                return true;
            }

            Player plr = Bukkit.getPlayer(args[1]);
            if(plr == null) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.nullPlayer").replace("%name%", args[1])));
                return true;
            }

            if(world.getOperators().contains(plr.getName())) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.op.already").replace("%name%", plr.getName())));
                return true;
            }

            world.getOperators().add(plr.getName());
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.op.success").replace("%player%", plr.getName())));
        }  else if(args[0].equalsIgnoreCase("deop")) {
            SMPWorld world = checkSMP(p);
            if (world == null) return true;

            if (args.length < 2) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.deop.usage")));
                return true;
            }

            if (!world.getOperators().contains(args[1])) {
                p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.deop.notAnOP").replace("%name%", args[1])));
                return true;
            }

            world.getOperators().remove(args[1]);
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.deop.success").replace("%player%", args[1])));
        } else if(args[0].equalsIgnoreCase("setspawn")) {
            SMPWorld world = checkSMP(p);
            if (world == null) return true;

            p.getWorld().setSpawnLocation(p.getLocation());
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.setSpawn")));
        } else {
            sendHelp(p);
        }
        return true;
    }

    private SMPWorld checkSMP(Player p) {
        SMPWorld world = plugin.getWorldManager().getWorld(p);
        if (world == null) {
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.notOnAnSMP")));
            return null;
        }

        if(!world.isPermitted(plugin.getUserManager().get(p))) {
            p.sendMessage(Utils.toColor(plugin.getConfig().getString("Messages.noPermission")));
            return null;
        }

        return world;
    }

    private void sendHelp(CommandSender sender) {
        for(String s : plugin.getConfig().getStringList("Messages.help")) {
            sender.sendMessage(Utils.toColor(s));
        }
    }
}
