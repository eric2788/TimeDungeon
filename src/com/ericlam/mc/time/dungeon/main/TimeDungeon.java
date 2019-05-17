package com.ericlam.mc.time.dungeon.main;

import com.ericlam.mc.time.dungeon.commands.ReloadCommand;
import com.ericlam.mc.time.dungeon.commands.TDCommandExecutor;
import com.ericlam.mc.time.dungeon.commands.admins.*;
import com.ericlam.mc.time.dungeon.commands.admins.cost.CostMainCommand;
import com.ericlam.mc.time.dungeon.commands.admins.warp.WarpMainCommand;
import com.ericlam.mc.time.dungeon.commands.players.JoinCommand;
import com.ericlam.mc.time.dungeon.commands.players.LeaveCommand;
import com.ericlam.mc.time.dungeon.listeners.PlayerListener;
import com.ericlam.mc.time.dungeon.listeners.SignListeners;
import com.ericlam.mc.time.dungeon.managers.VaultEconomy;
import com.ericlam.mc.time.dungeon.papi.TDPlaceholder;
import com.hypernite.mc.api.commands.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class TimeDungeon extends JavaPlugin {

    private static TimeDungeon plugin;

    public static TimeDungeon getPlugin() {
        return plugin;
    }

    public static FileConfiguration getMyConfig(){
        return plugin.getConfig();
    }

    private static FileConfiguration msgConfig;

    private static FileConfiguration signsData;

    public static FileConfiguration getSignsData() {
        return signsData;
    }

    public static String[] getSignLines() {
        return msgConfig.getStringList("signs.lines").stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).limit(4).toArray(String[]::new);
    }

    public static Location getSpawn() {
        FileConfiguration config = plugin.getConfig();
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        World world = Bukkit.getWorld(config.getString("spawn.world"));
        double pitch = config.getDouble("spawn.pitch");
        double yaw = config.getDouble("spawn.yaw");
        if (world == null) return null;
        return new Location(world, x, y, z, (float) pitch, (float) yaw);
    }

    public static int waitSecs,endSecs;

    public static int getWaitSecs() {
        return waitSecs;
    }

    public static int getEndSecs() {
        return endSecs;
    }

    public static void warn(String msg){
        plugin.getLogger().warning(msg);
    }

    private static VaultEconomy vaultEconomy;

    public static VaultEconomy getVaultEconomy() {
        return vaultEconomy;
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();
        this.reloadConfig();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        this.getServer().getPluginManager().registerEvents(new SignListeners(this), this);
        this.getCommand("td").setExecutor(new TDCommandExecutor(this));
        CommandManager manager = CommandManager.getInstance();

        /*
            Player commands
         */

        manager.registerCommand(new JoinCommand(this));
        manager.registerCommand(new LeaveCommand(this));


        /*
            Admin commands
         */

        manager.registerCommand(new CanJoinCommand(this));
        manager.registerCommand(new CreateCommand(this));
        manager.registerCommand(new DeleteCommand(this));
        manager.registerCommand(new InfoCommand(this));
        manager.registerCommand(new SetSpawnCommand(this));
        manager.registerCommand(new ListCommand(this));
        manager.registerCommand(new MaxPlayerCommand(this));
        manager.registerCommand(new NameCommand(this));
        manager.registerCommand(new SaveCommand(this));
        manager.registerCommand(new ReloadCommand(this));
        manager.registerCommand(new TimeCommand(this));
        manager.registerCommand(new CostMainCommand(this));
        manager.registerCommand(new WarpMainCommand(this));


        /*
            API Hooking
         */

        vaultEconomy = new VaultEconomy(this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            getLogger().info("找到 PlaceholderAPI 插件！ 正在掛接...");
            new TDPlaceholder(this).register();
        }
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        File msgFile = new File(getDataFolder(),"messages.yml");
        File signsFile = new File(getDataFolder(), "signs.yml");
        if (!msgFile.exists()) saveResource("messages.yml",true);
        if (!signsFile.exists()) saveResource("signs.yml", true);
        signsData = YamlConfiguration.loadConfiguration(signsFile);
    }

    public static String getMessage(String path){
        return ChatColor.translateAlternateColorCodes('&',msgConfig.getString("prefix")+msgConfig.getString(path));
    }

    public static String getPureMessage(String path){
        return ChatColor.translateAlternateColorCodes('&',msgConfig.getString(path));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void reloadConfig(){
        super.reloadConfig();
        waitSecs = this.getConfig().getInt("wait-time");
        endSecs = this.getConfig().getInt("end-time");
        msgConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
    }

    public static List<String> getStringList(String path){
        return msgConfig.getStringList(path).stream().map(e->ChatColor.translateAlternateColorCodes('&',msgConfig.getString("prefix")+e)).collect(Collectors.toList());
    }
}
