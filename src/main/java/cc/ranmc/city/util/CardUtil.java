package cc.ranmc.city.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class CardUtil {

    private static final List<String> CARD_TYPE = List.of(
            "&aSSS",
            "&bS", "&bS", "&bS",
            "&eA", "&eA", "&eA", "&eA",
            "&cB", "&cB", "&cB", "&cB", "&cB",
            "&4C", "&4C", "&4C", "&4C", "&4C", "&4C");

    private static final List<String> CARD_STATUS = List.of(
            "&a全新",
            "&b优秀", "&b优秀", "&b优秀",
            "&e良好", "&e良好", "&e良好", "&e良好",
            "&c差劲", "&c差劲", "&c差劲", "&c差劲", "&c差劲",
            "&4损坏");

    public static ItemStack getRandomCard() {
        return BasicUtil.getItem(Material.OAK_HANGING_SIGN, 1,
                "&6&l显卡",
                "&b&l型号: " + CARD_TYPE.get(new Random().nextInt(CARD_TYPE.size())),
                "&b&l成色: " + CARD_STATUS.get(new Random().nextInt(CARD_STATUS.size())));
    }

    public static String getCardType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return "";
        if (lore.size() < 2) return "";
        String line = ChatColor.stripColor(lore.get(1));
        if (!line.startsWith("型号: ")) return "";
        return line.split(" ")[1];
    }

    public static String getCardStatus(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return "";
        if (lore.size() < 3) return "";
        String line = ChatColor.stripColor(lore.get(2));
        if (!line.startsWith("成色: ")) return "";
        return line.split(" ")[1];
    }


}
