package com.ericlam.mc.time.dungeon.listeners;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.DungeonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        DungeonManager.getInstance().handleLeave(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        Bukkit.getScheduler().runTask(TimeDungeon.getPlugin(), () -> DungeonManager.getInstance().handleDeath(player));
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        DungeonManager.getInstance().handleDamage(e);
    }
}
