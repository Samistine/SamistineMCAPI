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
 * Thrown when the command-sender is not of the right type to execute the
 * command.
 * <br>Thrown when a console attempts to issue a player only command.
 * <br>Thrown when a player attempts to issue a console only command.
 *
 * @author Samuel Seidel
 */
public class IllegalSenderException extends CommandException {

    public static enum SENDER {
        PLAYER("a player"),
        CONSOLE("the console");

        private final String title;

        private SENDER(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

    }

    private final SENDER encounteredSender;
    private final SENDER expectedSender;

    /**
     * Create a new instance of {@link IllegalSenderException} specifying the
     * wrong sender that was encountered and the correct sender that is required
     * for the command to execute.
     *
     * @param command the command for which this exception is being thrown for
     * @param encounteredSender the {@link SENDER} that issued the command
     * @param expectedSender the {@link SENDER} required for this command
     */
    public IllegalSenderException(String command, SENDER encounteredSender, SENDER expectedSender) {
        super(command, "Encountered {" + encounteredSender.getTitle() + "}, expected and required {" + expectedSender + "} while proccessing command");
        this.encounteredSender = encounteredSender;
        this.expectedSender = expectedSender;
    }

    public SENDER getEncounteredSender() {
        return encounteredSender;
    }

    public SENDER getExpectedSender() {
        return expectedSender;
    }

}
