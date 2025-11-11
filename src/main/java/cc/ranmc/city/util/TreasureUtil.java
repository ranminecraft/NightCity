package cc.ranmc.city.util;

import cc.ranmc.city.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static cc.ranmc.city.util.BasicUtil.print;

public class TreasureUtil {

    public static void generate() {
        if (Math.random() >= Main.getInstance().getConfig().getDouble("treasure.random", 0.5)) {
            //print("本次不生成宝藏");
            return;
        }
        World world = Bukkit.getWorld(Main.getInstance().getConfig().getString("treasure.world", "zy"));
        if (world == null) {
            //print("生成宝藏的世界为空");
            return;
        }
        List<String> worldList = Main.getInstance().getTreasureData().getStringList(world.getName());
        if (worldList.size() > Main.getInstance().getConfig().getInt("treasure.max", 5)) {
            return;
        }

        Random random = new Random();
        int radius = Main.getInstance().getConfig().getInt("treasure.radius", 25000);
        int x = random.nextInt(radius);
        if (random.nextBoolean()) x = -x;
        int z = random.nextInt(radius);
        if (random.nextBoolean()) z = -z;
        int finalX = x;
        int finalZ = z;
        world.getChunkAtAsync(new Location(world, x, 0, z)).thenAccept(_ -> {
            Block block = world.getHighestBlockAt(finalX, finalZ);
            Location location = block.getLocation();
            block.setType(Material.DECORATED_POT);
            if (block.getState() instanceof DecoratedPot pot) {
                pot.setSherd(DecoratedPot.Side.FRONT, Material.ARCHER_POTTERY_SHERD);
                pot.getSnapshotInventory().addItem(getTreasureItem());
                pot.update();
            }
            print("宝藏最终生成位置" + BasicUtil.getLocation(location));

            worldList.add(BasicUtil.getLocation(location));
            Main.getInstance().getTreasureData().set(world.getName(), worldList);
            try {
                Main.getInstance().getTreasureData().save(Main.getInstance().getTreasureYml());
            } catch (IOException e) {
                print("无法保存宝藏位置" + e.getMessage());
            }
        });
    }

    public static ItemStack getTreasureItem() {
        double random = Math.random();
        if (random < 0.1) {
            return MoneyUtil.getMoneyItem(500);
        } else if (random < 0.2) {
            return MoneyUtil.getMoneyItem(1000);
        } else if (random < 0.4) {
            ItemStack item = MoneyUtil.getMoneyItem(100);
            item.setAmount(new Random().nextInt(10) + 1);
            return item;
        } else if (random < 0.8) {
            return CardUtil.getRandomCard();
        } else {
            ItemStack diamond = new ItemStack(Material.DIAMOND);
            diamond.setAmount(new Random().nextInt(10) + 1);
            return diamond;
        }
    }

    public static boolean showDistance(Player player) {
        String treasureWorldName = Main.getInstance().getConfig().getString("treasure.world", "zy");
        if (!treasureWorldName.equals(player.getWorld().getName())) {
            player.sendMessage(BasicUtil.color("&b[夜城] &c当前世界没有宝藏，请前往资源世界"));
            return false;
        }
        final double[] distance = {999999};
        Location playerLocation = player.getLocation();
        List<String> worldList = Main.getInstance().getTreasureData().getStringList(treasureWorldName);
        if (worldList.isEmpty()) {
            player.sendMessage(BasicUtil.color("&b[夜城] &e宝藏已经全部找完了，请稍后再来吧"));
            return false;
        }
        AtomicReference<Location> location = new AtomicReference<>(player.getLocation());
        worldList.forEach(locStr -> {
            location.set(BasicUtil.getLocation(locStr));
            distance[0] = Math.min(playerLocation.distance(location.get()), distance[0]);
        });
        if (Material.DECORATED_POT != location.get().getBlock().getType()) {
            worldList.remove(BasicUtil.getLocation(location.get()));
            Main.getInstance().getTreasureData().set(treasureWorldName, worldList);
            try {
                Main.getInstance().getTreasureData().save(Main.getInstance().getTreasureYml());
            } catch (IOException e) {
                print("无法保存宝藏位置" + e.getMessage());
            }
            return showDistance(player);
        }
        player.sendMessage(BasicUtil.color("&b[夜城] &a找到宝藏距离你 &e" + String.format("%,.0f", distance[0]) + "m"));
        return true;
    }

    public static void blockBreak(Block block) {
        String treasureWorldName = Main.getInstance().getConfig().getString("treasure.world", "zy");
        if (!block.getWorld().getName().equalsIgnoreCase(treasureWorldName)) return;
        List<String> worldList = Main.getInstance().getTreasureData().getStringList(treasureWorldName);
        String blockLoc = BasicUtil.getLocation(block.getLocation());
        if (worldList.contains(blockLoc)) {
            worldList.remove(blockLoc);
            Main.getInstance().getTreasureData().set(treasureWorldName, worldList);
            try {
                Main.getInstance().getTreasureData().save(Main.getInstance().getTreasureYml());
            } catch (IOException e) {
                print("无法保存宝藏位置" + e.getMessage());
            }
        }
    }


}
