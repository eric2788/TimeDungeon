package com.ericlam.mc.time.dungeon.commands.admins;

import com.ericlam.mc.time.dungeon.exceptions.DungeonSetUpNotFinishException;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class SaveCommand extends SubCommand {

    public SaveCommand(Plugin plugin) {
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
        return TimeDungeon.getMessage("help.save");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String id = strings[0];
        try {
            boolean save = ArenaManager.getInstance().saveDungeon(id);
            commandSender.sendMessage(TimeDungeon.getMessage("setup."+(save ? "success" : "failed")));
        } catch (DungeonSetUpNotFinishException e) {
            TimeDungeon.warn(e.getMessage());
            commandSender.sendMessage(new String[]{TimeDungeon.getMessage("setup.not-finished"), TimeDungeon.getMessage("setup.failed")});
        }
    }
}
