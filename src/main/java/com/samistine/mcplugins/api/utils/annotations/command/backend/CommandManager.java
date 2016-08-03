package com.samistine.samistineutilities.utils.annotations.command.backend;

import com.samistine.samistineutilities.utils.annotations.command.handler.CommandErrorHandler;
import com.samistine.samistineutilities.utils.BukkitUtils;
import java.lang.reflect.Method;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.samistine.samistineutilities.utils.ReflectionUtils;
import com.samistine.samistineutilities.utils.annotations.command.Command;
import com.samistine.samistineutilities.utils.annotations.command.SubCommand;
import com.samistine.samistineutilities.utils.annotations.command.exception.CommandRegistrationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to manage the commands for this plugin.
 *
 * @author Samuel Seidel
 */
public final class CommandManager {

    private final Plugin plugin;
    private final Map<Object, AnnotatedCommand> mappings = new HashMap<>();

    private CommandMap commandMap;

    /**
     * @param plugin The plugin that this manager is for.
     */
    public CommandManager(Plugin plugin) {
        this.plugin = plugin;

        try {
            this.commandMap = ReflectionUtils.getFieldValue(SimplePluginManager.class, "commandMap", CommandMap.class, plugin.getServer().getPluginManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers a new command executor.
     *
     * @param executor	The command executor to register.
     * @throws	CommandRegistrationException if the registration fails
     */
    public void registerCommandExecutor(Object executor) {
        @SuppressWarnings("unchecked")
        Class<? extends Object> cls = (Class<? extends Object>) executor.getClass();

        final Map<String, AnnotatedCommand> tempMap = new HashMap<>();

        for (Method method : cls.getDeclaredMethods()) {
            Command commandInfo = method.getAnnotation(Command.class);

            if (commandInfo != null) {
                String command_name = commandInfo.name();
                String[] command_aliases = commandInfo.aliases();
                String command_description = commandInfo.description();
                String command_usage = commandInfo.usage();
                String command_permission = commandInfo.permission();
                int command_minArgs = commandInfo.min();
                int command_maxArgs = commandInfo.max();

                CommandErrorHandler errorHandler;
                try {
                    errorHandler = commandInfo.errorHandler().newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, null, ex);
                    errorHandler = CommandErrorHandler.VOID;
                }

                AnnotatedCommand command = new AnnotatedCommand(
                        command_name,
                        command_aliases,
                        command_usage,
                        command_description,
                        command_permission,
                        executor,
                        method,
                        command_minArgs,
                        command_maxArgs,
                        errorHandler
                );

                this.commandMap.register(plugin.getDescription().getName(), command.getCommandInstance(plugin));
                tempMap.put(command_name, command);
                this.mappings.put(executor, command);
            }
        }

        for (Method method : cls.getDeclaredMethods()) {
            SubCommand subCommandInfo = method.getAnnotation(SubCommand.class);

            if (subCommandInfo != null) {
                String command_parent = subCommandInfo.parent();
                AnnotatedCommand parent = tempMap.get(command_parent);

                if (parent == null) {
                    throw new CommandRegistrationException("Attempted to register sub-command of " + subCommandInfo.parent() + " before main handler.");
                }

                String command_name = subCommandInfo.name();
                String[] command_aliases = new String[0];
                String command_description = subCommandInfo.description();
                String command_usage = subCommandInfo.usage();
                String command_permission = subCommandInfo.permission();
                int command_minArgs = subCommandInfo.min();
                int command_maxArgs = subCommandInfo.max();

                CommandErrorHandler errorHandler = null;
                try {
                    errorHandler = subCommandInfo.errorHandler().newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                AnnotatedCommand command = new AnnotatedCommand(
                        command_name,
                        command_aliases,
                        command_usage,
                        command_description,
                        command_permission,
                        executor,
                        method,
                        command_minArgs,
                        command_maxArgs,
                        errorHandler
                );

                parent.addSubcommand(command);
            }

        }
    }

    private void unRegisterCommand(org.bukkit.command.Command cmd) {
        try {
            Map<String, org.bukkit.command.Command> knownCommands = ReflectionUtils.getFieldValue(commandMap.getClass(), "knownCommands", Map.class, commandMap);
            knownCommands.remove(cmd.getName()).unregister(commandMap);

            //Removes other references to the command, such as "pluginname:command"
            for (Iterator<Map.Entry<String, org.bukkit.command.Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, org.bukkit.command.Command> entry = it.next();
                if (entry.getValue() == cmd) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BukkitUtils.unregisterHelpTopic(cmd);
    }

    //TODO: Add error handling
    public void unRegisterCommandExecutor(Object executor) {
        AnnotatedCommand aCommand = mappings.get(executor);
        org.bukkit.command.Command pluginCommand = aCommand.getCommandInstance(plugin);
        unRegisterCommand(pluginCommand);
        mappings.remove(executor);
    }

}
