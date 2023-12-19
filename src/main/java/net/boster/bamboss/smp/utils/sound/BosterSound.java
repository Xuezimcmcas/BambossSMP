package net.boster.bamboss.smp.utils.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BosterSound {

    public Sound sound;
    public int i = 1;

    public static BosterSound load(String s) {
        if(s == null) {
            return null;
        }

        BosterSound b = new BosterSound();
        try {
            String[] ss = s.split(":");
            b.sound = Sound.valueOf(ss[0]);
            b.i = Integer.parseInt(ss[1]);
        } catch (Exception e) {
            try {
                b.sound = Sound.valueOf(s);
            } catch (Exception e1) {
                b = null;
            }
        }

        return b;
    }

    public String toString() {
        return sound.name() + ":" + i;
    }

    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), sound, 1, i);
    }
}
