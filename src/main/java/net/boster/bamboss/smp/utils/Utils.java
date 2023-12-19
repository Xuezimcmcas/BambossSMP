package net.boster.bamboss.smp.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.punishments.SMPBan;
import net.boster.bamboss.smp.world.SMPWorld;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    @Contract("!null -> !null")
    public static String toColor(@Nullable String s) {
        if(s == null) return null;

        String str = s;
        for(Matcher matcher = pattern.matcher(str); matcher.find(); matcher = pattern.matcher(str)) {
            String color = str.substring(matcher.start() + 1, matcher.end());
            str = str.replace("&" + color, ChatColor.of(color) + "");
        }

        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String toReplaces(String s, @NotNull String... replaces) {
        if(s == null) return null;

        String r = s;
        for(int i = 0; i < replaces.length; i++) {
            if(i + 1 < replaces.length) {
                r = r.replace(replaces[i], replaces[i + 1]);
            }
            i++;
        }

        return r;
    }

    public static ItemStack getCustomSkull(String value) {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", value));

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack getItemStack(ConfigurationSection section, @NotNull String... replaces) {
        if(section == null) return null;

        ItemStack item;
        if(section.getString("head") != null) {
            item = getCustomSkull(toReplaces(section.getString("head"), replaces));
        } else if(section.getString("skull") != null) {
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(toReplaces(section.getString("skull"), replaces)));
            item.setItemMeta(meta);
        } else {
            try {
                item = new ItemStack(Material.valueOf(toReplaces(section.getString("material"), replaces)));
            } catch (Exception e) {
                return null;
            }
        }

        item.setAmount(section.getInt("amount", 1));

        ConfigurationSection potion = section.getConfigurationSection("potionEffect");
        if(potion != null && item.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            try {
                meta.addCustomEffect(new PotionEffect(
                        Objects.requireNonNull(PotionEffectType.getByName(potion.getString("type"))),
                        potion.getInt("duration", 0) * 20,
                        potion.getInt("amplifier", 0)), true);
                item.setItemMeta(meta);
            } catch (Exception ignored) {}
        }

        ItemMeta meta = item.getItemMeta();
        if(section.getString("name") != null) {
            meta.setDisplayName(toColor(toReplaces(section.getString("name"), replaces)));
        }
        List<String> lore = new ArrayList<>();
        section.getStringList("lore").forEach(s -> lore.add(toColor(toReplaces(s, replaces))));
        meta.setLore(lore);
        if(section.get("CustomModelData") != null) {
            meta.setCustomModelData(section.getInt("CustomModelData"));
        }

        int damage = section.getInt("damage", -1);
        if(damage > -1) {
            item.setDurability((short) damage);
        }

        meta.setUnbreakable(section.getBoolean("unbreakable", false));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);

        item.setItemMeta(meta);
        return item;
    }

    public static String encode(Object s) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(s);
            outputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T decode(@NotNull String s) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(s));
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        Object r = inputStream.readObject();
        inputStream.close();
        return (T) r;
    }

    public static <T> T decodeSimple(@NotNull String s) {
        try {
            return decode(s);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public static @NotNull String parseList(@NotNull List<String> list) {
        String s = "";
        String n = "";

        for(String l : list) {
            s += n + l;
            n = "\n";
        }

        return s;
    }

    public static void sendBanMessage(@NotNull Player player, @NotNull SMPWorld smp, @NotNull SMPBan ban) {
        for(String s : BambossSMP.getInstance().getConfig().getStringList("Messages.ban.playerBanned")) {
            player.sendMessage(toColor(s.replace("%server%", smp.getSettings().getPrefixOrName())
                    .replace("%player%", ban.getAdminName())
                    .replace("%reason%", ban.getReason())));
        }
    }

    public static void sendTextComponent(@NotNull Player p, @NotNull String msg) {
        if(!msg.contains(";")) {
            p.sendMessage(msg);
            return;
        }

        try {
            String[] s = msg.split(";");
            TextComponent tc = new TextComponent(s[1]);
            if(s.length >= 4) {
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(s[0]), s[3]));
            }
            if(s.length >= 3) {
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(s[2])));
            }
            p.spigot().sendMessage(tc);
        } catch (Exception e) {
            p.sendMessage(msg);
        }
    }

    public static @Nullable Location getLocation(@Nullable String s) {
        if(s == null) return null;

        try {
            String[] ss = s.split(", ");
            World w = Bukkit.getWorld(ss[0]);
            double x = Double.parseDouble(ss[1]);
            double y = Double.parseDouble(ss[2]);
            double z = Double.parseDouble(ss[3]);
            if(ss.length >= 6) {
                float pitch = Float.parseFloat(ss[4]);
                float yaw = Float.parseFloat(ss[5]);
                return new Location(w, x, y, z, pitch, yaw);
            } else {
                return new Location(w, x, y, z);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static @NotNull String locationToString(@NotNull Location loc, boolean withPitchAndYaw) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() +
                (withPitchAndYaw ? ", " + loc.getYaw() + ", " + loc.getPitch() : "");
    }
}
