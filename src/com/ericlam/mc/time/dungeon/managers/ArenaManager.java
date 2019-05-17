package com.ericlam.mc.time.dungeon.managers;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.exceptions.DungeonSetUpNotFinishException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {
    private static ArenaManager arenaManager;

    public static ArenaManager getInstance() {
        if (arenaManager == null) arenaManager = new ArenaManager();
        return arenaManager;
    }
    
    private File dungeonFolder;

    private ArenaManager() {
        this.dungeonFolder = StorageManager.getInstance().getDungeonFolder();
    }

    private Map<String, Dungeon> preset = new HashMap<>();

    public Optional<Dungeon> getPreSet(String id) {
        return Optional.ofNullable(preset.get(id));
    }

    public Set<String> getPreSets() {
        return preset.keySet();
    }
    
    
    private Dungeon getFromFile(FileConfiguration yml){
        return StorageManager.getInstance().getFromFile(yml);
    }

    public Dungeon findOrCreateDun(String id){
        return Optional.ofNullable(preset.get(id)).orElseGet(()->{
            File file = new File(dungeonFolder,id+".yml");
            if (!file.exists()) {
                Dungeon dungeon = new Dungeon();
                preset.put(id,dungeon);
                return dungeon;
            }else{
                FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                Dungeon dungeon = getFromFile(yml);
                if (dungeon == null){
                    dungeon = new Dungeon();
                }
                preset.put(id,dungeon);
                return dungeon;
            }
        });
    }

    public void createDungeon(String id){
        preset.putIfAbsent(id,new Dungeon());
    }

    public boolean saveDungeon(String id) throws DungeonSetUpNotFinishException {
        if (!preset.containsKey(id)) return false;
        Dungeon dungeon = preset.get(id);
        if (!dungeon.isDone()) throw new DungeonSetUpNotFinishException(id);
        File file = new File(dungeonFolder, id+".yml");
        FileConfiguration yml = new YamlConfiguration();
        yml.set("name",dungeon.getName());
        yml.set("price",dungeon.getPrice());
        yml.set("level",dungeon.getLevel());
        yml.set("can-join-in-game",dungeon.isCanJoinInGame());
        yml.set("times",dungeon.getSeconds());
        yml.set("max-players",dungeon.getMaxPlayers());
        for (int i = 0; i < dungeon.getItems().size(); i++) {
            yml.set("items."+i,dungeon.getItems().get(i));
        }
        Location location = dungeon.getSpawn();
        LinkedHashMap<String,Object> locMap = new LinkedHashMap<>();
        locMap.put("world",location.getWorld().getName());
        locMap.put("x",location.getX());
        locMap.put("y",location.getY());
        locMap.put("z",location.getZ());
        locMap.put("pitch",location.getPitch());
        locMap.put("yaw",location.getYaw());
        yml.createSection("location",locMap);
        Location wlocation = dungeon.getWait();
        LinkedHashMap<String,Object> wlocMap = new LinkedHashMap<>();
        wlocMap.put("world",wlocation.getWorld().getName());
        wlocMap.put("x",wlocation.getX());
        wlocMap.put("y",wlocation.getY());
        wlocMap.put("z",wlocation.getZ());
        wlocMap.put("pitch",wlocation.getPitch());
        wlocMap.put("yaw",wlocation.getYaw());
        yml.createSection("wait-location",wlocMap);
        try {
            yml.save(file);
            preset.remove(id);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
