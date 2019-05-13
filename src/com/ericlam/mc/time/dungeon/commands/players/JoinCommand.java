package com.ericlam.mc.time.dungeon.commands.players;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.DungeonManager;
import com.hypernite.mc.api.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class JoinCommand extends SubCommand {
    public JoinCommand(Plugin plugin) {
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
        return TimeDungeon.getMessage("help.join");
    }

    @Nullable
    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getName() {
        return "join";
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
        String id = strings[0];
        Player player = (Player) commandSender;
        DungeonManager.getInstance().joinGame(player,id);
    }
}
