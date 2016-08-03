package com.samistine.mcplugins.api;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The UtilityLogger class is a modified {@link Logger} that prepends all
 * logging calls with the name of the root plugin plus the utility doing the
 * logging. The API is exactly the same as {@link Logger}.
 *
 * @see Logger
 */
final class FeatureLogger extends Logger {

    private final String prefix;

    /**
     * Creates a new FeatureLogger that extracts the name from a plugin.
     *
     * @param context A reference to the plugin
     */
    public FeatureLogger(SFeature context) {
        super(context.getClass().getCanonicalName(), null);
        String pluginName = context.getRootPlugin().getName();
        String pluginPrefix = context.getRootPlugin().getDescription().getPrefix();

        this.prefix = new StringBuilder().append("[")
                .append(
                        pluginPrefix != null ? pluginPrefix : pluginName
                                + " " + context.getName()
                )
                .append("] ").toString();

        this.setParent(context.getRootPlugin().getLogger());
        this.setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(this.prefix + logRecord.getMessage());
        super.log(logRecord);
    }

}
