package net.boster.bamboss.smp.gui;

import net.boster.bamboss.smp.BambossSMP;
import net.boster.gui.CustomGUI;
import net.boster.gui.button.ClickableButton;
import net.boster.gui.constructor.SimpleConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServerSetupGUI extends SimpleConstructor implements SMPGUI {

    public ServerSetupGUI(@NotNull ConfigurationSection section) {
        super(section);

        ClickableButton gameRulesItem = new ClickableButton(Objects.requireNonNull(section.getConfigurationSection("GameRules")));
        ClickableButton settingsItem = new ClickableButton(Objects.requireNonNull(section.getConfigurationSection("Settings")));
        ClickableButton closeItem = new ClickableButton(Objects.requireNonNull(section.getConfigurationSection("Close")));

        gameRulesItem.setClickAction(p -> BambossSMP.getInstance().getGuiManager().getGameRulesGUI().open(p));
        settingsItem.setClickAction(p -> BambossSMP.getInstance().getGuiManager().getSettingsGUI().open(p));
        closeItem.setClickAction(HumanEntity::closeInventory);

        craftGUI.addButton(gameRulesItem);
        craftGUI.addButton(settingsItem);
        craftGUI.addButton(closeItem);
    }

    @Override
    public void open(@NotNull Player player) {
        CustomGUI g = new CustomGUI(player, craftGUI);

        g.open();
    }
}
