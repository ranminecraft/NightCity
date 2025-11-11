package cc.ranmc.city.util;

import cc.ranmc.city.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static cc.ranmc.city.util.BasicUtil.textReplace;

public class MoneyUtil {

    public static final ItemStack PANE = BasicUtil.getItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ");
    private static ItemStack MONEY_1000, MONEY_10000;
    public static final String GUI_TITLE = textReplace("&9&l夜城 &0- &e&l存入现金");

    private static void init() {
        if (MONEY_1000 != null) return;
        MONEY_1000 = BasicUtil.getItem(Material.FIRE_CHARGE, 1,
                "&b夜城币", "&e面值: 100");
        ItemMeta meta = MONEY_1000.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.FLAME, 1, false);
        MONEY_1000.setItemMeta(meta);

        MONEY_10000 = BasicUtil.getItem(Material.FIRE_CHARGE, 1,
                "&b夜城币", "&e面值: 1000");
        meta = MONEY_10000.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.FLAME, 1, false);
        MONEY_10000.setItemMeta(meta);
    }

    public static ItemStack getMoney100Item() {
        init();
        return MONEY_1000.clone();
    }

    public static ItemStack getMoney1000Item() {
        init();
        return MONEY_10000.clone();
    }

    public static boolean isMoney(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return item.getType() == Material.FIRE_CHARGE && meta.hasLore() &&
                Objects.requireNonNull(meta.getLore()).getFirst().startsWith(textReplace("&e面值: "));
    }

    public static int countMoney(ItemStack item) {
        if (item == null) return 0;
        ItemMeta meta = item.getItemMeta();
        if (item.getType() != Material.FIRE_CHARGE || !meta.hasLore()) return 0;
        String lore = Objects.requireNonNull(meta.getLore()).getFirst();
        if (!lore.startsWith(textReplace("&e面值: "))) return 0;
        int money = 0;
        try {
            money = Integer.parseInt(lore.split(" ")[1]);
        } catch (NullPointerException ignored) {}
        return money;
    }

    public static void openGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, GUI_TITLE);

        inventory.setItem(45, BasicUtil.getItem(Material.RED_STAINED_GLASS_PANE, 1, "&c返回菜单"));
        inventory.setItem(46, PANE);
        inventory.setItem(47, PANE);
        inventory.setItem(48, PANE);
        inventory.setItem(49, BasicUtil.getItem(Material.VAULT, 1,
                "&b存入现金",
                "&b请在上方放入现金",
                "&b本页金额&e 0",
                "&b你的余额&e " + (int) Main.getInstance().getEcon().getBalance(player)));
        inventory.setItem(50, PANE);
        inventory.setItem(51, PANE);
        inventory.setItem(52, PANE);
        inventory.setItem(53, BasicUtil.getItem(Material.RED_STAINED_GLASS_PANE, 1, "&c关闭菜单"));
        player.openInventory(inventory);
    }

}
