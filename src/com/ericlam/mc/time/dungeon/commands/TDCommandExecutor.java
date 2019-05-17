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

import java.util.Arrays;
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
            commandSender.sendMessage(TimeDungeon.getMessage("command.arg-too-short").replace("<command>",e.getMessage()));
        } catch (CommandNotFoundException e) {
            commandSender.sendMessage(TimeDungeon.getMessage("command.not-found").replace("<command>",e.getMessage()));
        } catch (NoPermissionException e) {
            commandSender.sendMessage(TimeDungeon.getMessage("command.no-permission"));
            commandSender.sendMessage(Arrays.stream(e.getPermissions()).map(TimeDungeon::getMessage).toArray(String[]::new));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return CommandHandle.tapComplete(commandSender,command,strings);
    }
}
