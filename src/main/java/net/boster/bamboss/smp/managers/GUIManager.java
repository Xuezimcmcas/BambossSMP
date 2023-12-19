package net.boster.bamboss.smp.managers;

import lombok.Data;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.files.CustomFile;
import net.boster.bamboss.smp.gui.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Data
public class GUIManager {

    @NotNull private final BambossSMP plugin;

    private final CustomFile settingsGUIFile = new CustomFile("settings-gui");

    @NotNull private SMPGUI setupServerGUI = new EmptySMPGUI();
    @NotNull private SMPGUI gameRulesGUI = new EmptySMPGUI();
    @NotNull private SMPGUI settingsGUI = new EmptySMPGUI();
    @NotNull private SMPGUI smpListGUI = new EmptySMPGUI();
    @NotNull private SMPGUI iconsListGUI = new EmptySMPGUI();

    public void init() {
        settingsGUIFile.create();

        setupServerGUI = new ServerSetupGUI(Objects.requireNonNull(settingsGUIFile.getConfiguration().getConfigurationSection("ServerSetupGUI")));
        gameRulesGUI = new SettingsGUI(Objects.requireNonNull(settingsGUIFile.getConfiguration().getConfigurationSection("GameRulesGUI")));
        settingsGUI = new SettingsGUI(Objects.requireNonNull(settingsGUIFile.getConfiguration().getConfigurationSection("SettingsGUI")));
        smpListGUI = new SMPListGUI(Objects.requireNonNull(plugin.getConfig().getConfigurationSection("SMPListGUI")));
        iconsListGUI = new MaterialsListGUI(Objects.requireNonNull(plugin.getConfig().getConfigurationSection("IconsListGUI")));
    }
}
