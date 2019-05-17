package com.ericlam.mc.time.dungeon.commands.admins;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SetSpawnCommand extends SubCommand {
    public SetSpawnCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getParentCommand() {
        return "td";
    }

    @Override
    public int getArgs() {
        return 0;
    }

    @Override
    public String getHelpMessages() {
        return TimeDungeon.getMessage("help.setspawn");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TimeDungeon.getMessage("error.not-player"));
            return;
        }
        Player player = (Player) commandSender;
        Location location = player.getLocation().clone();
        FileConfiguration config = TimeDungeon.getMyConfig();
        Map<String, Object> spawn = new LinkedHashMap<>();
        spawn.put("world", location.getWorld().getName());
        spawn.put("x", location.getX());
        spawn.put("y", location.getY());
        spawn.put("z", location.getZ());
        spawn.put("pitch", location.getPitch());
        spawn.put("yaw", location.getYaw());
        config.createSection("spawn", spawn);
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
