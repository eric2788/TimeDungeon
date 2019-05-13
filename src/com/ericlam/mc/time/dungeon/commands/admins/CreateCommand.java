package com.ericlam.mc.time.dungeon.commands.admins;

import com.ericlam.mc.time.dungeon.exceptions.DungeonNonExistException;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.ericlam.mc.time.dungeon.managers.StorageManager;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class CreateCommand extends SubCommand {
    public CreateCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getParentCommand() {
        return "td";
    }

    @Override
    public int getArgs() {
        return 1;
    }

    @Override
    public String getHelpMessages() {
        return TimeDungeon.getMessage("help.create");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String id = strings[0];
        try {
            StorageManager.getInstance().findDungeonLocal(id);
            commandSender.sendMessage(new String[]{TimeDungeon.getMessage("setup.id-exist"), TimeDungeon.getMessage("setup.failed")});
            return;
        } catch (DungeonNonExistException ignored) {
        }
        ArenaManager.getInstance().createDungeon(id);
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
