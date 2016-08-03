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
package com.samistine.samistineutilities.utils.annotations.command;

import com.samistine.samistineutilities.utils.annotations.command.backend.AnnotatedCommand;
import com.samistine.samistineutilities.utils.annotations.command.handler.CommandErrorHandler;
import java.util.Arrays;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Samuel
 */
public class Test {

    public Test() {

    }

    void run() throws NoSuchMethodException {
        System.out.println("com.samistine.samistineutilities.utils.annotations.command3.Test.run()");
        AnnotatedCommand command = new AnnotatedCommand("test", new String[0], "/test", "A test command", "", this, this.getClass().getDeclaredMethod("testMethod", CommandSender.class, String.class, String[].class), 1, -1, CommandErrorHandler.FEEDBACK);
        command.addSubcommand(new AnnotatedCommand("2", new String[0], "", "a subcommand", "", this,
                this.getClass().getDeclaredMethod("testMethod2", CommandSender.class, String.class, String[].class),
                0, 10, CommandErrorHandler.FEEDBACK));

        org.bukkit.command.Command cmd = command.getCommandInstance(null);

        do {
            int length = 10000;
            final long startTime = System.nanoTime();
            for (int i = 0; i < 10000; i++) {//Warmup
                cmd.execute(exec, "test", new String[]{"Main", "Command"});//Main Command
            }
            for (int i = 0; i < length; i++) {
                final long startTime2 = System.nanoTime();
                cmd.execute(exec, "test", new String[]{"Main", "Command"});//Main Command
                final long endTime2 = System.nanoTime();
                System.out.println("Execution time: " + (endTime2 - startTime2));
            }
            final long endTime = System.nanoTime();
            System.out.println("Total execution time: " + (endTime - startTime));
            System.out.println("Divided execution time: " + (endTime - startTime) / length);
        } while (false);

        do {
            int length = 10000;
            final long startTime = System.nanoTime();
            for (int i = 0; i < 10000; i++) {//Warmup
                cmd.execute(exec, "test", new String[]{"2", "A", "Subcommand"});//SubCommand
            }
            for (int i = 0; i < length; i++) {
                final long startTime2 = System.nanoTime();
                cmd.execute(exec, "test", new String[]{"2", "A", "Subcommand"});//SubCommand
                final long endTime2 = System.nanoTime();
                System.out.println("Execution time: " + (endTime2 - startTime2));
            }
            final long endTime = System.nanoTime();
            System.out.println("Total execution time: " + (endTime - startTime));
            System.out.println("Divided execution time: " + (endTime - startTime) / length);
        } while (false);

        System.out.println(cmd.tabComplete(exec, "test", new String[]{"2", ""})); //Tab Complete
    }

    @CommandTabCompletion(value = "a|b|c")
    void testMethod(CommandSender sender, String label, String[] args) {
        //System.out.println("com.samistine.samistineutilities.utils.annotations.command3.Test.testMethod()");
        //sender.sendMessage("Command Successfull" + Arrays.toString(args));
    }

    @CommandTabCompletion(value = "1|2|3")
    void testMethod2(CommandSender sender, String label, String[] args) {
        //System.out.println("com.samistine.samistineutilities.utils.annotations.command3.Test.testMethod2()");
        //sender.sendMessage("Command Successfull" + Arrays.toString(args));
    }

    public static void main(String[] args) throws Exception {
        new Test().run();
    }

    CommandSender exec = new CommandSender() {
        @Override
        public void sendMessage(String message) {
            System.err.println("SendMessage: " + message);
        }

        @Override
        public void sendMessage(String[] messages) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Server getServer() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isPermissionSet(String name) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isPermissionSet(Permission perm) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean hasPermission(String name) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean hasPermission(Permission perm) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeAttachment(PermissionAttachment attachment) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void recalculatePermissions() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isOp() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setOp(boolean value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
}
