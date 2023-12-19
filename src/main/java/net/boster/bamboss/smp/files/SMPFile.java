package net.boster.bamboss.smp.files;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@Getter
public class SMPFile {

    @NotNull
    private final String name;
    @NotNull private final File file;
    @NotNull private final YamlConfiguration configuration = new YamlConfiguration();

    public SMPFile(@NotNull File file) {
        this.file = file;
        this.name = file.getName().split(".yml")[0];
    }

    public void createFile() {
        if(!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadConfig() {
        try {
            configuration.load(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
