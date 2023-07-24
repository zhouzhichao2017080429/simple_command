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
