package net.boster.bamboss.smp.gui;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import net.boster.gui.button.SimpleButtonItem;
import net.boster.gui.constructor.PaginatedGUI;
import net.boster.gui.multipage.MultiPageEntry;
import net.boster.gui.multipage.MultiPageGUI;
import net.boster.gui.utils.PlaceholdersProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SMPListGUI extends PaginatedGUI implements SMPGUI {
    private final SimpleButtonItem format;
    private final String noLoreString;

    private final String notLiked;
    private final String liked;

    public SMPListGUI(@NotNull ConfigurationSection section) {
        super(section);

        format = new SimpleButtonItem(Objects.requireNonNull(section.getConfigurationSection("Format")));
        noLoreString = Utils.toColor(section.getString("Format.NoLoreText", ""));

        notLiked = Utils.toColor(section.getString("Format.NotLiked", ""));
        liked = Utils.toColor(section.getString("Format.Liked", ""));
    }

    @Override
    public void open(@NotNull Player p) {
        MultiPageGUI g = getGUI(p);

        apply(g);

        for(SMPWorld world : BambossSMP.getInstance().getWorldManager().all().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLiked().size(), o1.getLiked().size())).collect(Collectors.toList())) {

            if(!world.getSettings().isAccessible()) continue;

            PlaceholdersProvider provider = (player, s) -> s.replace("%player%", world.getOwner().getName())
                    .replace("%prefix%", Utils.toColor(world.getSettings().getPrefixOrName()))
                    .replace("%online%", world.getOnlinePlayers().size() + "")
                    .replace("%maximum%", world.getSettings().getMaxPlayers() + "")
                    .replace("%likes%", world.getLiked().size() + "")
                    .replace("%like_status%", world.getLiked().contains(p.getUniqueId()) ? liked : notLiked);

            g.getItems().add(new MultiPageEntry() {
                public void onClick(@NotNull MultiPageGUI gui, @NotNull InventoryClickEvent event) {
                    Player p = (Player) event.getWhoClicked();
                    if(event.isLeftClick()) {
                        p.teleport(world.getWorld().getSpawnLocation());
                    } else {
                        if(world.getLiked().contains(p.getUniqueId())) {
                            p.sendMessage(Utils.toColor(BambossSMP.getInstance().getConfig().getString("Messages.alreadyLiked")));
                            return;
                        }

                        world.getLiked().add(p.getUniqueId());
                        p.sendMessage(Utils.toColor(BambossSMP.getInstance().getConfig().getString("Messages.liked")
                                .replace("%server%", world.getSettings().getPrefixOrName())));

                        Bukkit.getScheduler().runTask(BambossSMP.getInstance(), () -> {
                            gui.getInventory().setItem(event.getSlot(), item(p));
                        });
                    }
                }

                @Override
                public @Nullable ItemStack item(@NotNull Player player) {
                    ItemStack itemStack = format.prepareItem(player, provider);
                    if(itemStack == null) return null;

                    if(world.getSettings().getIcon() != null) {
                        itemStack.setType(world.getSettings().getIcon());
                    }

                    List<String> lore = new ArrayList<>();

                    if(format.getLore() != null) {
                        List<String> text = world.getSettings().getLore();

                        for(String s : format.getLore().apply(p)) {
                            if(s.contains("%lore%")) {
                                if(text.size() > 0) {
                                    lore.add(provider.applyPlaceholders(p, s.replace("%lore%", text.get(0))));
                                    for(int li = 1; li < text.size(); li++) {
                                        lore.add(provider.applyPlaceholders(p, Utils.toColor(text.get(li))));
                                    }
                                } else {
                                    lore.add(provider.applyPlaceholders(p, s.replace("%lore%", noLoreString)));
                                }
                            } else {
                                lore.add(provider.applyPlaceholders(p, s));
                            }
                        }

                        ItemMeta meta = itemStack.getItemMeta();
                        meta.setLore(lore);
                        itemStack.setItemMeta(meta);
                    }

                    return itemStack;
                }
            });
        }

        g.open();
    }
}
