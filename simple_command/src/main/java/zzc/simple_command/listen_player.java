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
