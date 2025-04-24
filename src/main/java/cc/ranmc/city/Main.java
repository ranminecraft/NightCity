package cc.ranmc.city;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin implements Listener{
	
	public YamlConfiguration firstdata,ipdata,kickdata,spawndata,kitdata,kitlogdata;
	public File firstyml,ipyml,kickyml,spawnyml,kityml,kitlogyml;
	
	public Boolean WorldOreProtect,SpawnerDrop;
    public String ProtectWorld;
    public List<String> ProtectOreList;
    public int playMoney,PlayMoneyTestTime;
    public int PlayMoneyTestTimes = 0;
    public Boolean enablePlayMoney;
  	private static Economy econ;
  	public double spawnMobLimit;
    public RegisteredServiceProvider<Economy> rsp;
    private final Map<String, Integer> pOnline = new HashMap<>();
	
  	private BukkitTask task;
	
	@Override
	public void onDisable() {
		task.cancel();
		super.onDisable();
	}
	
	@Override
	public void onEnable(){
		
		// 注册Event
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// 加载配置文件
		loadConfig();
		
		// 输出成功启动
		Bukkit.getConsoleSender().sendMessage("§b[CityPlugin] §aPlugin loaded success.§d-By Ranica");
		
		// 计时器
		task = Bukkit.getScheduler().runTaskTimer(this, () -> {
			LocalDateTime dt = LocalDateTime.now();
			// 重置生物过多
			 if((dt.getHour()==23) && (dt.getMinute()==59) && (dt.getSecond()==59)) {
				 spawndata = new YamlConfiguration();
				try {
					spawndata.save(spawnyml);
				} catch (IOException e) {
					//e.printStackTrace();
				}
			 }

			for(Player player:Bukkit.getOnlinePlayers()) {
				// 在线送钱
				if(enablePlayMoney && PlayMoneyTestTimes%60==0) {
					int pos = pOnline.get(player.getName())+1;
					pOnline.put(player.getName(), pos);
				}
			}

			// 在线送钱
			if(enablePlayMoney) {
				PlayMoneyTestTimes++;
				if(PlayMoneyTestTimes>=PlayMoneyTestTime*60) {
					PlayMoneyTestTimes=0;
					say("§b[夜城] §e你已经在线一段时间了,输入/pm领取在线奖励");
				}
			}
		}, 20, 20);
		
	}
	
	// 加载配置文件
	public void loadConfig() {
		// 检查配置文件
		if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
			saveDefaultConfig();
		}
		reloadConfig();
		
		// 加载第一次进入信息
		firstyml = new File(this.getDataFolder(), "first.yml");
		if(!firstyml.exists()) {
			this.saveResource("first.yml", true);
		}
		
		firstdata = YamlConfiguration.loadConfiguration(firstyml);
		
		// 加载IP地址
		ipyml = new File(this.getDataFolder(), "ip.yml");
		if(!ipyml.exists()) {
			this.saveResource("ip.yml", true);
		}
		
		ipdata = YamlConfiguration.loadConfiguration(ipyml);
        
		// 加载踢出记录
		kickyml = new File(this.getDataFolder(), "kick.yml");
		if(!kickyml.exists()) {
			this.saveResource("kick.yml", true);
		}
		
		kickdata = YamlConfiguration.loadConfiguration(kickyml);
		
		// 限制生物过多
		spawnyml = new File(this.getDataFolder(), "spawn.yml");
		if(!spawnyml.exists()) {
			this.saveResource("spawn.yml", true);
		}
		
		spawndata = YamlConfiguration.loadConfiguration(spawnyml);
		
		kityml = new File(this.getDataFolder(), "kit.yml");
		if(!kityml.exists()) {
			this.saveResource("kit.yml", true);
		}
		
		kitdata = YamlConfiguration.loadConfiguration(kityml);
		
		kitlogyml = new File(this.getDataFolder(), "kitlog.yml");
		if(!kitlogyml.exists()) {
			this.saveResource("kitlog.yml", true);
		}
		
		kitlogdata = YamlConfiguration.loadConfiguration(kitlogyml);
		
        // Vault插件
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
        	rsp = getServer().getServicesManager().getRegistration(Economy.class);
            econ = Objects.requireNonNull(rsp).getProvider();
            outPut("§b[CityPlugin] §a成功加载Vault插件");
        }else {
       	 	outPut("§b[CityPlugin] §c无法找到Vault插件,部分功能受限");
        }

        //在线送钱
        playMoney = this.getConfig().getInt("PlayMoney");
        enablePlayMoney = this.getConfig().getBoolean("EnablePlayMoney");
        PlayMoneyTestTime = this.getConfig().getInt("PlayMoneyTestTime");
        
        //限制刷怪笼
        spawnMobLimit = this.getConfig().getDouble("SpawnMobLimit");
        
        //挖掘刷怪笼
        SpawnerDrop = this.getConfig().getBoolean("SpawnerDrop");
        
        //世界矿物保护
        WorldOreProtect = this.getConfig().getBoolean("WorldOreProtect");
        ProtectWorld = this.getConfig().getString("ProtectWorld");
        ProtectOreList = this.getConfig().getStringList("ProtectOre");
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
			Player player = event.getPlayer();
			if (player.hasPermission("city.svip")) {
				event.allow();
			}
		}
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		if (event.getPlayer().hasPermission("city.svip") && event.getMessage().length() <= 40) {
			event.setMessage(textReplace(event.getMessage()));
		}
	}
	
	//限制生物过多
	@EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
		if(this.getConfig().getBoolean("EnableSpawnLimit")) {
			EntityType et = event.getEntityType();
			Entity en = event.getEntity();
			List<String> spawnList = this.getConfig().getStringList("SpawnLimitList");
			for (String s : spawnList) {
				if (s.equalsIgnoreCase(et.toString())) {
					int lx = en.getLocation().getBlockX() / 100;
					int lz = en.getLocation().getBlockZ() / 100;
					if (en.getLocation().getBlockX() < 0) {
						lx--;
					}
					if (en.getLocation().getBlockZ() < 0) {
						lz--;
					}
					String info = Objects.requireNonNull(en.getLocation().getWorld()).getName() + "#" + lx + "#" + lz + "#" + et;
					int ii = spawndata.getInt(info);
					if (ii >= this.getConfig().getInt("SpawnLimitCount")) {
						event.setCancelled(true);
					} else {
						ii++;
						spawndata.set(info, ii);
						try {
							spawndata.save(spawnyml);
						} catch (IOException e) {
							//e.printStackTrace();
						}
					}
				}
			}
			
		}
		event.getEntity().getType();
	}

	// 玩家加入事件
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		
		Player p = event.getPlayer();
		
		// 检查配置文件丢失重新创建
		if(!firstyml.exists()) {
			this.saveResource("first.yml", true);
			firstdata = YamlConfiguration.loadConfiguration(firstyml);
		}
		
		if(!ipyml.exists()) {
			this.saveResource("ip.yml", true);
			ipdata = YamlConfiguration.loadConfiguration(ipyml);
		}
		
		// 在线送钱
		if(enablePlayMoney) {
			pOnline.put(p.getName(), 0);
		}
		
		// 玩家进入提示
		if(this.getConfig().getBoolean("EnableText")){
			if (p.hasPermission("city.svip")) {
				event.setJoinMessage(textReplace(this.getConfig().getString("JoinMessageSvip"),p));
			} else if (p.hasPermission("city.vip")) {
				event.setJoinMessage(textReplace(this.getConfig().getString("JoinMessageVip"),p));
			} else {
				event.setJoinMessage(textReplace(this.getConfig().getString("JoinMessage"),p));
			}
		}
		
		if(firstdata.getString(p.getName())==null) {
			firstdata.set(p.getName(), 1);
			//首次进入欢迎语
			if(this.getConfig().getBoolean("EnableWelcomeFirstJoin")) {
				say(textReplace(this.getConfig().getString("FirstJoinMessage"),p));
			}
			
			//首次进入执行指令
			if(this.getConfig().getBoolean("EnableCommandFirstJoin")) {
				getServer().dispatchCommand(getServer().getConsoleSender(), textReplace(this.getConfig().getString("RunCommandFirstJoin"),p));
			}
		
			try {
				firstdata.save(firstyml);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		//保存玩家IP地址
		if(this.getConfig().getBoolean("EnableSaveIP")){
			List<String> ipls = ipdata.getStringList(p.getName());
			String pip = Objects.requireNonNull(p.getAddress()).getHostString();
			if(!ipls.contains(pip)) {
				ipls.add(pip);
			}
			ipdata.set(p.getName(), ipls);
			try {
				ipdata.save(ipyml);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event){
		if(Bukkit.getWorld(Objects.requireNonNull(this.getConfig().getString("AntiThrowWorld")))==event.getPlayer().getWorld()&&this.getConfig().getBoolean("AntiThrowEgg")) {
			event.setHatching(false);
		}
    }
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // 限制生存世界挖矿
        if(WorldOreProtect) {
        	if(event.getBlock().getWorld()==Bukkit.getServer().getWorld(ProtectWorld)) {
				for (String s : ProtectOreList) {
					if (event.getBlock().getType() == Material.getMaterial(s)) {
						player.sendMessage("§b[夜城] §c请前往资源世界挖矿");
						event.setExpToDrop(0);
						event.setDropItems(false);
					}

				}
            }
        }
        
        // 挖掘刷怪笼
        if(player.hasPermission("city.vip")&&SpawnerDrop) {
        	if(event.getBlock().getType()==Material.SPAWNER) {
        		event.setExpToDrop(0);
        		ItemStack item = new ItemStack(Material.SPAWNER);
        		player.getInventory().addItem(item);
        	}
        }
        
        
	}
	
	//玩家退出事件
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();
        
		//玩家退出提示
		if(this.getConfig().getBoolean("EnableText")){
			if (p.hasPermission("city.svip")) {
				event.setQuitMessage(textReplace(textReplace(this.getConfig().getString("QuitMessageSvip"),p)));
			} else if (p.hasPermission("city.vip")) {
				event.setQuitMessage(textReplace(textReplace(this.getConfig().getString("QuitMessageVip"),p)));
			} else {
				event.setQuitMessage(textReplace(textReplace(this.getConfig().getString("QuitMessage"),p)));
			}
		}
	}
	
	//限制刷怪笼
	@EventHandler
	private void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
        	double i = Math.random();
            if (i > spawnMobLimit) {
                event.setCancelled(true);
            }
            
            if(this.getConfig().getBoolean("AntiSpawnerVillager")) {
            	if(event.getEntityType()==EntityType.VILLAGER||event.getEntityType()==EntityType.WANDERING_TRADER) {
            		event.setCancelled(true);
            	}
    		}
        }
    }

	//SHIFT+F指令
	@EventHandler
	public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
		Player player = event.getPlayer();

		if (player.isSneaking() && player.hasPermission("city.shift")) {
			player.chat("/cd");
			event.setCancelled(true);
		}
	}

	//补全指令
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
	public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args){
		
		if (cmd.getName().equalsIgnoreCase("city") && args.length == 1) {
			//重载
			if (args[0].equalsIgnoreCase("reload")){
				if(sender.hasPermission("city.admin")) {
					loadConfig();
					sender.sendMessage("§b[CityPlugin] §aReload complete");
				} else {
					sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
				}
				return true;
			}
			
			//帮助
			if (args[0].equalsIgnoreCase("help")){
				if(sender.hasPermission("city.user")) {
					sender.sendMessage(
							"""
									§e--------------------
									§bCityPlugin §dBy Ranica
									§b/pm
									§b/city kit
									§b/city version
									§b/city reload
									§e--------------------""");
					} else {
						sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
					}
				return true;
			}
			
			//版本
			if (args[0].equalsIgnoreCase("version")){
				if(sender.hasPermission("city.user")) {
					sender.sendMessage(
							"§e--------------------\n" +
							"§bCityPlugin §dBy Ranica" +
							"\n§bVersion: "+getDescription().getVersion() +
							"\n§chttp://www.xyfcm.top\n§e--------------------");
					} else {
						sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
					}
				return true;
			}
			
		}
		
		// 获取玩家信息
		if (cmd.getName().equalsIgnoreCase("city") && args.length == 2) {
			if (args[0].equalsIgnoreCase("info")){
				if(sender.hasPermission("city.admin")) {
					List<String> ipls = ipdata.getStringList(args[1]);
					if(ipls.isEmpty()) {
						sender.sendMessage("§b[夜城] §c没有找到该玩家的IP地址");
					}else {
						sender.sendMessage("§e找到"+ipls.size()+"个"+args[1]+"使用过的IP地址");
						for (String ipl : ipls) {
							sender.sendMessage(textReplace("&e- " + ipl));
						}
					}
				} else {
				sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
				}
			return true;
			}
		}
		
		// 以下指令不能在控制台输入
		if (!(sender instanceof Player player)) {
			outPut("§b[夜城] §c该指令不能在控制台输入");
			return true;
	    }
		
		// 在线送钱
		if (cmd.getName().equalsIgnoreCase("pm")) {
			if(sender.hasPermission("city.user") && enablePlayMoney) {
				int onlineTime = pOnline.get(player.getName());
				int getMoney = playMoney * onlineTime;
				int reward;
				String text = "";
				if (player.hasPermission("city.vip")) {
					reward = (int) (0.5 * playMoney);
					text = "§c[VIP额外获得" + reward + "]";
					econ.depositPlayer(player, reward);
				} else if (player.hasPermission("city.svip")) {
					reward = playMoney;
					text = "§6[SVIP额外获得" + reward + "]";
					econ.depositPlayer(player, reward);
				}
				if (econ.depositPlayer(player, getMoney).transactionSuccess()) {
					player.sendMessage("§b[夜城] §a你在线了§c" + onlineTime + "§a分钟,获得§c" + getMoney + "§a金币" + text);
					pOnline.put(player.getName(),0);
				}else {
					player.sendMessage("§b[夜城] §c在线奖励领取失败,请联系管理员");
				}
				
			} else {
				sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
			}
			return true;
		}
		
		//回城
		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if(sender.hasPermission("city.user")) {
				player.chat("/res tp spawn");
				
			} else {
				sender.sendMessage("§b[夜城] §c你没有足够的权限执行");
			}
			return true;
		}
		
		sender.sendMessage("§b[夜城] §c未知指令,请检查后重新输入");
		return true;
		
	}
	
	//输出日志
	public static void outPut(String s){
        Bukkit.getConsoleSender().sendMessage(s);
	}
	
	//服务器输出
	public void say(String s){
		Bukkit.broadcastMessage(s);
	}
	
	//文本替换
	public static String textReplace(String text) {
		if(text == null) {
			text = "";
			outPut("§b[CityPlugin] §c插件输出文本信息出错");
			}else {
				text=text.replace("&", "§");
			}
		return text;
	}
	
	public String textReplace(String text,Player p) {
		if(text == null) {
			text = "";
			outPut("§b[CityPlugin] §c插件输出文本信息出错");
			}else {
				text=text.replace("&", "§")
					.replace("%player%",p.getName())
					.replace("%player_x%", String.valueOf(p.getLocation().getBlockX()))
					.replace("%player_y%", String.valueOf(p.getLocation().getBlockY()))
					.replace("%player_z%", String.valueOf(p.getLocation().getBlockZ()));
			}
		return text;
	}
	
}
