package cc.ranmc.city.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static cc.ranmc.city.util.BasicUtil.textReplace;

public class CardUtil {

    public static ItemStack getRandomCard() {
        return new ItemStack(Material.OAK_HANGING_SIGN);
    }

}
