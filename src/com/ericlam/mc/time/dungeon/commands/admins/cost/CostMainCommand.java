package com.ericlam.mc.time.dungeon.commands.admins.cost;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class CostMainCommand extends SubCommand {
    public CostMainCommand(Plugin plugin) {
        super(plugin);
        this.addSubCommand(new CostClearCommand());
        this.addSubCommand(new CostItemCommand());
        this.addSubCommand(new CostLevelCommand());
        this.addSubCommand(new CostMoneyCommand());
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
        return TimeDungeon.getMessage("help.cost.main");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "cost";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(getSubHelpMessages());
    }
}
