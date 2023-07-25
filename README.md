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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class Simple_command extends JavaPlugin {

    public static FileConfiguration config;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<String, String> speak_command_map = new HashMap<String,String>();
    

    @Override
    public void onEnable() {
        System.out.println("§6指令简化插件启动中...");
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
                    speak_command_map.put(commandName,command_content);

                }
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class listen_player implements Listener {

    Timer timer = new Timer();
    Player player;
    CommandSender sender;

    @EventHandler
    public void playchat(AsyncPlayerChatEvent event){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                player = event.getPlayer();
                sender = Bukkit.getConsoleSender();
                String my_message = event.getMessage();

                //普通咒语执行
                if(Simple_command.speak_command_map.containsKey(my_message)) {
                    player.performCommand(Simple_command.speak_command_map.get(my_message));
                }

            }
        };

        timer.schedule(timerTask, 1000);

    }

}


```
