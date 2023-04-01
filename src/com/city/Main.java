package com.city;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.handy.playertitle.lib.util.BaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.handy.playertitle.api.PlayerTitleApi;
import com.handy.playertitle.api.param.PotionEffectParam;
import com.handy.playertitle.api.param.TitleBuffParam;
import com.handy.playertitle.api.param.TitleListParam;
import com.handy.playertitle.constants.BuffTypeEnum;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin implements Listener{
	
	public YamlConfiguration firstdata,ipdata,kickdata,spawndata,kitdata,kitlogdata,prefixdata;
	public File firstyml,ipyml,kickyml,spawnyml,kityml,kitlogyml,prefixyml;
	
	public Boolean WorldOreProtect,SpawnerDrop;
    public String ProtectWorld;
    public List<String> ProtectOreList;
    public int playMoney,PlayMoneyTestTime;
    public int PlayMoneyTestTimes = 0;
    public Boolean enablePlayMoney;
  	private static Economy econ;
  	public double spawnMobLimit;
    public RegisteredServiceProvider<Economy> rsp;
  	private SignMenuFactory signMenuFactory;
    private Map<String, Integer> pOnline = new HashMap<>();
    public PlayerTitleApi plt;
	
  	private BukkitTask task;
	
	public class hero {
		int health;
		String name;
	}
	
	@Override
	public void onDisable() {
		task.cancel();
		super.onDisable();
	}
	
	@Override
	public void onEnable(){
		
		//注册Event
		Bukkit.getPluginManager().registerEvents(this, this);
		
		//加载配置文件
		loadConfig();
		
		//输出成功启动
		Bukkit.getConsoleSender().sendMessage("§b[CityPlugin] §aPlugin loaded success.§d-By Ranica");
		
		//计时器
		task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
        	public void run() {
        		LocalDateTime dt = LocalDateTime.now();
        		//重置生物过多
         		if((dt.getHour()==23) && (dt.getMinute()==59) && (dt.getSecond()==59)) {
         			spawndata = new YamlConfiguration();
    				try {
    					spawndata.save(spawnyml);
    				} catch (IOException e) {
    					//e.printStackTrace();
    				}
         		}
        		
        		for(Player player:Bukkit.getOnlinePlayers()) {
        			//在线送钱
            		if(enablePlayMoney && PlayMoneyTestTimes%60==0) {
            			int pos = pOnline.get(player.getName())+1;
            			pOnline.put(player.getName(), pos);
            		}
        		}
        		
        		//在线送钱
        		if(enablePlayMoney) {
            		PlayMoneyTestTimes++;
            		if(PlayMoneyTestTimes>=PlayMoneyTestTime*60) {
            			PlayMoneyTestTimes=0;
            			say("§b[夜之城] §e你已经在线一段时间了,输入/pm领取在线奖励");
            		}
        		}
        	}
    	}, 20, 20);
		
	}
	
	//加载配置文件
	public void loadConfig() {
		//检查配置文件
		if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
			saveDefaultConfig();
		}
		reloadConfig();
		
		//加载第一次进入信息
		firstyml = new File(this.getDataFolder(), "first.yml");
		if(!firstyml.exists()) {
			this.saveResource("first.yml", true);
		}
		
		firstdata = YamlConfiguration.loadConfiguration(firstyml);
		
		//加载IP地址
		ipyml = new File(this.getDataFolder(), "ip.yml");
		if(!ipyml.exists()) {
			this.saveResource("ip.yml", true);
		}
		
		ipdata = YamlConfiguration.loadConfiguration(ipyml);
        
		//加载踢出记录
		kickyml = new File(this.getDataFolder(), "kick.yml");
		if(!kickyml.exists()) {
			this.saveResource("kick.yml", true);
		}
		
		kickdata = YamlConfiguration.loadConfiguration(kickyml);
		
		//限制生物过多
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
		
		prefixyml = new File(this.getDataFolder(), "prefix.yml");
		if(!prefixyml.exists()) {
			this.saveResource("prefix.yml", true);
		}
		
		prefixdata = YamlConfiguration.loadConfiguration(prefixyml);
		
        //Vault插件
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
        	rsp = getServer().getServicesManager().getRegistration(Economy.class);
            econ = rsp.getProvider();
            outPut("§b[CityPlugin] §a成功加载Vault插件");
        }else {
       	 	outPut("§b[CityPlugin] §c无法找到Vault插件,部分功能受限");
        }
        
        //加载ProtocolLib
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
       	 	outPut(textReplace("§b[CityPlugin] &a成功加载ProtocolLib"));
       	 	this.signMenuFactory = new SignMenuFactory(this);
        } else {
       	 	outPut(textReplace("§b[CityPlugin] &c无法找到ProtocolLib"));
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
        
        //称号插件
        plt = PlayerTitleApi.getInstance();
	}
	
	//菜单点击事件
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		if(clicked==null) {
			return;
		}
		
		if(event.getView().getTitle().contains(textReplace("&b&l夜之城&0-&e&l定制称号"))) {
			//取消点击
			event.setCancelled(true);
			
			if (event.getRawSlot()==7&&clicked.getType()==Material.NETHER_STAR) {
				List<String> pInfoList = prefixdata.getStringList(player.getName());
				if(pInfoList.size()==0) {
					pInfoList = new ArrayList<>();
					pInfoList.add("鸽子");
					pInfoList.add("力量(INCREASE_DAMAGE)");
					pInfoList.add("饱和(SATURATION)");
				}
				player.chat("/minepay buy 定制称号");
				//player.closeInventory();
			}
			
			if (event.getRawSlot()==1&&clicked.getType()==Material.NAME_TAG) {
				SignMenuFactory.Menu menu = signMenuFactory.newMenu(Arrays.asList("鸽子","","",""))
						.reopenIfFail(true)
			            .response((p, strings) -> {
			            	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
							scheduler.scheduleSyncDelayedTask(this, new Runnable() {
								@Override
								public void run() {
									setPrefixName(p,strings[0]);
								}
							},1);
			                return true;
			            });

			    menu.open(player);
			}
			
			if (event.getRawSlot()==3&&clicked.getType()==Material.ENCHANTED_BOOK) {
				
				Inventory inventory = Bukkit.createInventory(null, 18, textReplace("&b&l夜之城&0-&e&l选择属性①"));
				addCutomItem(inventory, "&b发光&f(GLOWING)" , "高亮显示玩家");
				addCutomItem(inventory, "&b跳跃提升&f&f(JUMP)", "让你跳的更高");
				addCutomItem(inventory, "&b饱和&f&f(SATURATION)", "不断恢复饱和");
				addCutomItem(inventory, "&b抗性提升&f(DAMAGE_RESISTANCE)", "减少受到伤害");
				addCutomItem(inventory, "&b防火&f(FIRE_RESISTANCE)", "免疫火焰伤害");
				addCutomItem(inventory, "&b力量&f(INCREASE_DAMAGE)", "造成更高伤害");
				addCutomItem(inventory, "&b急迫&f(FAST_DIGGING)", "提升挖掘速度");
				addCutomItem(inventory, "&b幸运&f(LUCK)", "提高掉落物奖励及钓鱼收益");
				addCutomItem(inventory, "&b生命恢复&f(REGENERATION)", "不断恢复生命");
				addCutomItem(inventory, "&b伤害吸收&f(ABSORPTION)", "吸收受到伤害");
				addCutomItem(inventory, "&b海豚的恩惠&f(DOLPHINS_GRACE)", "水下游得更快");
				addCutomItem(inventory, "&b潮涌能量&f(CONDUIT_POWER)", "在水下获得呼吸、夜视、速掘加成");
				addCutomItem(inventory, "&b村庄英雄&f(HERO_OF_THE_VILLAGE)", "村民交易时候享受折扣或赠送礼物");
				player.openInventory(inventory);
			}
			
			if (event.getRawSlot()==5&&clicked.getType()==Material.ENCHANTED_BOOK) {
				
				Inventory inventory = Bukkit.createInventory(null, 18, textReplace("&b&l夜之城&0-&e&l选择属性②"));
				addCutomItem(inventory, "&b发光&f(GLOWING)" , "高亮显示玩家");
				addCutomItem(inventory, "&b跳跃提升&f&f(JUMP)", "让你跳的更高");
				addCutomItem(inventory, "&b饱和&f&f(SATURATION)", "不断恢复饱和");
				addCutomItem(inventory, "&b抗性提升&f(DAMAGE_RESISTANCE)", "减少受到伤害");
				addCutomItem(inventory, "&b防火&f(FIRE_RESISTANCE)", "免疫火焰伤害");
				addCutomItem(inventory, "&b力量&f(INCREASE_DAMAGE)", "造成更高伤害");
				addCutomItem(inventory, "&b急迫&f(FAST_DIGGING)", "提升挖掘速度");
				addCutomItem(inventory, "&b幸运&f(LUCK)", "提高掉落物奖励及钓鱼收益");
				addCutomItem(inventory, "&b生命恢复&f(REGENERATION)", "不断恢复生命");
				addCutomItem(inventory, "&b伤害吸收&f(ABSORPTION)", "吸收受到伤害");
				addCutomItem(inventory, "&b海豚的恩惠&f(DOLPHINS_GRACE)", "水下游得更快");
				addCutomItem(inventory, "&b潮涌能量&f(CONDUIT_POWER)", "在水下获得呼吸、夜视、速掘加成");
				addCutomItem(inventory, "&b村庄英雄&f(HERO_OF_THE_VILLAGE)", "村民交易时候享受折扣或赠送礼物");
				player.openInventory(inventory);
			}
			
		}
		
		if(event.getView().getTitle().contains(textReplace("&b&l夜之城&0-&e&l选择属性①"))) {
			event.setCancelled(true);
			
			String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
			List<String> pInfoList = prefixdata.getStringList(player.getName());
			if(pInfoList.size()==0) {
				pInfoList = new ArrayList<>();
				pInfoList.add("鸽子");
				pInfoList.add("力量(INCREASE_DAMAGE)");
				pInfoList.add("饱和(SATURATION)");
			}
			pInfoList.set(1, name);
			prefixdata.set(player.getName(), pInfoList);
			try {
				prefixdata.save(prefixyml);
			} catch (IOException e) {
				//e.printStackTrace();
			}
			openDZInventory(player);
		}
		
		if(event.getView().getTitle().contains(textReplace("&b&l夜之城&0-&e&l选择属性②"))) {
			event.setCancelled(true);
			
			String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
			List<String> pInfoList = prefixdata.getStringList(player.getName());
			if(pInfoList.size()==0) {
				pInfoList = new ArrayList<>();
				pInfoList.add("鸽子");
				pInfoList.add("力量(INCREASE_DAMAGE)");
				pInfoList.add("饱和(SATURATION)");
			}
			pInfoList.set(2, name);
			prefixdata.set(player.getName(), pInfoList);
			try {
				prefixdata.save(prefixyml);
			} catch (IOException e) {
				//e.printStackTrace();
			}
			openDZInventory(player);
		}
	}
	
	public void addCutomItem(Inventory inventory,String name,String lore) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(textReplace("&b"+name));
		ArrayList<String> Lore = new ArrayList<String>();
        Lore.add(textReplace("&e"+lore));
        meta.setLore(Lore);
        item.setItemMeta(meta);
		inventory.addItem(item);
	}

	public static String clearColor(String text) {
		text = ChatColor.stripColor(textReplace(text));
		if (text.contains("#")) {
			text = Pattern.compile("(?i)#[A-Za-z0-9]{6}")
					.matcher(text)
					.replaceAll("")
					.replace("§", "");
		}
		return text;
	}

	public void setPrefixName(Player p,String name) {
		
		List<String> pInfoList = prefixdata.getStringList(p.getName());
		if(pInfoList.size() == 0) {
			pInfoList = new ArrayList<>();
			pInfoList.add("鸽子");
			pInfoList.add("力量(INCREASE_DAMAGE)");
			pInfoList.add("饱和(SATURATION)");
		}
		name = name.replace(" ", "")
				.replace("&k", "")
				.replace("&l", "")
				.replace("&m", "")
				.replace("&n", "")
				.replace("&o", "")
				.replace("&r", "");
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]{2,4}$");
		if(pattern.matcher(clearColor(textReplace(name))).matches()) {
			pInfoList.set(0, name);
			prefixdata.set(p.getName(), pInfoList);
			try {
				prefixdata.save(prefixyml);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		} else {
			p.sendMessage(textReplace("§b[夜之城] &c名称不规范,由2~4位中文组成"));
		}
		openDZInventory(p);
	}
	
	public void openDZInventory(Player p) {
		List<String> pInfoList = prefixdata.getStringList(p.getName());
		if(pInfoList.size()==0) {
			pInfoList = new ArrayList<>();
			pInfoList.add("鸽子");
			pInfoList.add("力量(INCREASE_DAMAGE)");
			pInfoList.add("饱和(SATURATION)");
		}
		
		Inventory inventory = Bukkit.createInventory(null, 9, textReplace("&b&l夜之城&0-&e&l定制称号"));
		ItemStack item1 = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = item1.getItemMeta();
        meta.setDisplayName(textReplace("&c确认购买"));
        ArrayList<String> Lore = new ArrayList<>();
        Lore.add(textReplace("&b价格： &a30 &b元"));
        Lore.add(textReplace("&b使用扫码支付"));
        Lore.add(textReplace("&b点击充值获得"));
        Lore.add(textReplace("&e购买期限：永久使用"));
        meta.setLore(Lore);
        item1.setItemMeta(meta);
         
        ItemStack item2 = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta2 = item2.getItemMeta();
        meta2.setDisplayName(textReplace("&b称号属性①"));
        ArrayList<String> Lore2 = new ArrayList<>();
        Lore2.add(textReplace("&9当前选择:&e "+pInfoList.get(1).split("\\u0028")[0]));
        Lore2.add(textReplace("&9点击选择称号属性"));
        meta2.setLore(Lore2);
        item2.setItemMeta(meta2);
        
        ItemStack item4 = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta4 = item4.getItemMeta();
        meta4.setDisplayName(textReplace("&b称号属性②"));
        ArrayList<String> Lore4 = new ArrayList<>();
        Lore4.add(textReplace("&9当前选择:&e "+pInfoList.get(2).split("\\u0028")[0]));
        Lore4.add(textReplace("&9点击选择称号属性"));
        meta4.setLore(Lore4);
        item4.setItemMeta(meta4);
        
        ItemStack item5 = new ItemStack(Material.NAME_TAG);
        ItemMeta meta5 = item5.getItemMeta();
        meta5.setDisplayName(textReplace("&b称号名称"));
        ArrayList<String> Lore5 = new ArrayList<>();
        Lore5.add(BaseUtil.replaceChatColor("&9当前名称: &f["+pInfoList.get(0)+"&f]"));
        Lore5.add(textReplace("&9点击输入称号名称"));
        Lore5.add(textReplace("&9颜色符号实例"));
        Lore5.add("§f&a亮绿->"+textReplace("&a亮绿")+"  §f&b亮蓝->"+textReplace("&b亮蓝"));
        Lore5.add("§f&c红色->"+textReplace("&c红色")+"  §f&d粉色->"+textReplace("&d粉色"));
        Lore5.add("§f&e黄色->"+textReplace("&e黄色")+"  §f&f白色->"+textReplace("&f白色"));
        Lore5.add("§f&0黑色->"+textReplace("&0黑色")+"  §f&1蓝色->"+textReplace("&1蓝色"));
        Lore5.add("§f&2绿色->"+textReplace("&2绿色")+"  §f&3青色->"+textReplace("&3青色"));
        Lore5.add("§f&4深红->"+textReplace("&4深红")+"  §f&5紫色->"+textReplace("&5紫色"));
        Lore5.add("§f&6金色->"+textReplace("&6金色")+"  §f&7浅灰->"+textReplace("&7浅灰"));
        Lore5.add("§f&8深灰->"+textReplace("&8深灰")+"  §f&9浅蓝->"+textReplace("&9浅蓝"));
        meta5.setLore(Lore5);
        item5.setItemMeta(meta5);

        ItemStack item3 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta3 = item3.getItemMeta();
        meta3.setDisplayName(textReplace("&7定制专属称号"));
        item3.setItemMeta(meta3);
        inventory.setItem(0, item3);
        inventory.setItem(2, item3);
        inventory.setItem(4, item3);
        inventory.setItem(6, item3);
        inventory.setItem(8, item3);
        inventory.setItem(1, item5);
        inventory.setItem(3, item2);
        inventory.setItem(5, item4);
        inventory.setItem(7, item1);
		p.openInventory(inventory);
	}
	
	//限制生物过多
	@EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
		if(this.getConfig().getBoolean("EnableSpawnLimit")) {
			EntityType et = event.getEntityType();
			Entity en = event.getEntity();
			List<String> spawnList = this.getConfig().getStringList("SpawnLimitList");
			for (int i = 0; i < spawnList.size(); i++) {
				if(spawnList.get(i).equalsIgnoreCase(et.toString())) {
					int lx = en.getLocation().getBlockX()/100;
					int lz = en.getLocation().getBlockZ()/100;
					if(en.getLocation().getBlockX()<0) {
						lx--;
					}
					if(en.getLocation().getBlockZ()<0) {
						lz--;
					}
					String info = en.getLocation().getWorld().getName()+"#"+lx+"#"+lz+"#"+et;
					int ii = spawndata.getInt(info);
					if(ii>=this.getConfig().getInt("SpawnLimitCount")) {
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
	
	//执行指令
	public void runc(String s,int i) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
			}
		},i);
	}
	
	//玩家加入事件
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		
		Player p = event.getPlayer();
		
		//检查配置文件丢失重新创建
		if(!firstyml.exists()) {
			this.saveResource("first.yml", true);
			firstdata = YamlConfiguration.loadConfiguration(firstyml);
		}
		
		if(!ipyml.exists()) {
			this.saveResource("ip.yml", true);
			ipdata = YamlConfiguration.loadConfiguration(ipyml);
		}
		
		//在线送钱
		if(enablePlayMoney) {
			pOnline.put(p.getName(), 0);
		}
		
		//玩家进入提示
		if(this.getConfig().getBoolean("EnableText")){
			if(p.hasPermission("city.vip")) {
				event.setJoinMessage(textReplace(this.getConfig().getString("JoinMessageVip"),p));
			}else {
				event.setJoinMessage(textReplace(this.getConfig().getString("JoinMessage"),p));
			}
		}
		
		if(firstdata.getString(p.getName())==null) {
			firstdata.set(""+p.getName(), 1);
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
			String pip = p.getAddress().getHostString();
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
		if(Bukkit.getWorld(this.getConfig().getString("AntiThrowWorld"))==event.getPlayer().getWorld()&&this.getConfig().getBoolean("AntiThrowEgg")) {
			event.setHatching(false);
		}
    }
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        //限制生存世界挖矿
        if(WorldOreProtect) {
        	if(event.getBlock().getWorld()==Bukkit.getServer().getWorld(ProtectWorld)) {
            	for(int i=0;i<ProtectOreList.size();i++) {
            		if(event.getBlock().getType()==Material.getMaterial(ProtectOreList.get(i))) {
            			player.sendMessage("§b[夜之城] §c请前往资源世界挖矿");
            			event.setExpToDrop(0);
            			event.setDropItems(false);
            		}
            		
            	}
            }
        }
        
        //挖掘刷怪笼
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
			if(p.hasPermission("city.vip")) {
				event.setQuitMessage(textReplace(textReplace(this.getConfig().getString("QuitMessageVip"),p)));
			}else {
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
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String[] commands = {"info","help","reload","version"};
		List<String> cmdls = new ArrayList<String>();
		if (alias.equalsIgnoreCase("city") && args.length == 1) {
			cmdls = Arrays.asList(commands);
        }
		return cmdls;
	}
	
	//指令输入
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if (cmd.getName().equalsIgnoreCase("city") && args.length == 1) {
			//重载
			if (args[0].equalsIgnoreCase("reload")){
				if(sender.hasPermission("city.admin")) {
					loadConfig();
					sender.sendMessage("§b[CityPlugin] §aReload complete");
				} else {
					sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
				}
				return true;
			}
			
			//帮助
			if (args[0].equalsIgnoreCase("help")){
				if(sender.hasPermission("city.user")) {
					sender.sendMessage(
							"§e--------------------\n" +
							"§bCityPlugin §dBy Ranica\n" +
							"§b/pm\n" + 
							"§b/city kit\n" + 
							"§b/city version\n" + 
							"§b/city reload\n" + 
							"§e--------------------");
					} else {
						sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
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
						sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
					}
				return true;
			}
			
		}
		
		//获取玩家信息
		if (cmd.getName().equalsIgnoreCase("city") && args.length == 2) {
			if (args[0].equalsIgnoreCase("info")){
				if(sender.hasPermission("city.admin")) {
					List<String> ipls = ipdata.getStringList(args[1]);
					if(ipls.size() == 0) {
						sender.sendMessage("§b[夜之城] §c没有找到该玩家的IP地址");
					}else {
						sender.sendMessage("§e找到"+ipls.size()+"个"+args[1]+"使用过的IP地址");
						for (int i = 0; i < ipls.size(); i++) {
							sender.sendMessage(textReplace("&e- "+ipls.get(i)));
				        }
					}
				} else {
				sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
				}
			return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("dz") && args.length == 1) {
			if(sender.hasPermission("city.admin")&&this.getConfig().getBoolean("EnableCutomPrefix")) {
				Player player = Bukkit.getPlayer(args[0]);
				if(player==null) {
					sender.sendMessage("§b[夜之城] 该玩家不在线");
					return true;
				}
				List<String> pInfoList = prefixdata.getStringList(player.getName());
				if(pInfoList.size()==0) {
					pInfoList = new ArrayList<>();
					pInfoList.add("鸽子");
					pInfoList.add("力量(INCREASE_DAMAGE)");
					pInfoList.add("饱和(SATURATION)");
				}
				TitleListParam titleListParam = new TitleListParam();
				titleListParam.setBuyType("activity");
				titleListParam.setAmount(99999);
				titleListParam.setDay(0);
				titleListParam.setIsHide(1);
				titleListParam.setDescription(player.getName() + "定制专属称号");
				titleListParam.setTitleName(BaseUtil.replaceChatColor("&f["+pInfoList.get(0)+"&f]"));
				List<TitleBuffParam> titleBuffs = new ArrayList<>();
				String buffStr1 = pInfoList.get(1);
				String buffStr2 = pInfoList.get(2);
				if(buffStr1.equals(buffStr2)) {
					TitleBuffParam buff1 = new TitleBuffParam();
					buff1.setBuffType(BuffTypeEnum.POTION_EFFECT);
					PotionEffectParam potionEffectParam = new PotionEffectParam();
					potionEffectParam.setPotionLevel(2);
					potionEffectParam.setPotionHide(true);
					potionEffectParam.setPotionChinesizationName(buffStr1.split("\\u0028")[0]);
					potionEffectParam.setPotionName(buffStr1.substring(buffStr1.indexOf("(")+1,buffStr1.indexOf(")")));
					buff1.setPotionEffectParam(potionEffectParam);
					titleBuffs.add(buff1);
				} else {
					TitleBuffParam buff1 = new TitleBuffParam();
					buff1.setBuffType(BuffTypeEnum.POTION_EFFECT);
					PotionEffectParam potionEffectParam = new PotionEffectParam();
					potionEffectParam.setPotionLevel(1);
					potionEffectParam.setPotionHide(true);
					potionEffectParam.setPotionChinesizationName(buffStr1.split("\\u0028")[0]);
					potionEffectParam.setPotionName(buffStr1.substring(buffStr1.indexOf("(")+1,buffStr1.indexOf(")")));
					buff1.setPotionEffectParam(potionEffectParam);
					titleBuffs.add(buff1);
					
					TitleBuffParam buff2 = new TitleBuffParam();
					buff2.setBuffType(BuffTypeEnum.POTION_EFFECT);
					PotionEffectParam potionEffectParam2 = new PotionEffectParam();
					potionEffectParam2.setPotionLevel(1);
					potionEffectParam2.setPotionHide(true);
					potionEffectParam2.setPotionChinesizationName(buffStr2.split("\\u0028")[0]);
					potionEffectParam2.setPotionName(buffStr2.substring(buffStr2.indexOf("(")+1,buffStr2.indexOf(")")));
					buff2.setPotionEffectParam(potionEffectParam2);
					titleBuffs.add(buff2);
				}
				titleListParam.setTitleBuffs(titleBuffs);
				plt.set(player.getName(), plt.add(titleListParam), 0);
				player.sendMessage("§b[夜之城] §a定制称号已发往你的仓库");
				sender.sendMessage("§b[夜之城] §a已经成功发放定制称号");
			} else {
				sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
			}
			return true;
		}
		
		//以下指令不能在控制台输入
		if (!(sender instanceof Player)) {
			outPut("§b[夜之城] §c该指令不能在控制台输入");
			return true;
	    }
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("dz") && args.length == 0) {
			if(sender.hasPermission("city.user")&&this.getConfig().getBoolean("EnableCutomPrefix")) {
				openDZInventory(p);
			} else {
				sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
			}
			return true;
		}
		
		//在线送钱
		if (cmd.getName().equalsIgnoreCase("pm")) {
			if(sender.hasPermission("city.user")&&enablePlayMoney) {
				int onlineTime = pOnline.get(p.getName());
				int getMoney = playMoney * onlineTime;
				EconomyResponse pm = econ.depositPlayer(p, getMoney);
				if(pm.transactionSuccess()) {
					p.sendMessage("§b[夜之城] §a你在线了 §c"+onlineTime+" §a分钟,获得 §c"+getMoney+" §a金币");
					pOnline.put(p.getName(),0);
				}else {
					p.sendMessage("§b[夜之城] §c在线奖励领取失败,请联系管理员");
				}
				
			} else {
				sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
			}
			return true;
		}
		
		//回城
		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if(sender.hasPermission("city.user")) {
				p.chat("/res tp spawn");
				
			} else {
				sender.sendMessage("§b[夜之城] §c你没有足够的权限执行");
			}
			return true;
		}
		
		sender.sendMessage("§b[夜之城] §c未知指令,请检查后重新输入");
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
					.replace("%player_x%",""+p.getLocation().getBlockX())
					.replace("%player_y%",""+p.getLocation().getBlockY())
					.replace("%player_z%",""+p.getLocation().getBlockZ());
			}
		return text;
	}
	
}
