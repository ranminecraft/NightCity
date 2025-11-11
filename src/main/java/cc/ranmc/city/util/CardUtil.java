package cc.ranmc.city.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class CardUtil {

    private static final List<String> CARD_TYPE = List.of(
            "&a&lSSS",
            "&b&lS", "&b&lS",
            "&e&lA", "&e&lA", "&e&lA",
            "&c&lB", "&c&lB", "&c&lB", "&c&lB",
            "&4&lC", "&4&lC", "&4&lC", "&4&lC", "&4&lC");

    private static final List<String> CARD_STATUS = List.of(
            "&a&l全新",
            "&b&l优秀", "&b&l优秀",
            "&e&l良好", "&e&l良好", "&e&l良好",
            "&c&l差劲", "&c&l差劲", "&c&l差劲", "&c&l差劲",
            "&4&l损坏");

    public static ItemStack getRandomCard() {
        return getCard(CARD_TYPE.get(new Random().nextInt(CARD_TYPE.size())),
                CARD_STATUS.get(new Random().nextInt(CARD_STATUS.size())));
    }

    public static ItemStack getCard(String type, String status) {
        return BasicUtil.getItem(getMaterial(BasicUtil.color(type), status), 1,
                "&6&l显卡",
                "&6&l型号: " + type,
                "&6&l成色: " + status);
    }

    public static Material getMaterial(String type, String status) {
        if (status.contains("损坏")) return Material.MUSIC_DISC_11;
        return switch (ChatColor.stripColor(type)) {
            case "SSS" -> Material.MUSIC_DISC_TEARS;
            case "S" -> Material.MUSIC_DISC_CREATOR_MUSIC_BOX;
            case "A" -> Material.MUSIC_DISC_RELIC;
            case "B" -> Material.MUSIC_DISC_PRECIPICE;
            case "C" -> Material.MUSIC_DISC_STAL;
            default -> Material.MUSIC_DISC_11;
        };
    }

    public static String getCardType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return "";
        if (lore.size() != 2) return "";
        String line = lore.getFirst();
        if (!line.contains("型号: ")) return "";
        return line.split(" ")[1];
    }

    public static String getCardStatus(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return "";
        if (lore.size() != 2) return "";
        String line = lore.get(1);
        if (!line.contains("成色: ")) return "";
        return line.split(" ")[1];
    }


}
