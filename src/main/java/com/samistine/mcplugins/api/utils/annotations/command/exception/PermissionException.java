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
package com.samistine.samistineutilities.utils.annotations.command.exception;

/**
 * Thrown when executing a command if the command-sender does not have
 * sufficient privileges for the command.
 *
 * @author Samuel Seidel
 */
public class PermissionException extends CommandException {

    /**
     * The permissions required to execute the command
     */
    private final String permission;

    /**
     * Create a new instance of {@link PermissionException}
     *
     * @param permission permission that was needed to execute the command
     * without throwing this exception
     */
    public PermissionException(String command, String permission) {
        super(command, "Permission {" + permission + "} is required to execute this command");
        this.permission = permission;
    }

    /**
     * Get the permission needed to execute the command
     *
     * @return permission
     */
    public String getPermission() {
        return permission;
    }

}
