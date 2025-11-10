package cc.ranmc.city;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin implements Listener{
	
	public YamlConfiguration ipData;
	public File ipYml;
    public String ProtectWorld;
    public List<String> ProtectOreList;
  	private static Economy econ;
	
	@Override
	public void onEnable() {
		// 注册Event
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// 加载配置文件
		loadConfig();
		
		// 输出成功启动
		Bukkit.getConsoleSender().sendMessage("§b[CityPlugin] §aPlugin loaded success.§d-By Ranica");
	}
	
	// 加载配置文件
	public void loadConfig() {
		// 检查配置文件
		reloadConfig();
		
		// 加载IP地址
		ipYml = new File(getDataFolder(), "ip.yml");
		if (!ipYml.exists()) saveResource("ip.yml", true);
		ipData = YamlConfiguration.loadConfiguration(ipYml);
		
        // Vault插件
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            econ = Objects.requireNonNull(getServer().getServicesManager().getRegistration(Economy.class)).getProvider();
            print("§b[CityPlugin] §a成功加载Vault插件");
        } else {
            print("§b[CityPlugin] §c无法找到Vault插件,部分功能受限");
        }
        
        // 世界矿物保护
        ProtectWorld = getConfig().getString("ProtectWorld");
        ProtectOreList = getConfig().getStringList("ProtectOre");
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
			Player player = event.getPlayer();
			if (player.hasPermission("city.vip")) {
				event.allow();
			}
		}
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		if (event.getPlayer().hasPermission("city.vip") && event.getMessage().length() <= 40) {
			event.setMessage(textReplace(event.getMessage()));
		}
	}

	// 玩家加入事件
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		// 玩家进入提示
        if (player.hasPermission("city.svip")) {
            event.setJoinMessage(textReplace(getConfig().getString("JoinMessageSvip"),player));
        } else if (player.hasPermission("city.vip")) {
            event.setJoinMessage(textReplace(getConfig().getString("JoinMessageVip"),player));
        } else {
            event.setJoinMessage(textReplace(getConfig().getString("JoinMessage"),player));
        }
		
		// 保存玩家IP地址
        List<String> ipList = ipData.getStringList(player.getName());

        if (ipList.isEmpty()) {
            // 首次进入欢迎语
            say(textReplace(getConfig().getString("FirstJoinMessage"),player));
            // 首次进入执行指令
            getServer().dispatchCommand(getServer().getConsoleSender(), textReplace(getConfig().getString("RunCommandFirstJoin"), player));
        }

        String address = Objects.requireNonNull(player.getAddress()).getHostString();
        if (!ipList.contains(address)) ipList.add(address);
        ipData.set(player.getName(), ipList);
        try {
            ipData.save(ipYml);
        } catch (IOException ignored) {}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // 限制生存世界挖矿
        if (event.getBlock().getWorld()==Bukkit.getServer().getWorld(ProtectWorld)) {
            for (String s : ProtectOreList) {
                if (event.getBlock().getType() == Material.getMaterial(s)) {
                    player.sendMessage("§b[夜城] §c请前往资源世界挖矿");
                    event.setExpToDrop(0);
                    event.setDropItems(false);
                }
            }
        }
	}
	
	// 玩家退出事件
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
        
		// 玩家退出提示
        if (player.hasPermission("city.svip")) {
            event.setQuitMessage(textReplace(textReplace(getConfig().getString("QuitMessageSvip"),player)));
        } else if (player.hasPermission("city.vip")) {
            event.setQuitMessage(textReplace(textReplace(getConfig().getString("QuitMessageVip"),player)));
        } else {
            event.setQuitMessage(textReplace(textReplace(getConfig().getString("QuitMessage"),player)));
        }
	}

	// SHIFT+F指令
	@EventHandler
	public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();

		if (player.isSneaking() && player.hasPermission("city.shift")) {
			player.chat("/cd");
			event.setCancelled(true);
		}
	}

	// 补全指令
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias, String[] args) {
		if (alias.equalsIgnoreCase("city") && args.length == 1) {
			return Arrays.asList("info", "help", "reload", "version");
        }
		if (alias.equalsIgnoreCase("city") && args.length == 2 && args[0].equals("info")) {
			return null;
		}
		return new ArrayList<>();
	}
	
	//指令输入
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("city") && args.length == 1) {
			// 重载
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("city.admin")) {
					loadConfig();
					sender.sendMessage("§b[CityPlugin] §aReload complete");
				} else {
					sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
				}
				return true;
			}
			
			// 帮助
			if (args[0].equalsIgnoreCase("help")) {
				if (sender.hasPermission("city.user")) {
					sender.sendMessage(
							"""
									§e--------------------
									§bCityPlugin §dBy Ranica
									§b/city version
									§b/city reload
									§e--------------------""");
					} else {
						sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
					}
				return true;
			}
			
			// 版本
			if (args[0].equalsIgnoreCase("version")) {
				if (sender.hasPermission("city.user")) {
					sender.sendMessage(
							"§e--------------------\n" +
							"§bCityPlugin §dBy Ranica" +
							"\n§bVersion: "+getDescription().getVersion() +
							"\n§chttps://www.ranmc.cc\n§e--------------------");
					} else {
						sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
					}
				return true;
			}
			
		}
		
		// 获取玩家信息
		if (cmd.getName().equalsIgnoreCase("city") && args.length == 2) {
			if (args[0].equalsIgnoreCase("info")) {
				if (sender.hasPermission("city.admin")) {
					List<String> ipList = ipData.getStringList(args[1]);
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
	
	// 输出日志
	public static void print(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
	}
	
	// 服务器输出
	public void say(String s) {
		Bukkit.broadcastMessage(s);
	}
	
	// 文本替换
	public static String textReplace(String text) {
		if (text == null) {
			text = "";
            print("§b[CityPlugin] §c插件输出文本信息出错");
			} else {
				text=text.replace("&", "§");
			}
		return text;
	}

    public String textReplace(String text,Player p) {
        if (text == null) {
            text = "";
            print("§b[CityPlugin] §c插件输出文本信息出错");
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
