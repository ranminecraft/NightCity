package cc.ranmc.city.listener;

import cc.ranmc.city.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import static cc.ranmc.city.util.BasicUtil.textReplace;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // 限制生存世界挖矿
        if (event.getBlock().getWorld().getName()
                .equalsIgnoreCase(Main.getInstance().getConfig().getString("ProtectWorld"))) {
            for (String ore : Main.getInstance().getConfig().getStringList("ProtectOre")) {
                if (event.getBlock().getType() == Material.getMaterial(ore)) {
                    player.sendMessage("§b[夜城] §c请前往资源世界挖矿");
                    event.setExpToDrop(0);
                    event.setDropItems(false);
                }
            }
        }
    }
}
