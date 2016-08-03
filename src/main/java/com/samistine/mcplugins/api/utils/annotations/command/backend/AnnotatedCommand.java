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
package com.samistine.samistineutilities.utils.annotations.command.backend;

import com.samistine.samistineutilities.utils.annotations.command.CommandTabCompletion;
import com.samistine.samistineutilities.utils.annotations.command.handler.CommandErrorHandler;
import com.samistine.samistineutilities.utils.annotations.command.exception.CommandException;
import com.samistine.samistineutilities.utils.annotations.command.exception.CommandRegistrationException;
import com.samistine.samistineutilities.utils.annotations.command.exception.IllegalSenderException;
import com.samistine.samistineutilities.utils.annotations.command.exception.InvalidLengthException;
import com.samistine.samistineutilities.utils.annotations.command.exception.PermissionException;
import com.samistine.samistineutilities.utils.annotations.command.exception.UnhandledCommandException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Samuel Seidel
 */
public class AnnotatedCommand {

    private static final Logger logger = Logger.getLogger("AnnotatedCommand");

    private AnnotatedCommand parent = null;

    private final String name;
    private final String[] aliases;
    private final String usage;
    private final String description;
    private final String permission;

    private final Object commandClass;
    private final Method commandMethod;

    private final int minArgs;
    private final int maxArgs;

    private CommandErrorHandler errorHandler;

    Collection<AnnotatedCommand> subCommands = new ArrayList<>();

    public AnnotatedCommand(String name, String[] aliases, String usage, String description, String permission, Object commandClass, Method commandMethod, int minArgs, int maxArgs, CommandErrorHandler errorHandler) {
        this.name = name;
        this.aliases = aliases;
        this.usage = usage;
        this.description = description;
        this.permission = permission;
        this.commandClass = commandClass;
        this.commandMethod = commandMethod;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.errorHandler = errorHandler;

        //Validate {@link commandMethod}
        if (!commandMethod.getReturnType().equals(Void.TYPE)) {
            throw new CommandRegistrationException("Incorrect return type for command method " + commandMethod.getName() + " in " + commandClass.getClass().getName());
        } else if (!Arrays.equals(commandMethod.getParameterTypes(), new Class<?>[]{CommandSender.class, String.class, String[].class})) {
            throw new CommandRegistrationException("Incorrect arguments for command method " + commandMethod.getName() + " in " + commandClass.getClass().getName());
        }

        commandMethod.setAccessible(true);
    }

    public String getName() {
        return name;
    }

    public String getUsageMessage() {
        String msg;
        if (parent != null) {
            msg = "Usage: /" + parent.name + " " + name;
            if (!usage.isEmpty()) {
                msg = msg + " " + usage;
            }
        } else {
            msg = "Usage: /" + name + " " + usage;
        }
        return msg;
    }

    private Command pluginCommand;

    public Command getCommandInstance(Plugin plugin) {
        if (parent != null) {
            return parent.getCommandInstance(plugin);
        }
        if (pluginCommand == null) {
            pluginCommand = new BukkitCommand(plugin, this);
        }
        return pluginCommand;
    }

