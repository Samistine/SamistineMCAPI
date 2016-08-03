/*
 * Public Domain
 */
package com.samistine.samistineutilities.utils.annotations.command.exception;

/**
 * This class and it's subclasses are thrown when an issue occurs while
 * executing a user's command.
 *
 * <p>
 * This class and subclasses were inspired by
 * <a href="https://github.com/InventivetalentDev/PluginAnnotations/">PluginAnnotations</a>
 * by InventivetalentDev
 * </p>
 *
 * @author Samuel Seidel
 */
public class CommandException extends RuntimeException {

    /**
     * The command for which this exception is being thrown for
     */
    private final String command;

    public CommandException(String command, String message) {
        super(message);
        this.command = command;
    }

    public CommandException(String command, Throwable cause) {
        super(cause);
        this.command = command;
    }

    public CommandException(String command, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

}
