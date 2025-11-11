package cc.ranmc.city.command;

import cc.ranmc.city.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cc.ranmc.city.util.BasicUtil.textReplace;

public class CityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, 
                             @NotNull Command cmd, 
                             @NotNull String label, 
                             String[] args) {

        if (args.length == 1) {
            // 重载
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("city.admin")) {
                    Main.getInstance().loadConfig();
                    sender.sendMessage("§b[夜城] §aReload complete");
                } else {
                    sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
                }
                return true;
            }

        }

        // 获取玩家信息
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                if (sender.hasPermission("city.admin")) {
                    List<String> ipList = Main.getInstance().getIpData().getStringList(args[1]);
                    if (ipList.isEmpty()) {
                        sender.sendMessage("§b[夜城] §c没有找到该玩家的IP地址");
                    } else {
                        sender.sendMessage("§e找到" + ipList.size() + "个" + args[1] + "使用过的IP地址");
                        for (String ipl : ipList) {
                            sender.sendMessage(textReplace("&e- " + ipl));
                        }
                    }
                } else {
                    sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
                }
                return true;
            }
        }

        sender.sendMessage("§b[夜城] §c未知指令,请检查后重新输入");
        return true;

    }
}
