package cc.ranmc.city.listener;

import cc.ranmc.city.Main;
import cc.ranmc.city.util.CardUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceListener implements Listener {

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        if (source.getType().toString().startsWith("MUSIC_DISC_")) return;
        String status = CardUtil.getCardStatus(source);
        if (status.isEmpty()) {
            event.setCancelled(true);
            event.setResult(new ItemStack(Material.AIR));
            return;
        }
         switch (status) {
            case "全新" -> status = "&b&l优秀";
            case "优秀" -> {
                if (Math.random() >= 0.1) {
                    status = "&e&l良好";
                }
            }
            case "良好" -> {
                if (Math.random() >= 0.2) {
                    status = "&c&l差劲";
                }
            }
            case "差劲" -> {
                if (Math.random() >= 0.3) {
                    status = "&c&l损坏";
                }
            }
        }
        String type = CardUtil.getCardType(source);
        String finalStatus = status;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> {
            Furnace furnace = (Furnace) event.getBlock().getState();
            ItemStack smelting = furnace.getSnapshotInventory().getSmelting();
            Bukkit.broadcastMessage("2");
            if (smelting == null || smelting.getType() == Material.AIR) {
                furnace.getSnapshotInventory().setSmelting(CardUtil.getCard(type, finalStatus));
                furnace.update();
                Bukkit.broadcastMessage("1");
            } else {
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), CardUtil.getCard(type, finalStatus));
            }
        }, 2);
    }
}
