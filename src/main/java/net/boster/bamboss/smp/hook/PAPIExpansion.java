package net.boster.bamboss.smp.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.boster.bamboss.smp.BambossSMP;
import net.boster.bamboss.smp.utils.Constants;
import net.boster.bamboss.smp.world.SMPWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "smp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Bosternike";
    }

    @Override
    public @NotNull String getVersion() {
        return BambossSMP.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) return "null player";

        if(params.equals("prefix")) {
            SMPWorld w = BambossSMP.getInstance().getWorldManager().getWorld(player.getWorld().getName());
            if(w == null) return Constants.LOBBY_WORLD_PREFIX;

            return w.getSettings().getPrefixOrName();
        }

        return "invalid request";
    }
}
