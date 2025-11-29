package cc.ranmc.city.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CityTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args) {
        if (args.length == 1) return List.of("info", "money", "vip");
        if (args.length == 2 && (args[0].equals("info") || args[0].equals("vip"))) return null;
        if (args.length == 3 && args[0].equals("vip")) return List.of("30");
        return new ArrayList<>();
    }
}
