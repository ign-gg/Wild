package tk.suomicraftpe.commands;

import java.util.concurrent.ThreadLocalRandom;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import cn.nukkit.Player;

public class WildCommand extends PluginBase {

    Config c;

    public void onEnable() {
        saveDefaultConfig();
        c = getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("wild")) {
            if (sender instanceof Player) {
                if (c.getList("allowedWorlds").contains(((Player) sender).getLevel().getName())) {
                    wild((Player) sender);
                } else {
                    sender.sendMessage(c.getString("messageUnallowed"));
                }
            } else {
                sender.sendMessage(c.getString("messageInGame"));
            }
        }

        return true;
    }

    private int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        
        return min + ThreadLocalRandom.current().nextInt(max - min);
    }

    private void wild(Player p) {
        if (p.teleport(p.getLevel().getSafeSpawn(new Vector3(rand(c.getInt("minX"), c.getInt("maxX")), 70, rand(c.getInt("minZ"), c.getInt("maxZ")))))) {
            p.sendMessage(c.getString("messageSuccess"));
        } else {
            p.sendMessage(c.getString("messageError"));
        }
    }
}
