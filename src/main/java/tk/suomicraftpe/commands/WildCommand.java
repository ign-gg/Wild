package tk.suomicraftpe.commands;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import cn.nukkit.Player;

public class WildCommand extends PluginBase {

    private Config c;

    private final Set<Long> tpQueue = ConcurrentHashMap.newKeySet();

    public void onEnable() {
        saveDefaultConfig();
        c = getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("wild")) {
            if (sender instanceof Player) {
                String world;
                boolean byName;

                if (args.length > 0) {
                    world = args[0];
                    byName = true;
                } else {
                    world = ((Player) sender).getLevel().getName();
                    byName = false;
                }

                if (c.getList("allowedWorlds").contains(world)) {
                    wild((Player) sender, world, byName);
                } else {
                    sender.sendMessage(c.getString("messageUnallowed"));
                }
            } else {
                sender.sendMessage(c.getString("messageInGame"));
            }
        }

        return true;
    }

    private static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        
        return min + ThreadLocalRandom.current().nextInt(max - min);
    }

    private void wild(Player p, String world, boolean byName) {
        if (tpQueue.contains(p.getId())) {
            return;
        }

        Level level = getServer().getLevelByName(world);
        if (level == null) {
            p.sendMessage(c.getString("messageError"));
            return;
        }

        tpQueue.add(p.getId());

        level.threadedExecutor.execute(() -> {
            Position spawn = level.getSafeSpawn(new Position(rand(
                    byName ? c.getInt("BminX") : c.getInt("minX"),
                    byName ? c.getInt("BmaxX") : c.getInt("maxX")),
                    128, rand(
                    byName ? c.getInt("BminZ") : c.getInt("minZ"),
                    byName ? c.getInt("BmaxZ") : c.getInt("maxZ")),
                    level));

            p.level.threadedExecutor.execute(() -> {
                tpQueue.remove(p.getId());

                if (p.teleport(spawn)) {
                    p.sendMessage(c.getString("messageSuccess"));
                } else {
                    p.sendMessage(c.getString("messageError"));
                }
            });
        });
    }
}
