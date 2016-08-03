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
package com.samistine.samistineutilities.utils.annotations.config;

import com.samistine.samistineutilities.utils.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Samuel Seidel
 */
public class ConfigPathProcessor {

    private final Logger logger;

    public ConfigPathProcessor(Logger logger) {
        this.logger = logger;
    }

    /**
     * Load the plugin's configuration values into the class fields
     *
     * @param conf {@link ConfigurationSection} to load the configuration from
     * @param classToLoad Class to set the fields in
     * @throws IllegalArgumentException if <code>conf</code> and/or
     * <code>classToLoad</code> are null
     */
    public void loadValues(ConfigurationSection conf, Object classToLoad) {
        Validate.notNull(conf, "ConfigurationSection, conf, cannot be null");
        Validate.notNull(classToLoad, "Object, classToLoad, cannot be null");

        logger.log(Level.FINE, "Processing ConfigPath annotations for object: {0}", classToLoad.getClass().getName());

        for (Field field : ReflectionUtils.findFields(classToLoad.getClass(), ConfigPath.class)) {
            try {
                loadValues(conf, classToLoad, field);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set config value for field '" + field.getName() + "' in " + classToLoad.getClass(), e);
            }
        }
    }

    private void loadValues(ConfigurationSection conf, Object classToLoad, Field field) throws IllegalArgumentException, IllegalAccessException {
        ConfigPath annotation = field.getAnnotation(ConfigPath.class);
        field.setAccessible(true);

        String path = annotation.path();
        if (conf.contains(path)) {
            Object conf_value = conf.get(path);
            field.set(classToLoad, conf_value);
        } else {
            //TODO: Parse value type
        }
    }

}
