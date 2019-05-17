package com.ericlam.mc.time.dungeon.commands;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(Plugin plugin) {
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
        return TimeDungeon.getMessage("help.reload");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        getPlugin().reloadConfig();
        commandSender.sendMessage(TimeDungeon.getMessage("setup.success"));
    }
}
