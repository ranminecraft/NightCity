package cc.ranmc.city.listener;

import cc.ranmc.city.util.BasicUtil;
import cc.ranmc.city.util.MoneyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static cc.ranmc.city.util.BasicUtil.returnItem;
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
        if (event.getView().getTitle().equals(BasicUtil.color(GUI_TITLE))) {
            Inventory inventory = event.getClickedInventory();
            if (inventory == null) return;
            if (inventory != player.getInventory() && event.getRawSlot() >= 45) {
                event.setCancelled(true);
            }
            if (clicked == null && inventory == player.getInventory()) return;

            if (event.getRawSlot() == 45) {
                player.chat("/earn");
                return;
            }
            if (event.getRawSlot() == 49) {
                MoneyUtil.save(player, inventory);
                return;
            }
            if (event.getRawSlot() == 53) {
                player.closeInventory();
                return;
            }
        }
    }
}
