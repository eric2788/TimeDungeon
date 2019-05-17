package com.ericlam.mc.time.dungeon.listeners;

import com.ericlam.mc.time.dungeon.Dungeon;
import com.ericlam.mc.time.dungeon.exceptions.DungeonNonExistException;
import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.DungeonManager;
import com.ericlam.mc.time.dungeon.managers.StorageManager;
import com.ericlam.mc.time.dungeon.runners.DungeonRunner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SignListeners implements Listener {

    private FileConfiguration signs;
    private File signF;

    public SignListeners(TimeDungeon plugin) {
        this.signs = TimeDungeon.getSignsData();
        this.signF = new File(plugin.getDataFolder(), "signs.yml");
    }

    public static void updateSign(String id, DungeonRunner.State state, int players) {
        Optional<Sign> signOptional = getSignFromId(id);
        Dungeon dungeon;
        try {
            dungeon = StorageManager.getInstance().findDungeonLocal(id);
        } catch (DungeonNonExistException e) {
            return;
        }
        if (!signOptional.isPresent()) return;
        Sign sign = signOptional.get();
        String[] lines = TimeDungeon.getSignLines();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i]
                    .replace("<name>", dungeon.getName())
                    .replace("<mp>", dungeon.getMaxPlayers() + "")
                    .replace("<p>", players + "")
                    .replace("<state>", TimeDungeon.getPureMessage("placeholder.stats." + state.toString()));
            sign.setLine(i, line);
        }
        sign.update(true);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendBlockChange(sign.getLocation(), sign.getBlockData()));
    }

    private static Optional<Sign> getSignFromId(String id) {
        FileConfiguration sign = TimeDungeon.getSignsData();
        if (!sign.contains(id)) return Optional.empty();
        ConfigurationSection section = sign.getConfigurationSection(id);
        final int x = section.getInt("x");
        final int y = section.getInt("y");
        final int z = section.getInt("z");
        final World world = Bukkit.getWorld(section.getString("world"));
        if (world == null) return Optional.empty();
        Block block = world.getBlockAt(x, y, z);
        return Optional.ofNullable((Sign) block.getState());
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLines().length < 2) return;
        String identifier = e.getLine(0);
        if (!identifier.equalsIgnoreCase("[TimeDungeon]")) return;
        String id = e.getLine(1);
        try {
            StorageManager.getInstance().findDungeonLocal(id);
        } catch (DungeonNonExistException ex) {
            e.getPlayer().sendMessage(TimeDungeon.getMessage("error.no-exist-dun"));
            return;
        }
        final int x = e.getBlock().getX();
        final int y = e.getBlock().getY();
        final int z = e.getBlock().getZ();
        final String world = e.getBlock().getWorld().getName();
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("x", x);
        section.put("y", y);
        section.put("z", z);
        section.put("world", world);
        signs.createSection(id, section);
        try {
            signs.save(signF);
            e.getPlayer().sendMessage(TimeDungeon.getMessage("signs.created"));
        } catch (IOException ex) {
            ex.printStackTrace();
            e.getPlayer().sendMessage(TimeDungeon.getMessage("setup.failed"));
            return;
        }
        updateSign(id, DungeonRunner.State.NONE, 0);
    }

    private Optional<String> getIdFromBlock(Block block) {
        final int x = block.getX();
        final int y = block.getY();
        final int z = block.getZ();
        final String world = block.getWorld().getName();
        String id = null;
        for (String key : signs.getKeys(false)) {
            boolean matchX = signs.getInt(key + ".x") == x;
            boolean matchY = signs.getInt(key + ".y") == y;
            boolean matchZ = signs.getInt(key + ".z") == z;
            boolean matchW = signs.getString(key + ".world").equals(world);
            if (matchX && matchY && matchZ && matchW) {
                id = key;
                break;
            }
        }
        return Optional.ofNullable(id);
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.SIGN && e.getBlock().getType() != Material.WALL_SIGN) return;
        Optional<String> id = getIdFromBlock(e.getBlock());
        if (!id.isPresent()) return;
        signs.set(id.get(), null);
        try {
            signs.save(signF);
            e.getPlayer().sendMessage(TimeDungeon.getMessage("signs.destroyed"));
        } catch (IOException ex) {
            e.getPlayer().sendMessage(TimeDungeon.getMessage("setup.failed"));
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) return;
        Optional<String> id = getIdFromBlock(block);
        if (!id.isPresent()) return;
        DungeonManager.getInstance().joinGame(e.getPlayer(), id.get());
    }
}
