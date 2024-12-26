package me.w41k3r.shopkeepersaddon;

import me.w41k3r.shopkeepersaddon.Economy.EcoListeners;
import me.w41k3r.shopkeepersaddon.General.Listeners;
import me.w41k3r.shopkeepersaddon.General.UpdateListeners;
import me.w41k3r.shopkeepersaddon.General.VirtualOwner;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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

        saveDefaultConfig();

        plugin = this;
        shopkeepersInstance = getServer().getPluginManager().getPlugin("Shopkeepers");

        prefix = setting().getString("messages.prefix");

        // Vaultが読み込めなかった場合プラグインを無効にする
        if (!setupVault()) {
            this.getLogger().severe("Disabling due to Vault dependency error!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        debugLog("Shopkeepers plugin found, loading shops...");
        loadShops();
        debugLog("Starting plugin!");

        // コマンドの登録
        getCommand("shopkeepersaddon").setExecutor(new Commands());
        getCommand("shops").setExecutor(new Commands());
        getCommand("setshop").setExecutor(new Commands());
        getCommand("visitshop").setExecutor(new Commands());

        // イベントリスナーの登録
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new EcoListeners(), this);
        getServer().getPluginManager().registerEvents(new UpdateListeners(), this);

        // VirtualOwnerのインスタンスを生成
        virtualOwner = new VirtualOwner("ShopeepersAddon");
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
        player.sendMessage(prefix + message);
    }

    // get config
    public static FileConfiguration setting() {
        return plugin.getConfig();
    }
}