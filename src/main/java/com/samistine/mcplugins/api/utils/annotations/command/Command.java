package com.samistine.samistineutilities.utils.annotations.command;

import com.samistine.samistineutilities.utils.annotations.command.handler.CommandErrorHandler;
import com.samistine.samistineutilities.utils.annotations.command.handler.FeedbackErrorHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this method should be used to handle a command.
 *
 * @author Samuel Seidel
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Name of the command.
     *
     * @return	command name
     */
    String name();

    /**
     * Command aliases
     *
     * @return	aliases
     */
    String[] aliases() default {};

    /**
     * @return	The long description of this command.
     */
    String description();

    /**
     * @return	The usage information for this command.
     */
    String usage() default "";

    /**
     * Permission required to use this sub-command
     *
     * @return	permission
     */
    String permission() default "";

    /**
     * Minimum argument length
     *
     * @return	min argument length
     */
    int min() default 0;

    /**
     * Maximum argument length (-1 for no limitation)
     *
     * @return	max argument length
     */
    int max() default -1;

    Class<? extends CommandErrorHandler> errorHandler() default FeedbackErrorHandler.class;

}
