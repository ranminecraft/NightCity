package cc.ranmc.city.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BasicUtil {

    // 输出日志
    public static void print(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    // 服务器输出
    public static void say(String s) {
        Bukkit.broadcastMessage(s);
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
}
