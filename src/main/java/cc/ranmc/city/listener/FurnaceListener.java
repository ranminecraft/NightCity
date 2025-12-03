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

import static cc.ranmc.city.constans.CardStatus.BAD;
import static cc.ranmc.city.constans.CardStatus.BEST;
import static cc.ranmc.city.constans.CardStatus.BROKEN;
import static cc.ranmc.city.constans.CardStatus.FINE;
import static cc.ranmc.city.constans.CardStatus.GOOD;

public class FurnaceListener implements Listener {

    @EventHandler
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        if (!source.getType().toString().startsWith("MUSIC_DISC_")) return;
        String status = CardUtil.getCardStatus(source);
        if (status.isEmpty()) {
            event.setCancelled(true);
            event.setResult(new ItemStack(Material.AIR));
            return;
        }
         switch (status) {
            case BEST -> status = GOOD;
            case GOOD -> {
                if (Math.random() < 0.02) {
                    status = FINE;
                }
            }
            case FINE -> {
                if (Math.random() < 0.04) {
                    status = BAD;
                }
            }
            case BAD -> {
                if (Math.random() < 0.08) {
                    status = BROKEN;
                }
            }
        }
        String type = CardUtil.getCardType(source);
        String finalStatus = status;
        Bukkit.getScheduler().runTask(Main.getInstance(), ()-> {
            Furnace furnace = (Furnace) event.getBlock().getState();
            ItemStack smelting = furnace.getSnapshotInventory().getSmelting();
            if (smelting == null || smelting.getType() == Material.AIR) {
                furnace.getSnapshotInventory().setSmelting(CardUtil.getCard(type, finalStatus));
                furnace.update();
            }/* else {
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(),
                        CardUtil.getCard(type, finalStatus));
            }*/
        });
    }
}
