package com.ericlam.mc.time.dungeon.commands.admins;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class MaxPlayerCommand extends SubCommand {
    public MaxPlayerCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getParentCommand() {
        return "td";
    }

    @Override
    public int getArgs() {
        return 2;
    }

    @Override
    public String getHelpMessages() {
        return TimeDungeon.getMessage("help.maxplayer");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "maxplayer";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String id = strings[0];
        int max_player;
        try{
            max_player = Integer.parseInt(strings[1]);
        }catch (NumberFormatException e){
            commandSender.sendMessage(TimeDungeon.getMessage("error.not-number"));
            return;
        }
        Dungeon dungeon = ArenaManager.getInstance().findOrCreateDun(id);
        dungeon.setMaxPlayers(max_player);
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
