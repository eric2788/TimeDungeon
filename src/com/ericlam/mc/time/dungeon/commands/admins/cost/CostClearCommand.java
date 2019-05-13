package com.ericlam.mc.time.dungeon.commands.admins.cost;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.hypernite.mc.api.commands.SubSubCommand;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public class CostClearCommand extends SubSubCommand {
    @Override
    public String getParentCommand() {
        return "cost";
    }

    @Override
    public int getArgs() {
        return 1;
    }

    @Override
    public String getHelpMessages() {
        return TimeDungeon.getMessage("help.cost.clear");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String id = strings[0];
        Dungeon dungeon = ArenaManager.getInstance().findOrCreateDun(id);
        dungeon.setPrice(0.0);
        dungeon.setLevel(0);
        dungeon.clearItems();
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
