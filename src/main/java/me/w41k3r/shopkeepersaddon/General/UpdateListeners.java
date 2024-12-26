package me.w41k3r.shopkeepersaddon.General;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

import static me.w41k3r.shopkeepersaddon.General.UIHandler.*;
import static me.w41k3r.shopkeepersaddon.General.Utils.debugLog;
import static me.w41k3r.shopkeepersaddon.Main.*;

public class UpdateListeners implements Listener {

    public static void updateConfig(FileConfiguration oldConfig, File configFile) {
        try {
            File backupFile = new File(configFile.getParent(), configFile.getName().replace(".yml", "-" + oldConfig.getString("version") + ".yml"));
            oldConfig.save(backupFile);
            if (configFile.delete()) {
                Bukkit.getLogger().info("Old config file deleted successfully.");
                plugin.saveDefaultConfig();
                FileConfiguration newConfig;
                plugin.reloadConfig();
                newConfig = plugin.getConfig();
                for (String key : newConfig.getKeys(true)) {
                    if (key.equalsIgnoreCase("version")) continue;
                    if (oldConfig.contains(key)) {
                        newConfig.set(key, oldConfig.get(key));
                    }
                }
                newConfig.save(configFile);

            } else {
                Bukkit.getLogger().info("Failed to delete the old config file.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to update config.");
        }
    }


    static void startUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                refreshShops();
                debugLog("Shops refreshed!");
            }
        }.runTaskTimerAsynchronously(plugin, setting().getLong("refresh-rate") * 20, setting().getLong("refresh-rate") * 20);
    }


    static void refreshShops() {
        FileConfiguration config;
        try {
            config = YamlConfiguration.loadConfiguration(new File(shopkeepersInstance.getDataFolder(), "data/save.yml"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        adminItemsList.clear();
        adminShops.clear();
        adminItems.clear();
        adminHeads.clear();
        adminShopItems.clear();

        playerItemsList.clear();
        playerShops.clear();
        playerItems.clear();
        playerHeads.clear();
        playerShopItems.clear();

        for (String key : config.getKeys(false)) {
            if (key.equalsIgnoreCase("data-version")) {
                continue;
            }
            readyItemsUI(config, key);
        }
    }
}