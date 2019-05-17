package com.ericlam.mc.time.dungeon.managers;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.exceptions.DungeonNonExistException;
import com.ericlam.mc.time.dungeon.main.AdvMessageBuilder;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.nms.DungeonNMS;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StorageManager {
    private static StorageManager storageManager;

    public static StorageManager getInstance() {
        if (storageManager == null) storageManager = new StorageManager();
        return storageManager;
    }

    private File dungeonFolder;

    private StorageManager() {
        TimeDungeon plugin = TimeDungeon.getPlugin();
        dungeonFolder = new File(plugin.getDataFolder(),"Dungeons");
        if (!dungeonFolder.exists()) dungeonFolder.mkdir();
    }

    public File getDungeonFolder() {
        return dungeonFolder;
    }

    private Map<String, Dungeon> caches = new HashMap<>();

    public Dungeon findDungeonLocal(String id) throws DungeonNonExistException {
        if (!caches.containsKey(id)){
            File file = new File(dungeonFolder,id+".yml");
            if (!file.exists()) throw new DungeonNonExistException(id);
            FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
            Dungeon dungeon = getFromFile(yml);
            if (dungeon == null){
                throw new DungeonNonExistException(id);
            }
            caches.put(id, dungeon);
            return dungeon;
        }else{
            return caches.get(id);
        }
    }

    public String[] listDungeons(){
        File[] files = dungeonFolder.listFiles();
        if (files == null) return new String[0];
        String[] arr = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!FilenameUtils.getExtension(file.getName()).equals("yml")) continue;
            arr[i] = FilenameUtils.getBaseName(file.getName());
        }
        String[] presets = ArenaManager.getInstance().getPreSets().stream().map(l -> l.concat("(設置中)")).toArray(String[]::new);
        return (String[]) ArrayUtils.addAll(arr, presets);
    }

    public void sendPlayerInfo(String id, CommandSender player) throws DungeonNonExistException {
        Dungeon dungeon;
        Optional<Dungeon> dungeonP = ArenaManager.getInstance().getPreSet(id);
        dungeon = dungeonP.isPresent() ? dungeonP.get() : findDungeonLocal(id);
        boolean preset = ArenaManager.getInstance().getPreSets().contains(id);
        String[] arr = dungeon.toInfo().stream().map(e->e.replace("<id>",id)).toArray(String[]::new);
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
        player.sendMessage(arr);
        adv.sendPlayer((Player) player);
        if (preset) player.sendMessage(TimeDungeon.getMessage("setup.not-saved"));
    }

    public boolean removeDungeon(String id) throws DungeonNonExistException {
        caches.remove(id);
        File file = new File(dungeonFolder,id+".yml");
        if (!file.exists()) throw new DungeonNonExistException(id);
        try {
            FileUtils.forceDelete(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    Dungeon getFromFile(FileConfiguration yml) {
        String name = yml.getString("name");
        int price = yml.getInt("price");
        int level = yml.getInt("level");
        int secs = yml.getInt("times");
        int maxP = yml.getInt("max-players");
        boolean canjoin = yml.getBoolean("can-join-in-game");
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection item = yml.getConfigurationSection("items");
        for (String key : item.getKeys(false)) {
            ItemStack stack = (ItemStack) item.get(key);
            if (stack == null) {
                Bukkit.broadcast("stack is null", "td.debug");
                continue;
            }
            items.add(stack);
        }
        double x = yml.getDouble("location.x");
        double y = yml.getDouble("location.y");
        double z = yml.getDouble("location.z");
        double pitch = yml.getDouble("location.pitch");
        double yaw = yml.getDouble("location.yaw");
        World world = Bukkit.getWorld(yml.getString("location.world"));
        if (world == null){
            TimeDungeon.getPlugin().getLogger().warning("your world "+yml.getString("location.world")+" is not exist!");
            return null;
        }
        Location loc = new Location(world,x,y,z,(float)pitch,(float)yaw);
        double wx = yml.getDouble("wait-location.x");
        double wy = yml.getDouble("wait-location.y");
        double wz = yml.getDouble("wait-location.z");
        double wpitch = yml.getDouble("wait-location.pitch");
        double wyaw = yml.getDouble("wait-location.yaw");
        World wworld = Bukkit.getWorld(yml.getString("wait-location.world"));
        if (wworld == null){
            TimeDungeon.getPlugin().getLogger().warning("your world "+yml.getString("wait-location.world")+" is not exist!");
            return null;
        }
        Location wloc = new Location(wworld,wx,wy,wz,(float)wpitch,(float)wyaw);
        return new Dungeon(secs,name,price,level,items,loc,wloc,canjoin,maxP);
    }






}
