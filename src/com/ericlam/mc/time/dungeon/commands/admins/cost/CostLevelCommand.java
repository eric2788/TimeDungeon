package com.ericlam.mc.time.dungeon.commands.admins.cost;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.hypernite.mc.api.commands.SubSubCommand;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public class CostLevelCommand extends SubSubCommand {
    @Override
    public String getParentCommand() {
        return "cost";
    }

    @Override
    public int getArgs() {
        return 2;
    }

    @Override
    public String getHelpMessages() {
        return TimeDungeon.getMessage("help.cost.level");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String id = strings[0];
        int level;
        try{
            level = Integer.parseInt(strings[1]);
        }catch (NumberFormatException e){
            commandSender.sendMessage(TimeDungeon.getMessage("error.not-number"));
            return;
        }
        Dungeon dungeon = ArenaManager.getInstance().findOrCreateDun(id);
        dungeon.setLevel(level);
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
