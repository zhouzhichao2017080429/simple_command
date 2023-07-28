# speak_command
**轻量化口语指令插件**

![喇叭](https://github.com/zhouzhichao2017080429/simple_command/assets/73045175/e8f903f4-d1d7-44a0-b2f0-8644a9edda9f)





*你是否想体验刚一说出口事情就发生的那种超能力的快感，当你大喊回家你便回到了家，当你大喊回主城便回到了主城......*

**插件效果展示：**

![image](https://github.com/zhouzhichao2017080429/simple_command/assets/73045175/2e12add0-2b9c-41e2-bc3b-7255a5dc834e)
![image](https://github.com/zhouzhichao2017080429/simple_command/assets/73045175/bf71e813-0a05-4249-b936-f97029342c4f)
![image](https://github.com/zhouzhichao2017080429/simple_command/assets/73045175/e9eb9775-369c-457e-ae10-f9f22d2f50b5)



Simple_command.java
```
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


```

listen_player.java
```
package zzc.simple_command;

import java.util.*;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;



public class listen_player implements Listener {

    Timer timer = new Timer();
    Player player;
    CommandSender sender;
    String judge_num_str;

    public String uniform_sentence(String a){
        a = a.replace("[", "").replace("]", "");
        return a;
    }

    public String uniform_sentence(String a, String x, String y, String z){
        a = a.replace("xyz", x+" "+y+" "+z);
        return a;
    }

    public String uniform_sentence(String a, String judge_num_str){
        a = a.replace("[", "").replace("]", "");
        if (a.contains("xx")) {a = a.replace("xx", player.getName());}
        if (a.contains("yy")) {a = a.replace("yy", judge_num_str);}
        return a;
    }

    @EventHandler
    public void playchat(AsyncPlayerChatEvent event){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
//                System.out.println("§6public void run()");
                player = event.getPlayer();
                sender = Bukkit.getConsoleSender();
                String x = player.getLocation().getBlockX()+"";
                String y = player.getLocation().getBlockY()+"";
                String z = player.getLocation().getBlockZ()+"";
                String my_message = event.getMessage();
                double money = 0;
                double points = 0;
                ConfigurationSection _advanced_command = Simple_command.config.createSection("new_section");;
                ConfigurationSection points_command = Simple_command.config.getConfigurationSection("points_command");
                ConfigurationSection money_command = Simple_command.config.getConfigurationSection("money_command");
//                Simple_command.log.info("§6my_message: "+my_message);
                //普通咒语执行
                if(Simple_command.speak_command_map.containsKey(my_message)) {
                    player.performCommand(Simple_command.speak_command_map.get(my_message));
                }

                //高级咒语执行
                if(Simple_command.vaultPlugin != null && Simple_command.vaultPlugin.isEnabled() && Simple_command.money_command_map.containsKey(my_message)) {
                    money = Simple_command.econ.getBalance(player);
                    _advanced_command = money_command.getConfigurationSection(Simple_command.money_command_map.get(my_message));
                    Simple_command.log.info("§6触发金钱咒语");
                }
//                Simple_command.log.info(String.valueOf(Simple_command.playpointsPlugin != null));
//                Simple_command.log.info(String.valueOf(Simple_command.playpointsPlugin.isEnabled()));
//                Simple_command.log.info(String.valueOf(Simple_command.playpoint_command_map.containsKey(my_message)));

                if(Simple_command.playpointsPlugin != null && Simple_command.playpointsPlugin.isEnabled() && Simple_command.playpoint_command_map.containsKey(my_message)) {
                    money = Simple_command.ppAPI.look(player.getUniqueId());
                    _advanced_command = points_command.getConfigurationSection(Simple_command.playpoint_command_map.get(my_message));
                    Simple_command.log.info("§6触发点卷咒语");
                }
                if((Simple_command.money_command_map.containsKey(my_message) && Simple_command.vaultPlugin != null) || (Simple_command.playpoint_command_map.containsKey(my_message) && Simple_command.playpointsPlugin != null) ) {


                    judge_num_str = _advanced_command.getString("judge");
                    Simple_command.log.info("judge_num_str: " + judge_num_str);
                    judge_num_str = uniform_sentence(judge_num_str);

                    String p_str = _advanced_command.getString("positive");
                    String n_str = _advanced_command.getString("negetive");
                    p_str = uniform_sentence(p_str,judge_num_str);
                    n_str = uniform_sentence(n_str,judge_num_str);


                    double judge_num = Double.parseDouble(judge_num_str);
                    if(money>=judge_num){
                        String _command = _advanced_command.getString("command");
                        String[] _command_split = _command.split(";");
                        for (int i = 0; i < _command_split.length; i++){
                            String command_i = _command_split[i];
                            command_i = uniform_sentence(command_i,judge_num_str);
                            command_i = uniform_sentence(command_i,x,y,z);
                            sender.getServer().dispatchCommand(sender, command_i);
                        }

                        player.sendMessage(p_str);

                        // 判断是否存在子关键字 "broadcast"
                        boolean hasBroadcastKey = _advanced_command.contains("broadcast");
                        // 输出判断结果
                        if (hasBroadcastKey) {
                            String broadcast_str = _advanced_command.getString("broadcast");
                            broadcast_str = uniform_sentence(broadcast_str,judge_num_str);
                            sender.getServer().broadcastMessage(broadcast_str);
                        }

                    }else{player.sendMessage(n_str); }
                }

                //个人传送
                if(my_message.startsWith("tpa")&&my_message.length()>=5){
                    player.performCommand("tpa "+my_message.substring(4).trim());
                    player.sendMessage("已为您执行: "+"tpa "+my_message.substring(4).trim());
                    return;
                }else if(my_message.startsWith("tp")&&my_message.length()>=4){
                    player.performCommand("tpa "+my_message.substring(3).trim());
                    player.sendMessage("已为您执行: "+"tpa "+my_message.substring(3).trim());
                    return;
                }

                //领地传送
                if(my_message.startsWith("res tp")&&my_message.length()>=8){
                    player.performCommand("res tp "+my_message.substring(7).trim());
                    player.sendMessage("§6您已执行: "+"res tp "+my_message.substring(7).trim());
                    return;
                }

            }
        };

        timer.schedule(timerTask, 1000);

    }

}


```
