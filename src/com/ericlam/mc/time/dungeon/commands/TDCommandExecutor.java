package com.ericlam.mc.time.dungeon.commands;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.hypernite.mc.api.commands.CommandHandle;
import com.hypernite.mc.api.commands.exception.ArgTooShortException;
import com.hypernite.mc.api.commands.exception.CommandNotFoundException;
import com.hypernite.mc.api.commands.exception.NoPermissionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class TDCommandExecutor implements CommandExecutor, TabCompleter {

    private TimeDungeon plugin;

    public TDCommandExecutor(TimeDungeon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            return CommandHandle.handle(commandSender,command,strings,plugin);
        } catch (ArgTooShortException e) {
            e.printStackTrace();
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        } catch (NoPermissionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return CommandHandle.tapComplete(commandSender,command,strings);
    }
}
