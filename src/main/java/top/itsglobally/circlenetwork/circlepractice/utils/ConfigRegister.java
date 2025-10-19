package top.itsglobally.circlenetwork.circlepractice.utils;

import top.itsglobally.circlenetwork.circlepractice.CirclePractice;
import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;

public class ConfigRegister {
    private static File configDir;
    private static final CirclePractice plugin = CirclePractice.getPlugin();

    public static <T extends BaseConfig> T register(T config, String name) {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();
        File file = new File(configDir, name + ".yml");
        config.initFile(file);
        config.reload();
        return config;
    }
}
