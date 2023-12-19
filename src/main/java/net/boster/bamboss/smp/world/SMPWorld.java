package net.boster.bamboss.smp.world;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.files.SMPFile;
import net.boster.bamboss.smp.user.SMPUser;
import net.boster.bamboss.smp.utils.Constants;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.utils.WorldUtils;
import net.boster.chat.common.chat.Chat;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class SMPWorld {

    @NotNull private final SMPFile file;
    @NotNull private final SMPUser owner;
    @NotNull private SMPWorldSettings settings = new SMPWorldSettings(this);
    @NotNull private final List<String> operators;
    @NotNull private final SMPWorldPunishments punishments = new SMPWorldPunishments(this);
    @NotNull private SMPUsers userData = new SMPUsers(this);

    @NotNull private final World world;

    @Nullable private World nether;
    @Nullable private World theEnd;

    @NotNull private final MultiverseWorld mvWorld;

    @NotNull private List<UUID> liked = new ArrayList<>();

    private Chat chat;

    public SMPWorld(@NotNull SMPFile file) {
        this.file = file;
        this.owner = BambossSMP.getInstance().getUserManager().get(UUID.fromString(file.getConfiguration().getString("Owner", "")));
        this.settings = new SMPWorldSettings(this, Objects.requireNonNull(file.getConfiguration().getConfigurationSection("Settings")));
        this.operators = file.getConfiguration().getStringList("Operators");
        this.world = Objects.requireNonNull(Bukkit.getWorld(file.getName()));
        this.mvWorld = WorldUtils.getWorld(file.getName());

        String ls = file.getConfiguration().getString("Liked");
        if(ls != null) {
            liked = Utils.decodeSimple(ls);
        }

        ConfigurationSection ps = file.getConfiguration().getConfigurationSection("Punishments");
        if(ps != null) {
            punishments.load(ps);
        }

        ConfigurationSection ud = file.getConfiguration().getConfigurationSection("UserData");
        if(ud != null) {
            userData.load(ud);
        }
    }

    public @NotNull String getName() {
        return file.getName();
    }

    public @Nullable World getNether() {
        if(nether == null) {
            nether = Bukkit.getWorld(getName() + "_nether");
        }

        return nether;
    }

    public @Nullable World getTheEnd() {
        if(theEnd == null) {
            theEnd = Bukkit.getWorld(getName() + "_the_end");
        }

        return theEnd;
    }

    public <T> void setGameRule(@NotNull GameRule<T> rule, @NotNull T t) {
        applyForAllWorlds(w -> w.setGameRule(rule, t));
    }

    public void setupChat() {
        Chat i = BambossSMP.getInstance().getChatInstance();

        chat = new Chat();

        chat.setChatSettings(i.getChatSettings().clone());
        chat.getChatSettings().setWorlds(new ArrayList<>());

        chat.setSettings(i.getSettings().clone());
        chat.setCooldown(i.getCooldown() != null ? i.getCooldown().clone() : null);
        chat.setTagFeature(i.getTagFeature());
        chat.setRows(i.getRows());
        chat.setNamedRows(i.getNamedRows());
        chat.setRankColorMap(i.getRankColorMap());
        chat.setReplacesMap(i.getReplacesMap());

        chat.getChatSettings().getWorlds().add(getName());
        chat.getChatSettings().getWorlds().add(getName() + "_nether");
        chat.getChatSettings().getWorlds().add(getName() + "_the_end");
    }

    public void setWorldBorder(int size) {
        applyForAllWorlds(w -> w.getWorldBorder().setSize(size));
    }

    public void setDifficulty(@NotNull Difficulty difficulty) {
        applyForAllWorlds(w -> w.setDifficulty(difficulty));
    }

    public void applyForAllWorlds(@NotNull Consumer<World> consumer) {
        consumer.accept(world);

        if(getNether() != null) {
            consumer.accept(getNether());
        }

        if(getTheEnd() != null) {
            consumer.accept(getTheEnd());
        }
    }

    public void save() {
        YamlConfiguration c = file.getConfiguration();

        c.set("Owner", owner.getUuid().toString());
        c.set("Operators", operators);

        settings.serialize(c.createSection("Settings"));

        c.set("Liked", Utils.encode(liked));

        punishments.save(c.createSection("Punishments"));

        userData.save(c.createSection("UserData"));

        file.save();
    }

    public void delete() {
        BambossSMP.getInstance().getWorldManager().remove(this);
        file.getFile().delete();
        WorldUtils.deleteWorld(mvWorld.getName());
        WorldUtils.deleteWorld(getName() + "_nether");
        WorldUtils.deleteWorld(getName() + "_the_end");
    }

    public @NotNull List<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(p -> BambossSMP.getInstance().getWorldManager().removeWorldExtensionIfExists(p.getWorld().getName())
                .equals(getName())).collect(Collectors.toList());
    }

    public @Nullable Player getPlayer(@NotNull String s) {
        Player p = Bukkit.getPlayer(s);
        return getOnlinePlayers().contains(p) ? p : null;
    }

    public boolean canJoin(@NotNull Player player, boolean message) {
        if(getOnlinePlayers().size() >= settings.getMaxPlayers()) {
            if(isPermitted(BambossSMP.getInstance().getUserManager().get(player))) return true;

            if(message) {
                player.sendMessage(Constants.SERVER_FULL_MESSAGE);
            }
            return false;
        }

        return true;
    }

    public boolean isPermitted(@NotNull SMPUser user) {
        return owner == user || operators.contains(user.getName());
    }

    public boolean isAlias(@NotNull String name) {
        return getName().equals(name.replace("_nether", "").replace("_the_end", ""));
    }

    public static @NotNull File getFile(@NotNull String name) {
        return new File(BambossSMP.getInstance().getDataFolder(), "worlds/" + name + ".yml");
    }
}
