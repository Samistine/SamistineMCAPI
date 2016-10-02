package com.samistine.mcplugins.api;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * An SFeature Listener that automatically registers itself on onEnable() and
 * disables itself on onDisable().
 *
 * @author Samuel Seidel
 */
public class SFeatureListener extends SFeature implements Listener {

    public SFeatureListener(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onEnable() {
        getServer().getPluginManager().registerEvents(this, featurePlugin);
    }

    @Override
    protected void onDisable() {
        HandlerList.unregisterAll(this);
    }

}
