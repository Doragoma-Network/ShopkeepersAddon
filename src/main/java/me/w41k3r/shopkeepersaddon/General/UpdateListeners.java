package me.w41k3r.shopkeepersaddon.General;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

import static me.w41k3r.shopkeepersaddon.General.UIHandler.*;
import static me.w41k3r.shopkeepersaddon.Main.*;

public class UpdateListeners implements Listener {

    static void startUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                refreshShops();
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
