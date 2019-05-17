package com.ericlam.mc.time.dungeon.managers;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.exceptions.DungeonNonExistException;
import com.ericlam.mc.time.dungeon.main.AdvMessageBuilder;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.nms.DungeonNMS;
import com.ericlam.mc.time.dungeon.runners.DungeonRunner;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonManager {
    private static DungeonManager dungeonManager;

    public static DungeonManager getInstance() {
        if (dungeonManager == null) dungeonManager = new DungeonManager();
        return dungeonManager;
    }


    private Map<String, DungeonRunner> runnerMap = new HashMap<>();


    public void joinGame(Player player,String id){
        try {
            Dungeon dungeon = StorageManager.getInstance().findDungeonLocal(id);
            runnerMap.putIfAbsent(id,new DungeonRunner(id,dungeon,new LinkedList<>()));
            if (!checkCondition(dungeon,player)){
                String[] arr = TimeDungeon.getStringList("error.not-pass").stream().map(e->e
                        .replace("<money>",dungeon.getPrice()+"")
                        .replace("<level>",dungeon.getLevel()+"")).toArray(String[]::new);
                List<ItemStack> items = dungeon.getItems();
                AdvMessageBuilder adv = new AdvMessageBuilder("[");
                for (int i = 0; i < items.size(); i++) {
                    ItemStack item = items.get(i);
                    BaseComponent[] baseComponents = new BaseComponent[]{
                            new TextComponent(DungeonNMS.convertItemStackToJson(item))
                    };
                    adv.addHover("&e&n&oItem" + (i + 1) + "&r", HoverEvent.Action.SHOW_ITEM, baseComponents);
                    if (i != items.size()-1) adv.addMessage(", ");
                }
                adv.addMessage("]");
                TextComponent component = adv.build();
                player.sendMessage(arr);
                player.spigot().sendMessage(component);
                return;
            }
            runnerMap.get(id).join(player);
            runnerMap.get(id).startCounting();
        } catch (DungeonNonExistException e) {
            player.sendMessage(TimeDungeon.getMessage("error.no-exist-dun"));
        }
    }

    public void leaveGame(Player player,String id){
        if (!runnerMap.containsKey(id)){
            player.sendMessage(TimeDungeon.getMessage("error.not-in-game").replace("<id>",id));
            return;
        }
        runnerMap.get(id).leave(player);
    }

    public void handleLeave(Player player){
        runnerMap.values().forEach(r->r.handleLeave(player));
    }

    public void handleDeath(Player player){
        runnerMap.values().forEach(r->r.handleDeath(player));
    }

    public void handleDamage(EntityDamageEvent e){
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player)entity;
        boolean shouldDamage = true;
        for (DungeonRunner runner : runnerMap.values()) {
            shouldDamage = shouldDamage && runner.shouldDamage(player);
        }
        e.setCancelled(!shouldDamage);
    }

    public void remove(DungeonRunner runner){
        runnerMap.remove(runner.getId());
    }


    private boolean checkCondition(Dungeon dungeon, Player player){
        double money = dungeon.getPrice();
        int level = dungeon.getLevel();
        List<ItemStack> items = dungeon.getItems();
        boolean levelPass = player.getLevel() >= level;
        PlayerInventory inv = player.getInventory();
        boolean contains = true;
        for (ItemStack item : items) {
            contains = contains && inv.contains(item);
        }
        Economy eco = TimeDungeon.getVaultEconomy().getEconomy();
        boolean moneyPass = eco.withdrawPlayer(player,money).type == EconomyResponse.ResponseType.SUCCESS;

        if (levelPass && moneyPass && contains){
            player.setLevel(player.getLevel() - level);
            Map<Integer,ItemStack> map = inv.removeItem(items.toArray(new ItemStack[0]));
            if (map.size() > 0){
                TimeDungeon.warn("我們無法完全刪除玩家 "+player.getName()+" 需要消費的物品。");
                TimeDungeon.warn("剩餘物品有: "+map.values().stream().map(l->l.getType().toString()).collect(Collectors.joining()));
            }
            return true;

        }else return false;
    }

    /*
        PlaceholderAPI Request
     */

    public String requestTime(String id){
        if (!runnerMap.containsKey(id)){
            return TimeDungeon.getMessage("placeholder.not-started");
        }
        DungeonRunner runner = runnerMap.get(id);
        return runner.getGamestats() == DungeonRunner.State.STARTING ? runner.gameTime+"秒" : TimeDungeon.getMessage("placeholder.not-started");
    }

    public String requestStats(String id){
        if (!runnerMap.containsKey(id)){
            return TimeDungeon.getMessage("placeholder.not-started");
        }
        DungeonRunner runner = runnerMap.get(id);
        return TimeDungeon.getMessage("placeholder.stats."+runner.getGamestats().toString());
    }

    public String requestName(String id){
        try {
            return StorageManager.getInstance().findDungeonLocal(id).getName();
        } catch (DungeonNonExistException e) {
            return TimeDungeon.getMessage("placeholder.non-exist");
        }
    }

}
