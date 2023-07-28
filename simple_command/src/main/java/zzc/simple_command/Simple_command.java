package zzc.simple_command;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.black_ixx.playerpoints.PlayerPoints;

import net.milkbowl.vault.economy.Economy;

public final class Simple_command extends JavaPlugin {


    public static FileConfiguration config;
    public static PlayerPointsAPI ppAPI;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<String, String> speak_command_map = new HashMap<String,String>();
    public static HashMap<String, String> money_command_map = new HashMap<String, String>();
    public static HashMap<String, String> playpoint_command_map = new HashMap<String, String>();

    public static Economy econ = null;
    public static Plugin  vaultPlugin;
    public static Plugin  playpointsPlugin;



    @Override
    public void onEnable() {
        // 获取 PluginManager 对象
        Bukkit.getPluginManager().registerEvents(new listen_player(), this);
        PluginManager pluginManager = getServer().getPluginManager();

        // 判断是否安装了 Vault 插件
        vaultPlugin = pluginManager.getPlugin("Vault");
        if (vaultPlugin != null && vaultPlugin.isEnabled()) {
            // 如果 Vault 插件已安装且启用，导入 Vault 经济插件的包并初始化经济插件
            setupEconomy();
            log.info("§6Speak command: Vault plugin is installed!");
        } else {
            // 如果 Vault 插件未安装或未启用，执行其他逻辑
            log.info("§6Speak command: Vault plugin is not installed!");
        }

        playpointsPlugin = pluginManager.getPlugin("PlayerPoints");
        if (playpointsPlugin != null && playpointsPlugin.isEnabled()) {
            // 如果 Vault 插件已安装且启用，导入 Vault 经济插件的包并初始化经济插件
            ppAPI = PlayerPoints.getInstance().getAPI();
            log.info("§6Speak command: playpoints plugin is installed!");
        } else {
            // 如果 Vault 插件未安装或未启用，执行其他逻辑
            log.info("§6Speak command: playpoints plugin is not installed!");
        }

        Metrics metrics = new Metrics(this, 19222);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        System.out.println("§6speak command 插件启动中...");
        createConfig();
        loadConfig();

        // 提取和输出translation关键字中的指令和值
        List<String> transList = config.getStringList("commands");
        for (String entry : transList) {
            String[] data = entry.split(";");
            log.info("§6简化指令：§f"+entry);
            for (int i = 0; i < data.length; i++){
                if(i!=data.length-1){
                    String commandName = data[i].trim();
                    String command_content = data[data.length-1].trim();
//                    log.info(commandName);
//                    log.info(command_content);
                    speak_command_map.put(commandName,command_content);

                }
            }
        }
        log.info("");
        // 获取money_command关键字下的所有子键值对
        ConfigurationSection money_command = config.getConfigurationSection("money_command");
        if (money_command != null) {
            // 获取所有子关键字的名称
            for (String key : money_command.getKeys(false)) {
                log.info("§6注册金钱指令: §f" + key);
                ConfigurationSection _key = money_command.getConfigurationSection(key);
                String _listen = _key.getString("listen");
                String[] _listen_split = _listen.split(";");
                for (int i = 0; i < _listen_split.length; i++){
                    money_command_map.put(_listen_split[i],key);
                }
            }
        }
        log.info("");
        // 获取money_command关键字下的所有子键值对
        ConfigurationSection points_command = config.getConfigurationSection("points_command");
        if (points_command != null) {
            // 获取所有子关键字的名称
            for (String key : points_command.getKeys(false)) {
                log.info("§6注册点卷指令: §f" + key);
                ConfigurationSection _key = points_command.getConfigurationSection(key);
                String _listen = _key.getString("listen");
                String[] _listen_split = _listen.split(";");
                for (int i = 0; i < _listen_split.length; i++){
                    playpoint_command_map.put(_listen_split[i],key);
                }
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void createConfig() {
        // 获取插件数据文件夹
        File dataFolder = getDataFolder();

        // 如果插件数据文件夹不存在，创建它
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // 获取配置文件对象
        File configFile = new File(dataFolder, "config.yml");

        // 如果配置文件不存在，从资源文件中复制默认配置
        if (!configFile.exists()) {
            try (InputStream inputStream = getResource("config.yml")) {
                Files.copy(inputStream, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig() {
        // 加载config.yml文件
        config = getConfig();
        // 保存默认的config.yml文件到插件数据文件夹中（如果config.yml不存在）
        saveDefaultConfig();
    }


}
