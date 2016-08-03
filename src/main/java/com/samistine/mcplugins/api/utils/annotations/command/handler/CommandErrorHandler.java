/*
 * Copyright 2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */
package com.samistine.samistineutilities.utils.annotations.command.handler;

import com.samistine.samistineutilities.utils.annotations.command.exception.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandErrorHandler {

    public static final VoidErrorHandler VOID = new VoidErrorHandler();
    public static final FeedbackErrorHandler FEEDBACK = new FeedbackErrorHandler();

    public void handleCommandException(CommandException exception, CommandSender sender, Command command, String[] args);

    public void handlePermissionException(PermissionException exception, CommandSender sender, Command command, String[] args);

    public void handleIllegalSender(IllegalSenderException exception, CommandSender sender, Command command, String[] args);

    public void handleLength(InvalidLengthException exception, CommandSender sender, Command command, String[] args);

    public void handleArgumentParse(ArgumentParseException exception, CommandSender sender, Command command, String[] args);

    public void handleUnhandled(UnhandledCommandException exception, CommandSender sender, Command command, String[] args);

    default void handleException(CommandException ex, CommandSender sender, Command command, String[] args) {
        if (ex instanceof PermissionException) {
            handlePermissionException((PermissionException) ex, sender, command, args);
        } else if (ex instanceof IllegalSenderException) {
            handleIllegalSender((IllegalSenderException) ex, sender, command, args);
        } else if (ex instanceof ArgumentParseException) {
            handleArgumentParse((ArgumentParseException) ex, sender, command, args);
        } else if (ex instanceof InvalidLengthException) {
            handleLength((InvalidLengthException) ex, sender, command, args);
        } else if (ex instanceof UnhandledCommandException) {
            handleUnhandled((UnhandledCommandException) ex, sender, command, args);
        } else {
            handleCommandException((CommandException) ex, sender, command, args);
        }
    }

}
