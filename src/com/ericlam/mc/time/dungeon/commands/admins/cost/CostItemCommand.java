package com.ericlam.mc.time.dungeon.commands.admins.cost;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.ArenaManager;
import com.hypernite.mc.api.commands.SubSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class CostItemCommand extends SubSubCommand {
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
        return TimeDungeon.getMessage("help.cost.item");
    }

    @Nullable
    @Override
    public String getPermission() {
        return "td.admin";
    }

    @Override
    public String getName() {
        return "item";
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
        Player player = (Player) commandSender;
        ItemStack item = player.getInventory().getItemInMainHand();
        String id = strings[0];
        Dungeon dungeon = ArenaManager.getInstance().findOrCreateDun(id);
        if (!dungeon.getItems().contains(item)) {
            dungeon.addItem(item);
            commandSender.sendMessage(TimeDungeon.getMessage("setup.item.added"));
        }else{
            dungeon.removeItem(item);
            commandSender.sendMessage(TimeDungeon.getMessage("setup.item.removed"));
        }
    }
}
