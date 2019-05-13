package com.ericlam.mc.time.dungeon;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Dungeon {
    private int seconds;
    private String name;
    private double price;
    private int level;
    private List<ItemStack> items;
    private Location spawn;
    private Location wait;
    private boolean canJoinInGame;
    private int maxPlayers;

    public Dungeon(int seconds, String name, double price, int level, List<ItemStack> items, Location spawn, Location wait,boolean canJoinInGame,int maxPlayers) {
        this.seconds = seconds;
        this.name = name;
        this.price = price;
        this.level = level;
        this.items = items;
        this.spawn = spawn;
        this.wait = wait;
        this.canJoinInGame = canJoinInGame;
        this.maxPlayers = maxPlayers;
    }

    public Dungeon(){
        this.items = new ArrayList<>();
        this.level = 0;
        this.seconds = 0;
        this.price = 0.0;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isCanJoinInGame() {
        return canJoinInGame;
    }

    public void setCanJoinInGame(boolean canJoinInGame) {
        this.canJoinInGame = canJoinInGame;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public Location getWait() {
        return wait;
    }

    public void setWait(Location wait) {
        this.wait = wait;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void clearItems() {
        this.items.clear();
    }

    public void addItem(ItemStack... items){
        this.items.addAll(Arrays.asList(items));
    }

    public void removeItem(ItemStack... item){
        this.items.removeAll(Arrays.asList(item));
    }

    public boolean isDone(){
        return this.spawn != null && this.seconds > 0;
    }

    public List<String> toInfo(){
        String spawn = this.spawn == null ? "null" : "["+"("+this.spawn.getWorld().getName()+")"+Math.round(this.spawn.getX())+","+Math.round(this.spawn.getY())+","+Math.round(this.spawn.getZ())+"]";
        String seconds = this.seconds+"";
        String price = this.price+"";
        String level = this.level+"";
        String wait_spawn = this.wait == null ? "null" : "["+"("+this.wait.getWorld().getName()+")"+Math.round(this.wait.getX())+","+Math.round(this.wait.getY())+","+Math.round(this.wait.getZ())+"]";
        String canjoinInGame = this.canJoinInGame ? "yes" : "no";
        String maxPlayer = this.maxPlayers+"";
        return TimeDungeon.getStringList("info").stream().map(l->l
                .replace("<spawn>",spawn)
                .replace("<second>",seconds)
                .replace("<wait-spawn>",wait_spawn)
                .replace("<money>",price)
                .replace("<level>",level)
                .replace("<in-game>",canjoinInGame)
                .replace("<max>",maxPlayer)
                .replace("<name>",this.name)).collect(Collectors.toList());
    }
}