    public void addSubcommand(AnnotatedCommand command) {
        command.parent = this;
        subCommands.add(command);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        logger.log(Level.FINE, "Running command({0}) for label({1})", new Object[]{name, label});

        //Check for subcommands.
        if (args.length > 0) {
            String subCmd = args[0];
            for (AnnotatedCommand acommand : subCommands) {
                //If one exists, run it and return its value
                if (acommand.name.equals(subCmd)) {
                    logger.log(Level.FINER, "Found subcommand({0})", acommand.name);
                    return acommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        logger.log(Level.FINER, "Executing command {0}", toString());
        try {
            //Check if the sender is of the correct type to execute the command
            checkSenderType(sender);

            //Check permission
            checkPermission(sender);

            //Check argument length
            checkArgsLength(args);

            try {
                commandMethod.invoke(commandClass, sender, label, args);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof CommandException) {
                    throw (CommandException) cause;
                }
                throw new UnhandledCommandException(this.name, "Unhandled exception while invoking command method in " + this.commandClass + "#" + this.commandMethod.getName(), cause);
            } catch (Throwable throwable) {
                if (throwable instanceof CommandException) {
                    throw (CommandException) throwable;
                } else {
                    throw new UnhandledCommandException(this.name, "Unhandled exception in " + this.commandClass + "#" + this.commandMethod.getName(), throwable);
                }
            }

            return true;
        } catch (CommandException ex) {
            if (errorHandler != null) {
                errorHandler.handleException(ex, sender, command, args);
                return false;
            } else {
                throw ex;
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        //Check for subcommands.
        if (args.length > 0) {
            String subCmd = args[0];
            for (AnnotatedCommand acommand : subCommands) {
                //If one exists, run it and return its value
                if (acommand.name.equals(subCmd)) {
                    logger.log(Level.FINER, "Found subcommand({0})", acommand.name);
                    return acommand.onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        CommandTabCompletion tabAnnotation = commandMethod.getAnnotation(CommandTabCompletion.class);

        if (tabAnnotation != null) {
            Method handlerMethod_ = commandMethod;
            String[] tabCompletion_ = tabAnnotation.value();

            ArrayList<String> completions = new ArrayList<>();

            boolean empty = args[args.length - 1].isEmpty();

            if (args.length <= tabCompletion_.length) {
                String tab = tabCompletion_[args.length - 1];
                String last = args[args.length - 1].toLowerCase();

                if (tab.equalsIgnoreCase("<online_player>")) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        String playerName = player.getName();
                        String testName = playerName.toLowerCase();

                        if (empty || testName.startsWith(last)) {
                            completions.add(playerName);
                        }
                    }
                } else if (tab.equalsIgnoreCase("<player>")) {
                    for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                        String playerName = player.getName();
                        String testName = playerName.toLowerCase();

                        if (empty || testName.startsWith(last)) {
                            completions.add(playerName);
                        }
                    }
                } else if (tab.startsWith("[") && tab.endsWith("]")) {
                    try {
                        Method tabHandler = handlerMethod_.getDeclaringClass().getMethod(tab.substring(1, tab.length() - 1), CommandSender.class, String[].class);

                        if (tabHandler.getReturnType().equals(List.class)) {
                            for (String value : (List<String>) tabHandler.invoke(commandClass, sender, args)) {
                                String testValue = value.toLowerCase();

                                if (empty || testValue.startsWith(last)) {
                                    completions.add(value);
                                }
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (String value : tab.split("\\|")) {
                        String testValue = value.toLowerCase();

                        if (empty || testValue.startsWith(last)) {
                            completions.add(value);
                        }
                    }
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }

    private void checkPermission(CommandSender sender) {
        if (parent != null) {
            //Require sender to have permission for the root/above command
            parent.checkPermission(sender);
        }
        boolean hasPermission = (permission == null || permission.isEmpty()) || sender.hasPermission(permission);
        if (!hasPermission) {
            throw new PermissionException(this.name, this.permission);
        }
    }

    private void checkArgsLength(String[] args) {
        if (args.length < minArgs) {
            throw new InvalidLengthException(this.name, this.minArgs, args.length, getUsageMessage());
        }
        if (maxArgs != -1 && args.length > maxArgs) {
            throw new InvalidLengthException(this.name, this.maxArgs, args.length, getUsageMessage());
        }
    }

    private void checkSenderType(CommandSender sender) {
        Class<?>[] parameterTypes = commandMethod.getParameterTypes();

        if (Player.class.isAssignableFrom(parameterTypes[0])) {
            if (!(sender instanceof Player)) {
                throw new IllegalSenderException(this.name,
                        IllegalSenderException.SENDER.CONSOLE, IllegalSenderException.SENDER.PLAYER
                );
            }
        }
    }

    private static class BukkitCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {

        private final Plugin plugin;
        private final AnnotatedCommand annotatedCommand;

        protected BukkitCommand(Plugin plugin, AnnotatedCommand command) {
            super(
                    command.name,
                    command.description,
                    command.usage,
                    Arrays.asList(command.aliases)
            );
            this.plugin = plugin;
            this.annotatedCommand = command;
        }

        @Override
        public final boolean execute(CommandSender sender, String label, String[] args) {
            return annotatedCommand.onCommand(sender, this, label, args);
        }

        @Override
        public final List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
            return annotatedCommand.onTabComplete(sender, this, label, args);
        }

        @Override
        public Plugin getPlugin() {
            return plugin;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.name.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnnotatedCommand other = (AnnotatedCommand) obj;
        return this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return "AnnotatedCommand{"
                + "parent=" + (parent == null ? "null" : parent.name)
                + ", name=" + name
                + ", aliases=" + Arrays.toString(aliases)
                + ", usage=" + usage
                + ", description=" + description
                + ", permission=" + permission
                + ", commandClass=" + commandClass
                + ", commandMethod=" + commandMethod
                + ", minArgs=" + minArgs
                + ", maxArgs=" + maxArgs
                + ", errorHandler=" + errorHandler
                + ", subCommands=" + subCommands
                + ", pluginCommand=" + pluginCommand + '}';
    }

}
