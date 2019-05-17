package com.ericlam.mc.time.dungeon.runners;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.listeners.SignListeners;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.DungeonManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class DungeonRunner {
    private BukkitTask task;
    private Dungeon dungeon;
    private List<Player> players;
    private boolean canJoin, canJoinInGame;
    private final TimeDungeon timeDungeon;
    private State gamestats;
    private int waitTime,endTime;
    public int gameTime;
    private String id;

    public DungeonRunner(String id,Dungeon dungeon,List<Player> players) {
        this.id = id;
        this.dungeon = dungeon;
        this.players = players;
        this.gameTime = dungeon.getSeconds();
        this.waitTime = TimeDungeon.getWaitSecs();
        this.endTime = TimeDungeon.getEndSecs();
        this.canJoin = true;
        setGameState(State.NONE);
        this.canJoinInGame = dungeon.isCanJoinInGame();
        this.timeDungeon = TimeDungeon.getPlugin();
    }

    public String getId() {
        return id;
    }

    public enum State{
        NONE, COUNTING, STARTING, GAMEEND
    }

    private void sendActionBar(String msg){
        players.forEach(p->p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg)));
    }

    public void handleDeath(Player player){
        if (gamestats != State.STARTING) return;
        if (!players.removeIf(p->p.equals(player))) return;
        player.teleport(TimeDungeon.getSpawn() == null ? player.getLocation() : TimeDungeon.getSpawn());
        if (TimeDungeon.getSpawn() == null) player.sendMessage(TimeDungeon.getMessage("error.no-spawn"));
        player.sendTitle(TimeDungeon.getPureMessage("title.death"),"",20,40,20);
        players.forEach(p->p.sendMessage(TimeDungeon.getMessage("games.death").replace("<player>",player.getName())));
        SignListeners.updateSign(id, this.gamestats, players.size());
    }

    public boolean shouldDamage(Player player){
        if (!players.contains(player)) return true;
        return gamestats == State.STARTING;
    }


    public void join(Player player){
        if (gamestats == State.GAMEEND){
            player.sendMessage(TimeDungeon.getMessage("error.game-over"));
            return;
        }
        if (!canJoin && !canJoinInGame) {
            player.sendMessage(TimeDungeon.getMessage("games.started"));
            return;
        }
        if (players.size() >= dungeon.getMaxPlayers()){
            player.sendMessage(TimeDungeon.getMessage("games.fulled"));
            return;
        }
        if (players.contains(player)){
            player.sendMessage(TimeDungeon.getMessage("error.already-in-game"));
            return;
        }
        players.add(player);
        player.teleport(gamestats == State.STARTING ? dungeon.getSpawn() : dungeon.getWait());
        player.sendTitle(TimeDungeon.getPureMessage("title.joined"),"",20,40,20);
        players.forEach(p->p.sendMessage(TimeDungeon.getMessage("players.join").replace("<player>",player.getName())));
        SignListeners.updateSign(id, this.gamestats, players.size());
    }

    public boolean isStarted() {
        return this.gamestats == State.STARTING;
    }

    public State getGamestats() {
        return gamestats;
    }

    public void handleLeave(Player player){
        if (players.removeIf(p -> p.equals(player))) {
            players.forEach(p -> p.sendMessage(TimeDungeon.getMessage("player.leave").replace("<player>", player.getName())));
            SignListeners.updateSign(id, this.gamestats, players.size());
        }
    }

    public void leave(Player player){
        if (!players.removeIf(p->p.equals(player))){
            player.sendMessage(TimeDungeon.getMessage("error.not-in-game").replace("<id>",id));
            return;
        }
        player.teleport(TimeDungeon.getSpawn() == null ? player.getLocation() : TimeDungeon.getSpawn());
        if (TimeDungeon.getSpawn() == null) player.sendMessage(TimeDungeon.getMessage("error.no-spawn"));
        players.forEach(p->p.sendMessage(TimeDungeon.getMessage("player.leave").replace("<player>",player.getName())));
        player.sendTitle(TimeDungeon.getPureMessage("title.left"),"",20,40,20);
        SignListeners.updateSign(id, this.gamestats, players.size());
    }

    private void setGameState(State state) {
        this.gamestats = state;
        SignListeners.updateSign(id, state, players.size());
    }

    public void startCounting(){
        if (task != null) return;
        setGameState(State.COUNTING);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (waitTime > 0){
                    sendActionBar(TimeDungeon.getPureMessage("actionbar.wait").replace("<sec>",waitTime+""));

                    if (waitTime%60==0){
                        players.forEach(p->p.sendMessage(TimeDungeon.getMessage("time.count").replace("<sec>",waitTime+"")));
                        Bukkit.broadcastMessage(TimeDungeon.getMessage("invite.counting")
                                .replace("<secs>", waitTime + "")
                                .replace("<id>", id));
                    } else if (waitTime % 30 == 0) {
                        players.forEach(p -> p.sendMessage(TimeDungeon.getMessage("time.count").replace("<sec>", waitTime + "")));
                    } else if (waitTime < 11) {
                        players.forEach(p -> p.sendMessage(TimeDungeon.getMessage("time.count").replace("<sec>", waitTime + "")));
                    }

                    if (players.size() == 0){
                        cancel();
                        preEnding();
                    }

                    waitTime--;
                }else{
                    cancel();
                    startGameing();
                }
            }
        }.runTaskTimer(timeDungeon,0L,20L);
    }

    private void startGameing(){
        setGameState(State.STARTING);
        players.forEach(p->{
            p.teleport(dungeon.getSpawn());
            p.sendMessage(TimeDungeon.getMessage("game.start"));
            p.sendTitle(TimeDungeon.getPureMessage("title.started"),"",20,40,20);
        });
        this.canJoin = false;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameTime > 0){
                    sendActionBar(TimeDungeon.getPureMessage("actionbar.game").replace("<sec>",gameTime+""));

                    if (gameTime%60==0){
                        players.forEach(p->p.sendMessage(TimeDungeon.getMessage("time.start").replace("<sec>",gameTime+"")));
                    } else if (gameTime % 30 == 0) {
                        players.forEach(p -> p.sendMessage(TimeDungeon.getMessage("time.start").replace("<sec>", gameTime + "")));
                    } else if (gameTime < 11) {
                        players.forEach(p -> p.sendMessage(TimeDungeon.getMessage("time.start").replace("<sec>", gameTime + "")));
                    }

                    if (players.size() == 0){
                        cancel();
                        preEnding();
                    }
                    gameTime--;
                }else{
                    cancel();
                    preEnding();
                }
            }
        }.runTaskTimer(timeDungeon,0L,20L);
    }

    private void preEnding(){
        setGameState(State.GAMEEND);
        players.forEach(p->{
            p.sendTitle(TimeDungeon.getPureMessage("title.end"),"",20,40,20);
            p.sendMessage(TimeDungeon.getMessage("games.end"));
        });
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (endTime > 0){
                    sendActionBar(TimeDungeon.getPureMessage("actionbar.end").replace("<sec>",endTime+""));
                    if (endTime%5 == 0){
                        sendFireWork();
                    }
                    endTime--;
                }else{
                    cancel();
                    end();
                }
            }
        }.runTaskTimer(timeDungeon,0L,20L);
    }

    private void end(){
        players.forEach(player -> {
            player.teleport(TimeDungeon.getSpawn() == null ? player.getLocation() : TimeDungeon.getSpawn());
            if (TimeDungeon.getSpawn() == null) player.sendMessage(TimeDungeon.getMessage("error.no-spawn"));
        });
        DungeonManager.getInstance().remove(this);
    }

    private void sendFireWork(){
        players.forEach(p->{
            Firework firework = (Firework) p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
            firework.detonate();
        });
    }
}
