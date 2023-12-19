package net.boster.bamboss.smp;

import lombok.Getter;
import lombok.Setter;
import net.boster.bamboss.smp.commands.BosterCommand;
import net.boster.bamboss.smp.commands.SMPCommand;
import net.boster.bamboss.smp.commands.custom.*;
import net.boster.bamboss.smp.commands.custom.punishments.BanCommand;
import net.boster.bamboss.smp.commands.custom.punishments.KickCommand;
import net.boster.bamboss.smp.commands.custom.punishments.UnbanCommand;
import net.boster.bamboss.smp.commands.custom.world.*;
import net.boster.bamboss.smp.files.CustomFile;
import net.boster.bamboss.smp.files.SMPFile;
import net.boster.bamboss.smp.hook.PAPIHook;
import net.boster.bamboss.smp.listeners.*;
import net.boster.bamboss.smp.managers.GUIManager;
import net.boster.bamboss.smp.user.SMPUser;
import net.boster.bamboss.smp.managers.SMPUserManager;
import net.boster.bamboss.smp.utils.Constants;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.world.SMPWorld;
import net.boster.bamboss.smp.managers.SMPWorldManager;
import net.boster.chat.common.chat.Chat;
import net.boster.chat.common.config.YamlConfiguration;
import net.boster.gui.BosterGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
public class BambossSMP extends JavaPlugin {

    @Getter private static BambossSMP instance;

    @NotNull private final SMPUserManager userManager = new SMPUserManager();
    @NotNull private final SMPWorldManager worldManager = new SMPWorldManager();

    @NotNull private final GUIManager guiManager = new GUIManager(this);

    @NotNull private final CustomFile chatFile = new CustomFile("chat");

    private Chat chatInstance;
    private YamlConfiguration chatConfig;

    private final String PREFIX = "\u00a76+\u00a7a---------------- \u00a7dBambossSMP \u00a7a------------------\u00a76+";

    public void onLoad() {
        instance = this;
        Bukkit.getConsoleSender().sendMessage(PREFIX);
        Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBambossSMP\u00a7d] \u00a7fLoading!");
        saveDefaultConfig();
        load();
        Bukkit.getConsoleSender().sendMessage(PREFIX);
    }

    public void onEnable() {
        PAPIHook.load();
        BosterGUI.setup(this);

        Bukkit.getConsoleSender().sendMessage(PREFIX);

        registerListeners();
        registerCommands();
        setup();

        BosterCommand.load();

        startSaveTask();
        Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBambossSMP\u00a7d] \u00a7fThe plugin has been \u00a7dEnabled\u00a7f!");
        Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBambossSMP\u00a7d] \u00a7fPlugin creator: \u00a7dXuezimcmcas");
        Bukkit.getConsoleSender().sendMessage("\u00a7d[\u00a7bBambossSMP\u00a7d] \u00a7fPlugin version: \u00a7d" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(PREFIX);
    }

    public void onDisable() {
        save();
    }

    private void registerCommands() {
        new SMPCommand(this).register();

        ConfigurationSection c = Objects.requireNonNull(getConfig().getConfigurationSection("Commands"));

        new TPCommand(this, Objects.requireNonNull(c.getConfigurationSection("Teleport"))).register();
        new VanishCommand(this, Objects.requireNonNull(c.getConfigurationSection("Vanish"))).register();
        new GameModeCommand(this, Objects.requireNonNull(c.getConfigurationSection("GameMode"))).register();
        new FlyCommand(this, Objects.requireNonNull(c.getConfigurationSection("Fly"))).register();
        new FlySpeedCommand(this, Objects.requireNonNull(c.getConfigurationSection("FlySpeed"))).register();
        new WalkSpeedCommand(this, Objects.requireNonNull(c.getConfigurationSection("WalkSpeed"))).register();
        new HealCommand(this, Objects.requireNonNull(c.getConfigurationSection("Heal"))).register();
        new FeedCommand(this, Objects.requireNonNull(c.getConfigurationSection("Feed"))).register();
        new StaffChatCommand(this, Objects.requireNonNull(c.getConfigurationSection("StaffChat"))).register();
        new WeatherCommand(this, Objects.requireNonNull(c.getConfigurationSection("Weather"))).register();
        new TimeCommand(this, Objects.requireNonNull(c.getConfigurationSection("Time"))).register();
        new BanCommand(this, Objects.requireNonNull(c.getConfigurationSection("Ban"))).register();
        new UnbanCommand(this, Objects.requireNonNull(c.getConfigurationSection("Unban"))).register();
        new KickCommand(this, Objects.requireNonNull(c.getConfigurationSection("Kick"))).register();
        new InviteCommand(this, Objects.requireNonNull(c.getConfigurationSection("Invite"))).register();
        new InviteAcceptCommand(this, Objects.requireNonNull(c.getConfigurationSection("Accept"))).register();
        new TPACommand(this, Objects.requireNonNull(c.getConfigurationSection("TPA"))).register();
        new TPAcceptCommand(this, Objects.requireNonNull(c.getConfigurationSection("TPAccept"))).register();
        new TPADenyCommand(this, Objects.requireNonNull(c.getConfigurationSection("TPADeny"))).register();
        new DifficultyCommand(this, Objects.requireNonNull(c.getConfigurationSection("Difficulty"))).register();
        new PvPCommand(this, Objects.requireNonNull(c.getConfigurationSection("PvP"))).register();
        new EffectCommand(this, Objects.requireNonNull(c.getConfigurationSection("Effect"))).register();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new QuitNJoin(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
    }

    private void load() {
        Constants.load(getConfig());
        worldManager.load();
    }

    private void setup() {
        guiManager.init();

        chatFile.create();
        chatConfig = new YamlConfiguration();
        try {
            chatConfig.load(chatFile.getFile());
        } catch (Exception e) {
            throw new Error(e);
        }

        chatInstance = new Chat(chatConfig, "smp-chat-instance");
        chatInstance.clear();

        userManager.load();
        worldManager.loadWorlds();
    }

    public void save() {
        for(SMPUser user : userManager.all()) {
            userManager.saveUser(user);
        }

        for(SMPWorld world : worldManager.all()) {
            world.save();
        }
    }

    private void startSaveTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::save, 1200, 1200);
    }
}
