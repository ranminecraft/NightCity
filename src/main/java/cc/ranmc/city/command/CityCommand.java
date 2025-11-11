package cc.ranmc.city.command;

import cc.ranmc.city.Main;
import cc.ranmc.city.util.BasicUtil;
import cc.ranmc.city.util.CardUtil;
import cc.ranmc.city.util.MoneyUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cc.ranmc.city.util.BasicUtil.color;

public class CityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, 
                             @NotNull Command cmd, 
                             @NotNull String label, 
                             String[] args) {

        if (args.length == 1) {
            // 重载
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("city.admin")) {
                    sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
                    return true;
                }
                Main.getInstance().loadConfig();
                sender.sendMessage("§b[夜城] §a重载成功");

                ((Player)sender).getInventory().addItem(CardUtil.getRandomCard());

                return true;
            }
            // 存钱
            if (args[0].equalsIgnoreCase("money") && sender instanceof Player player) {
                MoneyUtil.openGUI(player);
                return true;
            }
        }

        if (args.length == 2) {
            // 获取玩家信息
            if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("city.admin")) {
                    sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
                    return true;
                }
                List<String> ipList = Main.getInstance().getIpData().getStringList(args[1]);
                if (ipList.isEmpty()) {
                    sender.sendMessage("§b[夜城] §c没有找到该玩家的IP地址");
                    return true;
                }
                sender.sendMessage("§e找到" + ipList.size() + "个" + args[1] + "使用过的IP地址");
                for (String ipl : ipList) {
                    sender.sendMessage(BasicUtil.color("&e- " + ipl));
                }
                return true;
            }
        }
        sender.sendMessage("§b[夜城] §c未知指令,请检查后重新输入");
        return true;

    }
}
