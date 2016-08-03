/*
 * The MIT License
 *
 * Copyright 2016 Samuel.
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
package com.samistine.samistineutilities.utils.annotations.command.handler;

import com.samistine.samistineutilities.utils.annotations.command.exception.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Samuel
 */
public class FeedbackErrorHandler implements CommandErrorHandler {

    @Override
    public void handleCommandException(CommandException exception, CommandSender sender, Command command, String[] args) {
        sender.sendMessage(ChatColor.RED + "Unknown exception, see console for details: " + exception.getMessage());
        throw exception;
    }

    @Override
    public void handlePermissionException(PermissionException exception, CommandSender sender, Command command, String[] args) {
        sender.sendMessage(ChatColor.RED + "You are missing the following permission: " + exception.getPermission());
    }

    @Override
    public void handleIllegalSender(IllegalSenderException exception, CommandSender sender, Command command, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is only available to players");
    }

    @Override
    public void handleUnhandled(UnhandledCommandException exception, CommandSender sender, Command command, String[] args) {
        sender.sendMessage(ChatColor.RED + "Unhandled exception, see console for details: " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void handleLength(InvalidLengthException exception, CommandSender sender, Command command, String[] args) {
        sender.sendMessage(ChatColor.RED + exception.getMessage());
        sender.sendMessage(ChatColor.RED + exception.getUsage());
    }

    @Override
    public void handleArgumentParse(ArgumentParseException exception, CommandSender sender, Command command, String[] args) {
        sender.sendMessage(ChatColor.RED + exception.getMessage());
        throw exception;
    }
}
