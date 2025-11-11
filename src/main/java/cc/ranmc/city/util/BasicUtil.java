package cc.ranmc.city.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BasicUtil {

    // 输出日志
    public static void print(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // 服务器输出
    public static void say(String message) {
        Bukkit.broadcastMessage(message);
    }

    // 文本替换
    public static String textReplace(String text) {
        if (text == null) {
            text = "";
            print("§b[夜城] §c插件输出文本信息出错");
        } else {
            text=text.replace("&", "§");
        }
        return text;
    }

    public static String textReplace(String text, Player p) {
        if (text == null) {
            text = "";
            print("§b[夜城] §c插件输出文本信息出错");
        } else {
            text=text.replace("&", "§")
                    .replace("%player%",p.getName())
                    .replace("%player_x%", String.valueOf(p.getLocation().getBlockX()))
                    .replace("%player_y%", String.valueOf(p.getLocation().getBlockY()))
                    .replace("%player_z%", String.valueOf(p.getLocation().getBlockZ()));
        }
        return text;
    }

    public static String getLocation(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    public static Location getLocation(String locationStr) {
        if (locationStr == null || locationStr.isEmpty()) return null;
        String[] date = locationStr.split(",");
        World world = Bukkit.getWorld(date[0]);
        if (world == null) {
            print("不存在世界" + locationStr);
            return null;
        }
        Location location = new Location(world,
                Double.parseDouble(date[1]),
                Double.parseDouble(date[2]),
                Double.parseDouble(date[3]));
        if (date.length == 6) {
            location.setYaw(Float.parseFloat(date[4]));
            location.setPitch(Float.parseFloat(date[5]));
        }
        return location;
    }
}
