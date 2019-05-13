package com.ericlam.mc.time.dungeon.main;

import com.ericlam.mc.time.dungeon.commands.TDCommandExecutor;
import com.ericlam.mc.time.dungeon.commands.admins.*;
import com.ericlam.mc.time.dungeon.commands.admins.cost.CostMainCommand;
import com.ericlam.mc.time.dungeon.commands.admins.warp.WarpMainCommand;
import com.ericlam.mc.time.dungeon.commands.players.JoinCommand;
import com.ericlam.mc.time.dungeon.commands.players.LeaveCommand;
import com.ericlam.mc.time.dungeon.listeners.PlayerListener;
import com.ericlam.mc.time.dungeon.papi.TDPlaceholder;
import com.hypernite.mc.api.commands.CommandManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Optional;
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

    public static Location spawn;

    public static Location getSpawn() {
        return spawn;
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

    private static Economy vauleEco;

    public static Economy getVauleEco() {
        return vauleEco;
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();
        this.reloadConfig();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(),this);
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
        manager.registerCommand(new ListCommand(this));
        manager.registerCommand(new MaxPlayerCommand(this));
        manager.registerCommand(new NameCommand(this));
        manager.registerCommand(new SaveCommand(this));
        manager.registerCommand(new TimeCommand(this));
        manager.registerCommand(new CostMainCommand(this));
        manager.registerCommand(new WarpMainCommand(this));


        /*
            API Hooking
         */

        Optional<RegisteredServiceProvider<Economy>> ecoPlugin = Optional.ofNullable(getServer().getServicesManager().getRegistration(Economy.class));
        RegisteredServiceProvider<Economy> eco = ecoPlugin.orElseThrow(()->{
            getLogger().warning("我們找不到任何經濟插件！請安裝一個支援 Vault 的經濟插件!");
            return new IllegalStateException("請安裝一個支援 Vault 的經濟插件!");
        });
        vauleEco = eco.getProvider();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            getLogger().info("找到 PlaceholderAPI 插件！ 正在掛接...");
            new TDPlaceholder(this).register();
        }
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        File msgFile = new File(getDataFolder(),"messages.yml");
        if (!msgFile.exists()) saveResource("messages.yml",true);
        msgConfig = YamlConfiguration.loadConfiguration(msgFile);
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
        spawn = getSpawnPoint();
        waitSecs = this.getConfig().getInt("wait-time");
        endSecs = this.getConfig().getInt("end-time");
    }

    public static List<String> getStringList(String path){
        return msgConfig.getStringList(path).stream().map(e->ChatColor.translateAlternateColorCodes('&',msgConfig.getString("prefix")+e)).collect(Collectors.toList());
    }


    private Location getSpawnPoint(){
        FileConfiguration config = this.getConfig();
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        World world = Bukkit.getWorld(config.getString("spawn.world"));
        double pitch = config.getDouble("spawn.pitch");
        double yaw = config.getDouble("spawn.yaw");
        if (world == null) return null;
        return new Location(world,x,y,z,(float)pitch,(float)yaw);
    }
}
