package net.boster.bamboss.smp.gui.item;

import lombok.Getter;
import lombok.Setter;
import net.boster.bamboss.smp.utils.Utils;
import net.boster.bamboss.smp.utils.sound.BosterSound;
import net.boster.gui.button.SimpleButtonItem;
import net.boster.gui.utils.PlaceholdersProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class MaterialIconItem extends SimpleButtonItem {

    @Nullable private BosterSound selectSound;
    @Nullable private BosterSound denySound;

    @Nullable private String message;
    @Nullable private String denyMessage;

    public MaterialIconItem(@NotNull ConfigurationSection section) {
        super(section);

        selectSound = BosterSound.load(section.getString("SelectSound"));
        denySound = BosterSound.load(section.getString("DenySound"));
        message = Utils.toColor(section.getString("message"));
        denyMessage = Utils.toColor(section.getString("denyMessage"));
    }

    public void selected(@NotNull Player player, @NotNull PlaceholdersProvider provider) {
        if(selectSound != null) {
            selectSound.play(player);
        }
        if(message != null) {
            player.sendMessage(provider.applyPlaceholders(player, message));
        }
    }

    public void deny(@NotNull Player player, @NotNull PlaceholdersProvider provider) {
        if(denySound != null) {
            denySound.play(player);
        }
        if(denyMessage != null) {
            player.sendMessage(provider.applyPlaceholders(player, denyMessage));
        }
    }
}
