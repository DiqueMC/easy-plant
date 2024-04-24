package com.diquemc.easyplant;

import org.bukkit.plugin.java.JavaPlugin;

public class EasyPlant extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
