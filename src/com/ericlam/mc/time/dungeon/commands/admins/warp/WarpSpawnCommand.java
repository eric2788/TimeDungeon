package com.ericlam.mc.time.dungeon.commands.admins.warp;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.hypernite.mc.api.commands.SubSubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class WarpSpawnCommand extends SubSubCommand {
    @Override
    public String getParentCommand() {
        return "warp";
    }

    @Override
    public int getArgs() {
        return 1;
    }

    @Override
    public String getHelpMessages() {
        return TimeDungeon.getMessage("help.warp.spawn");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(TimeDungeon.getMessage("error.not-player"));
            return;
        }
        Player player = (Player) commandSender;
        Location location = player.getLocation().clone();
        String id = strings[0];
        Dungeon dungeon = ArenaManager.getInstance().findOrCreateDun(id);
        dungeon.setSpawn(location);
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
