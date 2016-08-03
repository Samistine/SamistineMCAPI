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
package com.samistine.samistineutilities.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;

/**
 *
 * @author Samuel
 */
public class BukkitUtils {

    //See: https://bukkit.org/threads/0-references-still-not-being-garbage-collected.203502/
    public static void unregisterHelpTopic(Command command) {
        try {
            Collection<HelpTopic> topics = Bukkit.getHelpMap().getHelpTopics();
            Field cmdField = GenericCommandHelpTopic.class.getDeclaredField("command");
            cmdField.setAccessible(true);

            Iterator<HelpTopic> iter = topics.iterator();
            // Because the stupid HelpTopic stick onto here.
            while (iter.hasNext()) {
                HelpTopic ht = iter.next();
                if (ht instanceof GenericCommandHelpTopic) {
                    GenericCommandHelpTopic gcht = (GenericCommandHelpTopic) ht;
                    Command command2 = (Command) cmdField.get(gcht);
                    if (command2 == command) {
                        cmdField.set(gcht, null);
                        iter.remove();
                    }
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> configSectionStringValues(ConfigurationSection section) {
        Map<String, Object> values = section.getValues(false);
        return values.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof String)
                .collect(Collectors.toMap(Entry::getKey, entry -> (String) entry.getValue()));
    }
}
