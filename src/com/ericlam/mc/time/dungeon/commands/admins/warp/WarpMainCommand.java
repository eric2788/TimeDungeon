package com.ericlam.mc.time.dungeon.commands.admins.warp;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class WarpMainCommand extends SubCommand {

    public WarpMainCommand(Plugin plugin) {
        super(plugin);
        this.addSubCommand(new WarpSpawnCommand());
        this.addSubCommand(new WarpWaitCommand());
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
        return TimeDungeon.getMessage("help.warp.main");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(getHelpMessages());
    }
}
