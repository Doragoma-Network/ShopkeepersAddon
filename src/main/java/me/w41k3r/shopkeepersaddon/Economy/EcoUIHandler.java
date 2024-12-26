package me.w41k3r.shopkeepersaddon.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;

import static me.w41k3r.shopkeepersaddon.Economy.EcoUtils.hasMoney;
import static me.w41k3r.shopkeepersaddon.Economy.EcoUtils.removeEconomyItem;
import static me.w41k3r.shopkeepersaddon.General.Utils.getPrice;
import static me.w41k3r.shopkeepersaddon.Main.*;

public class EcoUIHandler {

    /**
     * トレードの際のUIの項目を設定します。
     *
     * @param event トレードのイベント
     * @param slot  0: 買い物のアイテム、1: 売り物のアイテム
     */
    public static void setItemsOnTradeSlots(TradeSelectEvent event, int slot) {
        // 0番目のスロットには買い物のアイテムを、1番目のスロットには売り物のアイテムを設定
        ItemStack toAdd = slot == 0 ? event.getMerchant().getRecipe(event.getIndex()).getIngredients().get(0) : event.getMerchant().getRecipe(event.getIndex()).getResult();
        // 既にアイテムが存在する場合は、プレイヤーのインベントリーに追加
        if (event.getInventory().getItem(slot) != null) {
            event.getWhoClicked().getInventory().addItem(event.getInventory().getItem(slot));
        }
        // 買い物のアイテムの場合、プレイヤーの所持金額に応じて個数を調整
        if (slot == 0) {
            for (int i = 1; i <= 64; i++) {
                // プレイヤーの所持金額が足りない場合は、個数を減らす
                if (!hasMoney((Player) event.getWhoClicked(), getPrice(toAdd) * i)) {
                    break;
                }
                toAdd.setAmount(i);
            }
        }

        // アイテムをUIに追加
        event.getInventory().setItem(slot, toAdd);
        // 1tick後にプレイヤーのインベントリーからアイテムを削除
        Bukkit.getScheduler().runTaskLater(plugin, () -> removeEconomyItem((Player) event.getWhoClicked()), 1);
    }
}