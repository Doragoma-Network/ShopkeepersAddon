package me.w41k3r.shopkeepersaddon;

import me.w41k3r.shopkeepersaddon.Economy.EcoListeners;
import me.w41k3r.shopkeepersaddon.General.Listeners;
import me.w41k3r.shopkeepersaddon.General.UpdateListeners;
import me.w41k3r.shopkeepersaddon.General.VirtualOwner;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import static me.w41k3r.shopkeepersaddon.General.UpdateListeners.updateConfig;
import static me.w41k3r.shopkeepersaddon.General.Utils.debugLog;
import static me.w41k3r.shopkeepersaddon.General.Utils.loadShops;

public final class Main extends JavaPlugin {

    public static Main plugin;
    public static Plugin shopkeepersInstance;

    public static Economy money;
    public static VirtualOwner virtualOwner;

    private static String prefix;

    @Override
    public void onEnable() {

        plugin = this;
        File configFile = new File(getDataFolder(), "config.yml");

        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultConfigStream = getResource("config.yml");
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));

        Bukkit.getLogger().info("Checking for config updates... " + oldConfig.getString("version") + " " + defaultConfig.getString("version"));


        if (oldConfig.getString("version") == null || oldConfig.getString("version").equals(defaultConfig.getString("version"))) {
            saveDefaultConfig();
        } else {
            updateConfig(oldConfig, configFile);
        }

        shopkeepersInstance = getServer().getPluginManager().getPlugin("Shopkeepers");
        prefix = getSettingString("messages.prefix");

        if (plugin.getConfig().getBoolean("economy.enabled")) {
            if (!setupVault()) {
                this.getLogger().severe("Disabling due to Vault dependency error!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        debugLog("Shopkeepers plugin found, loading shops...");
        loadShops();
        debugLog("Starting plugin!");

        // コマンドの登録
        getCommand("shopkeepersaddon").setExecutor(new Commands());
        getCommand("shops").setExecutor(new Commands());
        getCommand("setshop").setExecutor(new Commands());
        getCommand("visitshop").setExecutor(new Commands());

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new EcoListeners(), this);
        getServer().getPluginManager().registerEvents(new UpdateListeners(), this);

        virtualOwner = new VirtualOwner("ShopkeepersAddon");
    }

    private boolean setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            debugLog("Vault plugin not found!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            debugLog("No economy found!");
            return false;
        }
        money = rsp.getProvider();
        return true;
    }

    public static void sendPlayerMessage(Player player, String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message.replace('§', '&')).replace("\\n", "\n");
        player.sendMessage(prefix + formattedMessage);
    }


    // get config
    public static FileConfiguration setting() {
        return plugin.getConfig();
    }

    public static String getSettingString(String path) {
        String value = plugin.getConfig().getString(path);
        return ChatColor.translateAlternateColorCodes('&', value);
    }
}