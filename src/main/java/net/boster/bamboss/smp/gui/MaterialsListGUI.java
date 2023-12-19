package net.boster.bamboss.smp.gui;

import com.google.common.collect.ImmutableSet;
import net.boster.bamboss.coins.BambossCoinsAPI;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.gui.item.MaterialIconItem;
import net.boster.bamboss.smp.user.SMPUser;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import net.boster.gui.constructor.PaginatedGUI;
import net.boster.gui.multipage.MultiPageEntry;
import net.boster.gui.multipage.MultiPageGUI;
import net.boster.gui.utils.PlaceholdersProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MaterialsListGUI extends PaginatedGUI implements SMPGUI {

    private final Set<Material> blockedMaterials = ImmutableSet.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

    private final MaterialIconItem formatFree;
    private final MaterialIconItem formatPremium;
    private final MaterialIconItem formatPremiumAvailable;

    private final String enoughColor;
    private final String notEnoughColor;

    private final Map<Material, Integer> premiumMaterials = new HashMap<>();

    public MaterialsListGUI(@NotNull ConfigurationSection section) {
        super(section);

        formatFree = new MaterialIconItem(Objects.requireNonNull(section.getConfigurationSection("FormatFree")));
        formatPremium = new MaterialIconItem(Objects.requireNonNull(section.getConfigurationSection("FormatPremium")));
        formatPremiumAvailable = new MaterialIconItem(Objects.requireNonNull(section.getConfigurationSection("FormatPremiumAvailable")));

        enoughColor = section.getString("FormatPremium.enoughColor", "&e");
        notEnoughColor = section.getString("FormatPremium.notEnoughColor", "ce");

        ConfigurationSection pm = section.getConfigurationSection("PremiumMaterials");
        if(pm != null) {
            for(String s : pm.getKeys(false)) {
                try {
                    premiumMaterials.put(Material.valueOf(s), pm.getInt(s));
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(Utils.toColor("&d[&bBambossSMP&d] &d[&cIconsListGUI&d]&c " +
                            "Could not define premium material: &f" + s));
                }
            }
        }
    }

    @Override
    public void open(@NotNull Player p) {
        MultiPageGUI g = getGUI(p);

        apply(g);

        SMPWorld world = Objects.requireNonNull(BambossSMP.getInstance().getWorldManager().get(p));
        SMPUser user = BambossSMP.getInstance().getUserManager().get(p);
        int coins = BambossCoinsAPI.getCoins(p);

        for(Material material : Material.values()) {
            if(blockedMaterials.contains(material)) continue;

            Integer price = premiumMaterials.get(material);
            boolean available;

            PlaceholdersProvider provider;
            if(price != null) {
                available = user.getPremiumIcons().contains(material);
                if(available) {
                    provider = (player, s) -> s.replace("%material%", material.name());
                } else {
                    provider = (player, s) -> s.replace("%material%", material.name())
                            .replace("%color%", coins >= price ? enoughColor : notEnoughColor)
                            .replace("%price%", price + "");
                }
            } else {
                available = true;
                provider = (player, s) -> s.replace("%material%", material.name());
            }

            g.getItems().add(new MultiPageEntry() {
                public void onClick(@NotNull MultiPageGUI gui, @NotNull Player p) {
                    if(price != null) {
                        if(available) {
                            world.getSettings().setIcon(material);
                            formatPremiumAvailable.selected(p, provider);
                        } else {
                            if(coins < price) {
                                formatPremium.deny(p, provider);
                                return;
                            }

                            BambossCoinsAPI.withdrawCoins(p, price);
                            world.getSettings().setIcon(material);
                            formatPremium.selected(p, provider);
                        }
                    } else {
                        world.getSettings().setIcon(material);
                        formatFree.selected(p, provider);
                    }
                }

                @Override
                public @Nullable ItemStack item(@NotNull Player player) {
                    MaterialIconItem format = price != null ? available ? formatPremiumAvailable : formatPremium : formatFree;
                    ItemStack itemStack = format.prepareItem(player, provider);

                    if(itemStack != null && world.getSettings().getIcon() != null) {
                        itemStack.setType(material);
                    }

                    return itemStack;
                }
            });
        }

        g.open();
    }
}
