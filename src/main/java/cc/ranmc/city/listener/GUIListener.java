package cc.ranmc.city.listener;

import cc.ranmc.city.Main;
import cc.ranmc.city.util.BasicUtil;
import cc.ranmc.city.util.MoneyUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static cc.ranmc.city.util.BasicUtil.textReplace;
import static cc.ranmc.city.util.MoneyUtil.GUI_TITLE;

public class GUIListener implements Listener {

    /**
     * 菜单关闭
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        for (int i = 0; i < 45; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            returnItem(player, item);
        }
    }

    /**
     * 菜单点击
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (event.getView().getTitle().equals(textReplace(GUI_TITLE))) {
            Inventory inventory = event.getClickedInventory();
            if (inventory == null) return;
            if (inventory != player.getInventory()) {
                if (event.getRawSlot() >= 45) {
                    event.setCancelled(true);
                }
                int money = 0;
                for (int i = 0; i < 45; i++) {
                    money += MoneyUtil.countMoney(inventory.getItem(i));
                }
                inventory.setItem(49, BasicUtil.getItem(Material.VAULT, 1,
                        "&b存入现金",
                        "&b请在上方放入现金",
                        "&b本页金额&e " + money,
                        "&b你的余额&e " + (int) Main.getInstance().getEcon().getBalance(player)));
            }

            if (clicked == null && inventory == player.getInventory()) return;

            if (event.getRawSlot() == 45) {
                player.chat("/earn");
                return;
            }
            if (event.getRawSlot() == 49) {
                int money = 0;
                for (int i = 0; i < 45; i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item == null) continue;
                    if (MoneyUtil.isMoney(item)) {
                        money += MoneyUtil.countMoney(item);
                        inventory.setItem(i, new ItemStack(Material.AIR));
                    } else {
                        returnItem(player, item);
                    }
                }
                Main.getInstance().getEcon().depositPlayer(player, money);
                player.sendMessage(textReplace("&a夜城币 " + money + " 已经存入你的账户"));
                return;
            }
            if (event.getRawSlot() == 53) {
                player.closeInventory();
                return;
            }
        }
    }

    private static void returnItem(Player player, ItemStack item) {
        if (BasicUtil.isInventoryFull(player)) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(textReplace("&c请勿放入非现金,已掉落地面"));
        } else {
            player.getInventory().addItem(item);
            player.sendMessage(textReplace("&c请勿放入非现金,已返还背包"));
        }
    }
}
