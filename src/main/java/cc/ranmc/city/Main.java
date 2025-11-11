package cc.ranmc.city;

import java.io.File;
import java.util.List;
import java.util.Objects;

import cc.ranmc.city.command.CityCommand;
import cc.ranmc.city.command.CityTabComplete;
import cc.ranmc.city.listener.BlockListener;
import cc.ranmc.city.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import static cc.ranmc.city.util.BasicUtil.print;
import static cc.ranmc.city.util.BasicUtil.textReplace;

public class Main extends JavaPlugin implements Listener{

    @Getter
	private YamlConfiguration ipData;
    @Getter
    private File ipYml;
    @Getter
    private String ProtectWorld;
    @Getter
    private List<String> ProtectOreList;
    @Getter
  	private Economy econ;
    @Getter
    private static Main instance;
    
	@Override
	public void onEnable() {
        instance = this;
		// 注册 Event
		Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        // 注册指令
        PluginCommand cityCommand = Objects.requireNonNull(Bukkit.getPluginCommand("city"));
        cityCommand.setExecutor(new CityCommand());
        cityCommand.setTabCompleter(new CityTabComplete());

		// 加载配置文件
		loadConfig();
		
		// 输出成功启动
		Bukkit.getConsoleSender().sendMessage(textReplace("&b[夜城] &a插件已经成功加载 &dBy Ranica"));
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
            print("§b[夜城] §a成功加载Vault插件");
        } else {
            print("§b[夜城] §c无法找到Vault插件,部分功能受限");
        }
        
        // 世界矿物保护
        ProtectWorld = getConfig().getString("ProtectWorld");
        ProtectOreList = getConfig().getStringList("ProtectOre");
	}
	
}
