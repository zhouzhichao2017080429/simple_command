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
