/*
 * The MIT License
 *
 * Copyright 2016 Samuel Seidel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.samistine.mcplugins.api;

import com.samistine.mcplugins.api.utils.annotations.command.backend.CommandManager;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Samuel Seidel
 */
public abstract class SFeature {

    protected final JavaPlugin featurePlugin;
    protected final Server featureServer;
    protected final String featureName;
    protected final String featureDesc;
    protected final Logger featureLogger;

    /**
     * Convenience constructor when specifying the feature's name and
     * description within a {@link FeatureInfo} annotation
     *
     * @param plugin the Spigot plugin responsible for this feature
     */
    protected SFeature(JavaPlugin plugin) {
        FeatureInfo info = getClass().getAnnotation(FeatureInfo.class);
        Validate.notNull(info, "Feature must contain FeatureInfo anotation when using the convenience constuctor");
        this.featurePlugin = plugin;
        this.featureServer = plugin.getServer();
        this.featureName = info.name();
        this.featureDesc = info.desc();
        this.featureLogger = new FeatureLogger(this);
        validateConstruction();
    }

    /**
     *
     * @param plugin the Spigot plugin responsible this feature
     * @param name the name of this feature
     * @param description a description about this feature
     */
    protected SFeature(JavaPlugin plugin, String name, String description) {
        this.featurePlugin = plugin;
        this.featureServer = plugin.getServer();
        this.featureName = name;
        this.featureDesc = description;
        this.featureLogger = new FeatureLogger(this);
        validateConstruction();
    }

    /**
     * Validate everything is setup properly, (no null fields).
     */
    private void validateConstruction() {
        Validate.notNull(featurePlugin, "Plugin cannot be null");
        Validate.notNull(featureServer, "Server cannot be null");
        Validate.notNull(featureName, "Name cannot be null");
        Validate.notNull(featureDesc, "Description cannot be null");
        Validate.notNull(featureLogger, "Logger cannot be null");
    }

    private boolean running;

    boolean start() {
        if (isRunning()) {
            throw new AlreadyStartedException(featureName + " is already running.");
        } else if (shouldEnable()) {
            featureLogger.log(Level.FINE, "Enabling");
            onEnable();
            featureLogger.log(Level.INFO, "Enabled");
            running = true;
        }
        return running;
    }

    void stop() {
        if (isRunning()) {
            featureLogger.log(Level.FINE, "Disabling");
            onDisable();
            featureLogger.log(Level.INFO, "Disabled");
            running = false;
        }
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected boolean shouldEnable() {
        return getRootConfig().get(featureName, null) != null;
    }

    protected ConfigurationSection getConfig() {
        return getRootConfig().getConfigurationSection(featureName);
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * The name of this feature
     *
     * @return name
     */
    public final String getName() {
        return featureName;
    }

    /**
     * A short one-to-two liner description about this feature
     *
     * @return description
     */
    public final String getDesc() {
        return featureDesc;
    }

    /**
     * Gets the logger for the feature.
     * <p>
     * This logger contains this feature's {@link #getName() name} to assist in
     * debugging.
     *
     * @return logger
     */
    public final Logger getLogger() {
        return featureLogger;
    }

    /**
     * Gets the running server.
     *
     * @return server
     */
    public final Server getServer() {
        return featureServer;
    }

    /**
     * Get the plugin that is responsible for this feature.
     *
     * @return plugin
     */
    public final Plugin getRootPlugin() {
        return featurePlugin;
    }

    /**
     * Gets the plugin that is responsible for this feature,'s root/main config.
     *
     * @return config
     */
    public final FileConfiguration getRootConfig() {
        return featurePlugin.getConfig();
    }

    /**
     * Gets the command with the given name, specific to this plugin. Commands
     * need to be registered in the {@link PluginDescriptionFile#getCommands()
     * PluginDescriptionFile} to exist at runtime.
     *
     * @param name name or alias of the command
     * @return the plugin command if found, otherwise null
     */
    protected final PluginCommand getCommand(String name) {
        return featurePlugin.getCommand(name);
    }

    protected final void registerCommand(Object command) {
        getCommandManager().registerCommandExecutor(command);
    }

    protected final void unregisterCommand(Object command) {
        getCommandManager().unRegisterCommandExecutor(command);
    }

    private CommandManager cm;

    private CommandManager getCommandManager() {
        if (cm == null) {
            cm = new CommandManager(getRootPlugin());
        }
        return cm;
    }
}
