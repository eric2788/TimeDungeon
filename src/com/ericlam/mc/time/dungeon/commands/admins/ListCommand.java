package com.ericlam.mc.time.dungeon.commands.admins;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.StorageManager;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ListCommand extends SubCommand {
    public ListCommand(Plugin plugin) {
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
        return TimeDungeon.getMessage("help.list");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String[] list = StorageManager.getInstance().listDungeons();
        commandSender.sendMessage(TimeDungeon.getMessage("list").replace("<all>", Arrays.toString(list)));
    }
}
