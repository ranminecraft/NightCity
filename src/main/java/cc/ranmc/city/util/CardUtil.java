package cc.ranmc.city.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

import static cc.ranmc.city.constans.CardStatus.BAD;
import static cc.ranmc.city.constans.CardStatus.BEST;
import static cc.ranmc.city.constans.CardStatus.BROKEN;
import static cc.ranmc.city.constans.CardStatus.FINE;
import static cc.ranmc.city.constans.CardStatus.GOOD;
import static cc.ranmc.city.constans.CardType.A;
import static cc.ranmc.city.constans.CardType.B;
import static cc.ranmc.city.constans.CardType.C;
import static cc.ranmc.city.constans.CardType.S;
import static cc.ranmc.city.constans.CardType.SSS;

public class CardUtil {

    private static final List<String> CARD_TYPE = List.of(
            SSS,
            S, S,
            A, A, A,
            B, B, B, B,
            C, C, C, C, C);

    private static final List<String> CARD_STATUS = List.of(
            BEST,
            GOOD, GOOD,
            FINE, FINE, FINE,
            BAD, BAD, BAD, BAD);

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
        if (status.contains(BROKEN)) return Material.MUSIC_DISC_11;
        return switch (type) {
            case SSS -> Material.MUSIC_DISC_TEARS;
            case S -> Material.MUSIC_DISC_CREATOR_MUSIC_BOX;
            case A -> Material.MUSIC_DISC_RELIC;
            case B -> Material.MUSIC_DISC_PRECIPICE;
            case C -> Material.MUSIC_DISC_STAL;
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
