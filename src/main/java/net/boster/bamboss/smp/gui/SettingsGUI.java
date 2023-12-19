package net.boster.bamboss.smp.gui;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.PermissionRequirement;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.utils.chat.ChatInputHandler;
import net.boster.bamboss.smp.utils.chat.IChatInputHandler;
import net.boster.bamboss.smp.world.SMPWorld;
import net.boster.bamboss.smp.world.SMPWorldSettings;
import net.boster.gui.CustomGUI;
import net.boster.gui.GUI;
import net.boster.gui.button.GUIButton;
import net.boster.gui.button.SimpleButtonItem;
import net.boster.gui.craft.CraftCustomGUI;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SettingsGUI implements SMPGUI {

    private final CraftCustomGUI gui;
    private final String emptyTextField;

    public SettingsGUI(@NotNull ConfigurationSection section) {
        gui = new CraftCustomGUI(section.getInt("Size", 27), Utils.toColor(section.getString("Title")));
        emptyTextField = Utils.toColor(section.getString("EmptyTextField"));

        ConfigurationSection gameRules = section.getConfigurationSection("GameRuleItems");
        if(gameRules != null) {
            for(String s : gameRules.getKeys(false)) {
                ConfigurationSection c = gameRules.getConfigurationSection(s);
                if(c == null) continue;

                PermissionRequirement requirement = new PermissionRequirement(c);

                GameRule<Boolean> rule;
                try {
                    rule = Objects.requireNonNull((GameRule<Boolean>) GameRule.getByName(c.getString("rule")));
                } catch (Throwable e) {
                    e.printStackTrace();
                    continue;
                }

                ItemStack enabled = Utils.getItemStack(c.getConfigurationSection("enabled"));
                ItemStack disabled = Utils.getItemStack(c.getConfigurationSection("disabled"));
                if(enabled == null || disabled == null) continue;

                String enabled_name = Utils.toColor(c.getString("enabled.name"));
                String disabled_name = Utils.toColor(c.getString("disabled.name"));

                List<String> enabled_lore = c.getStringList("enabled.lore").stream().map(Utils::toColor).collect(Collectors.toList());
                List<String> disabled_lore = c.getStringList("disabled.lore").stream().map(Utils::toColor).collect(Collectors.toList());

                int slot = c.getInt("slot");

                gui.addButton(new GUIButton() {
                    @Override
                    public int getSlot() {
                        return slot;
                    }

                    public void onClick(@NotNull Player p) {
                        if(!requirement.check(p)) return;

                        SMPWorld world = BambossSMP.getInstance().getWorldManager().get(p);
                        if(world == null) return;

                        world.setGameRule(rule, !Boolean.TRUE.equals(world.getWorld().getGameRuleValue(rule)));
                        open(p);
                    }

                    @Override
                    public ItemStack prepareItem(@NotNull Player player) {
                        SMPWorld world = BambossSMP.getInstance().getWorldManager().get(player);
                        if(world == null) return null;

                        boolean b = Boolean.TRUE.equals(world.getWorld().getGameRuleValue(rule));
                        ItemStack i = b ? enabled : disabled;

                        ItemStack item = i.clone();
                        ItemMeta meta = item.getItemMeta();
                        if(b) {
                            if(enabled_name != null) {
                                meta.setDisplayName(enabled_name);
                            }
                        } else {
                            if(disabled_name != null) {
                                meta.setDisplayName(disabled_name);
                            }
                        }
                        meta.setLore(b ? enabled_lore : disabled_lore);
                        item.setItemMeta(meta);

                        return item;
                    }
                });
            }
        }

        loadTexts(section);
        loadBooleans(section);
        loadNumbers(section);
        loadIconSelectionButton(section);
        loadGameModeSelectionButton(section);
        loadListsString(section);

        ConfigurationSection items = section.getConfigurationSection("Items");
        if(items != null) {
            for(String s : items.getKeys(false)) {
                ConfigurationSection c = items.getConfigurationSection(s);
                ItemStack i = Utils.getItemStack(c);
                if(i == null) continue;

                List<Integer> slots = c.getIntegerList("slots");
                int st = c.getInt("slot", -1);
                if(st >= 0) {
                    slots.add(st);
                }

                for(Integer slot : slots) {
                    gui.addButton(new GUIButton() {
                        @Override
                        public int getSlot() {
                            return slot;
                        }

                        @Override
                        public ItemStack prepareItem(@NotNull Player player) {
                            return i;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void open(@NotNull Player player) {
        new CustomGUI(player, gui).open();
    }

    private void loadIconSelectionButton(@NotNull ConfigurationSection section) {
        ConfigurationSection button = section.getConfigurationSection("IconSelectionButton");
        if(button == null) return;

        int slot = button.getInt("slot");
        SimpleButtonItem format = new SimpleButtonItem(button);

        gui.addButton(new GUIButton() {
            @Override
            public int getSlot() {
                return slot;
            }

            public void onClick(@NotNull Player p) {
                BambossSMP.getInstance().getGuiManager().getIconsListGUI().open(p);
            }

            @Override
            public @Nullable ItemStack prepareItem(@NotNull Player player) {
                SMPWorld world = BambossSMP.getInstance().getWorldManager().get(player);
                if(world == null) return null;

                ItemStack itemStack = format.prepareItem(player);

                if(itemStack != null && world.getSettings().getIcon() != null) {
                    itemStack.setType(world.getSettings().getIcon());
                }

                return itemStack;
            }
        });
    }

    private void loadGameModeSelectionButton(@NotNull ConfigurationSection section) {
        ConfigurationSection c = section.getConfigurationSection("GameModeSelector");
        if(c == null) return;

        int slot = c.getInt("slot");
        Map<GameMode, SimpleButtonItem> modes = new HashMap<>();

        for(GameMode mode : GameMode.values()) {
            try {
                modes.put(mode, new SimpleButtonItem(Objects.requireNonNull(c.getConfigurationSection(mode.name()))));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        gui.addButton(new GUIButton() {
            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public void onClick(@NotNull GUI gui, @NotNull InventoryClickEvent event) {
                Player p = (Player) event.getWhoClicked();
                SMPWorld w = BambossSMP.getInstance().getWorldManager().get(p);
                GameMode mode = w.getSettings().getGameMode(GameMode.SURVIVAL);

                if(mode == GameMode.SURVIVAL) {
                    w.getSettings().setGameMode(GameMode.CREATIVE);
                } else if(mode == GameMode.CREATIVE) {
                    w.getSettings().setGameMode(GameMode.ADVENTURE);
                } else if(mode == GameMode.ADVENTURE) {
                    w.getSettings().setGameMode(GameMode.SPECTATOR);
                } else {
                    w.getSettings().setGameMode(GameMode.SURVIVAL);
                }

                event.getClickedInventory().setItem(slot, modes.get(w.getSettings().getGameMode()).prepareItem(p));
            }

            @Override
            public @Nullable ItemStack prepareItem(@NotNull Player player) {
                return modes.get(BambossSMP.getInstance().getWorldManager().get(player)
                        .getSettings().getGameMode(GameMode.SURVIVAL)).prepareItem(player);
            }
        });
    }

    private void loadTexts(@NotNull ConfigurationSection section) {
        ConfigurationSection texts = section.getConfigurationSection("Texts");
        if(texts == null) return;

        for(String s : texts.getKeys(false)) {
            ConfigurationSection c = texts.getConfigurationSection(s);
            if(c == null) return;

            PermissionRequirement requirement = new PermissionRequirement(c);

            Field field;
            try {
                field = SMPWorldSettings.class.getDeclaredField(Objects.requireNonNull(c.getString("field")));
                field.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                continue;
            }

            int slot = c.getInt("slot");
            ItemStack item = Utils.getItemStack(c);
            String name = Utils.toColor(c.getString("name"));
            List<String> lore = c.getStringList("lore").stream().map(Utils::toColor).collect(Collectors.toList());

            String hint = Utils.toColor(c.getString("hint"));

            gui.addButton(new GUIButton() {
                @Override
                public int getSlot() {
                    return slot;
                }

                public void onClick(@NotNull Player p) {
                    if(!requirement.check(p)) return;

                    p.closeInventory();
                    if(hint != null) {
                        p.sendMessage(hint);
                    }
                    ChatInputHandler.addPlayer(p, message -> {
                        SMPWorld w = BambossSMP.getInstance().getWorldManager().get(p);
                        if(w == null) return false;

                        try {
                            field.set(w.getSettings(), message);
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                        }
                        open(p);
                        return false;
                    });
                }

                @Override
                public @Nullable ItemStack prepareItem(@NotNull Player player) {
                    String text;
                    try {
                        text = (String) field.get(BambossSMP.getInstance().getWorldManager().get(player).getSettings());
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                        return null;
                    }

                    if(text == null) {
                        text = emptyTextField;
                    }
                    String finalText = Utils.toColor(text);

                    ItemStack i = item.clone();
                    ItemMeta meta = i.getItemMeta();

                    if(name != null) {
                        meta.setDisplayName(name.replace("%text%", finalText));
                    }
                    meta.setLore(lore.stream().map(l -> l.replace("%text%", finalText)).collect(Collectors.toList()));

                    i.setItemMeta(meta);
                    return i;
                }
            });
        }
    }

    private void loadBooleans(@NotNull ConfigurationSection section) {
        ConfigurationSection booleans = section.getConfigurationSection("Booleans");
        if(booleans != null) {
            for(String s : booleans.getKeys(false)) {
                ConfigurationSection c = booleans.getConfigurationSection(s);
                if(c == null) continue;

                PermissionRequirement requirement = new PermissionRequirement(c);

                Field field;
                try {
                    field = SMPWorldSettings.class.getDeclaredField(Objects.requireNonNull(c.getString("field")));
                    field.setAccessible(true);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                    continue;
                }

                ItemStack enabled = Utils.getItemStack(c.getConfigurationSection("enabled"));
                ItemStack disabled = Utils.getItemStack(c.getConfigurationSection("disabled"));
                if(enabled == null || disabled == null) continue;

                String enabled_name = Utils.toColor(c.getString("enabled.name"));
                String disabled_name = Utils.toColor(c.getString("disabled.name"));

                List<String> enabled_lore = c.getStringList("enabled.lore").stream().map(Utils::toColor).collect(Collectors.toList());
                List<String> disabled_lore = c.getStringList("disabled.lore").stream().map(Utils::toColor).collect(Collectors.toList());

                int slot = c.getInt("slot");

                gui.addButton(new GUIButton() {
                    @Override
                    public int getSlot() {
                        return slot;
                    }

                    public void onClick(@NotNull Player p) {
                        if(!requirement.check(p)) return;

                        SMPWorld world = BambossSMP.getInstance().getWorldManager().get(p);
                        if(world == null) return;

                        try {
                            field.set(world.getSettings(), !(boolean) field.get(world.getSettings()));
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                        }
                        open(p);
                    }

                    @Override
                    public ItemStack prepareItem(Player player) {
                        SMPWorld world = BambossSMP.getInstance().getWorldManager().get(player);
                        if(world == null) return null;

                        boolean b;
                        try {
                            b = (boolean) field.get(world.getSettings());
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                            return null;
                        }

                        ItemStack i = b ? enabled : disabled;

                        ItemStack item = i.clone();
                        ItemMeta meta = item.getItemMeta();
                        if(b) {
                            if(enabled_name != null) {
                                meta.setDisplayName(enabled_name);
                            }
                        } else {
                            if(disabled_name != null) {
                                meta.setDisplayName(disabled_name);
                            }
                        }
                        meta.setLore(b ? enabled_lore : disabled_lore);
                        item.setItemMeta(meta);

                        return item;
                    }
                });
            }
        }
    }

    private void loadNumbers(@NotNull ConfigurationSection section) {
        ConfigurationSection numbers = section.getConfigurationSection("Numbers");
        if(numbers == null) return;

        for(String s : numbers.getKeys(false)) {
            ConfigurationSection c = numbers.getConfigurationSection(s);
            if(c == null) continue;

            loadNumber(c);
        }
    }

    private void loadNumber(@NotNull ConfigurationSection c) {
        PermissionRequirement requirement = new PermissionRequirement(c);

        Field field;
        try {
            field = SMPWorldSettings.class.getDeclaredField(Objects.requireNonNull(c.getString("field")));
            field.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return;
        }

        int type = c.getInt("type");
        int slot = c.getInt("slot");
        ItemStack item = Utils.getItemStack(c);
        String name = Utils.toColor(c.getString("name"));
        List<String> lore = c.getStringList("lore").stream().map(Utils::toColor).collect(Collectors.toList());

        String hint = Utils.toColor(c.getString("hint"));

        gui.addButton(new GUIButton() {
            @Override
            public int getSlot() {
                return slot;
            }

            public void onClick(@NotNull Player p) {
                if(!requirement.check(p)) return;

                p.closeInventory();
                if(hint != null) {
                    p.sendMessage(hint);
                }
                ChatInputHandler.addPlayer(p, message -> {
                    SMPWorld w = BambossSMP.getInstance().getWorldManager().get(p);
                    if(w == null) return false;

                    Object o;
                    try {
                        if(type == 0) {
                            o = Integer.parseInt(message);
                        } else if(type == 1) {
                            o = Double.parseDouble(message);
                        } else if(type == 2) {
                            o = Long.parseLong(message);
                        } else {
                            o = Float.parseFloat(message);
                        }
                    } catch (Exception e) {
                        o = null;
                    }

                    if(o == null) {
                        p.sendMessage(Utils.toColor(BambossSMP.getInstance().getConfig().getString("Messages.notValidNumber")
                                .replace("%arg%", message)));
                        return false;
                    }

                    try {
                        field.set(w.getSettings(), o);
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                    open(p);
                    return false;
                });
            }

            @Override
            public @Nullable ItemStack prepareItem(@NotNull Player player) {
                String text;
                try {
                    if(type == 0) {
                        text = (int) field.get(BambossSMP.getInstance().getWorldManager().get(player).getSettings()) + "";
                    } else if(type == 1) {
                        text = (double) field.get(BambossSMP.getInstance().getWorldManager().get(player).getSettings()) + "";
                    } else if(type == 2) {
                        text = (long) field.get(BambossSMP.getInstance().getWorldManager().get(player).getSettings()) + "";
                    } else {
                        text = (float) field.get(BambossSMP.getInstance().getWorldManager().get(player).getSettings()) + "";
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                    return null;
                }

                ItemStack i = item.clone();
                ItemMeta meta = i.getItemMeta();

                if(name != null) {
                    meta.setDisplayName(name.replace("%value%", text));
                }
                meta.setLore(lore.stream().map(l -> l.replace("%value%", text)).collect(Collectors.toList()));

                i.setItemMeta(meta);
                return i;
            }
        });
    }

    private void loadListsString(@NotNull ConfigurationSection section) {
        ConfigurationSection texts = section.getConfigurationSection("ListsString");
        if(texts == null) return;

        for(String s : texts.getKeys(false)) {
            ConfigurationSection c = texts.getConfigurationSection(s);
            if(c == null) return;

            PermissionRequirement requirement = new PermissionRequirement(c);

            Field field;
            try {
                field = SMPWorldSettings.class.getDeclaredField(Objects.requireNonNull(c.getString("field")));
                field.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                continue;
            }

            int slot = c.getInt("slot");
            ItemStack item = Utils.getItemStack(c);
            String name = Utils.toColor(c.getString("name"));
            List<String> lore = c.getStringList("lore").stream().map(Utils::toColor).collect(Collectors.toList());

            String hint = Utils.toColor(c.getString("hint"));

            int limit = c.getInt("limit", -1);
            String limitMessage = Utils.toColor(c.getString("limitMessage"));

            gui.addButton(new GUIButton() {
                @Override
                public int getSlot() {
                    return slot;
                }

                public void onClick(@NotNull Player p) {
                    if(!requirement.check(p)) return;

                    p.closeInventory();
                    if(hint != null) {
                        p.sendMessage(hint);
                    }
                    ChatInputHandler.addPlayer(p, new IChatInputHandler() {
                        private final List<String> input = new ArrayList<>();

                        @Override
                        public boolean handle(@NotNull String message) {
                            SMPWorld w = BambossSMP.getInstance().getWorldManager().get(p);
                            if(w == null) return false;

                            if(message.equalsIgnoreCase("/cancel")) {
                                open(p);
                                return false;
                            } else if(message.equalsIgnoreCase("/done")) {
                                try {
                                    field.set(w.getSettings(), input);
                                } catch (ReflectiveOperationException e) {
                                    e.printStackTrace();
                                }
                                open(p);
                                return false;
                            } else {
                                if(limit > -1 && input.size() >= limit) {
                                    if(limitMessage != null) {
                                        p.sendMessage(limitMessage);
                                    }
                                    return true;
                                }

                                input.add(message);
                                return true;
                            }
                        }
                    });
                }

                @Override
                public @Nullable ItemStack prepareItem(@NotNull Player player) {
                    List<String> text;
                    try {
                        text = (List<String>) field.get(BambossSMP.getInstance().getWorldManager().get(player).getSettings());
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                        return null;
                    }

                    if(text == null) {
                        text = new ArrayList<>();
                    } else {
                        text = text.stream().map(Utils::toColor).collect(Collectors.toList());
                    }

                    ItemStack i = item.clone();
                    ItemMeta meta = i.getItemMeta();

                    if(name != null) {
                        meta.setDisplayName(name);
                    }
                    List<String> list = new ArrayList<>();
                    for(String l : lore) {
                        if(l.contains("%list%")) {
                            if(text.size() > 0) {
                                list.add(l.replace("%list%", text.get(0)));
                                for(int li = 1; li < text.size(); li++) {
                                    list.add(text.get(li));
                                }
                            } else {
                                list.add(l.replace("%list%", emptyTextField));
                            }
                        } else {
                            list.add(l);
                        }
                    }
                    meta.setLore(list);

                    i.setItemMeta(meta);
                    return i;
                }
            });
        }
    }
}
