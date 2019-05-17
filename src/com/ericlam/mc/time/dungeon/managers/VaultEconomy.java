package com.ericlam.mc.time.dungeon.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;

public class VaultEconomy {
    private Plugin plugin;

    public VaultEconomy(Plugin plugin) {
        this.plugin = plugin;
    }

    public Economy getEconomy(){
        Optional<RegisteredServiceProvider<Economy>> ecoPlugin = Optional.ofNullable(plugin.getServer().getServicesManager().getRegistration(Economy.class));
        RegisteredServiceProvider<Economy> eco = ecoPlugin.orElseThrow(()-> new IllegalStateException("我們找不到任何經濟插件！請安裝一個支援 Vault 的經濟插件!"));
        return eco.getProvider();
    }
}
