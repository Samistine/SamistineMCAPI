package com.samistine.mcplugins.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

/**
 *
 * @author Samuel Seidel
 */
public class SamistineAPI extends JavaPlugin {

    List<FeatureHelper> features = new ArrayList<>();

    protected Collection<FeatureHelper> loadFeatures(Plugin plugin, String classPath, Predicate<FeatureHelper> shouldEnable) {
        Reflections reflections = new Reflections(classPath);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(FeatureInfo.class);

        Collection<Class<SFeature>> featureClasses = new ArrayList<>();
        for (Class<?> clazz : annotated) {
            try {
                featureClasses.add(
                        (Class<SFeature>) clazz
                );
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
        }

        List<FeatureHelper> featureHelpers = new ArrayList<>();
        for (Class<SFeature> clazz : featureClasses) {
            FeatureInfo info = clazz.getAnnotation(FeatureInfo.class);
            FeatureHelper featureHelper = new FeatureHelper(info.name(), clazz, plugin);
            featureHelpers.add(featureHelper);
        }

        List<FeatureHelper> list = features;

        list.addAll(featureHelpers);

        featureHelpers.stream()
                .filter(shouldEnable)
                .forEach(FeatureHelper::init);

        featureHelpers.stream()
                .filter(FeatureHelper::stateIsInitialized)
                .forEach(FeatureHelper::enable);

        getServer().getConsoleSender().sendMessage(getModuleStatus());

        return featureHelpers;
    }

    /**
     * Disables the feature and removes it from underlying collections/maps.
     * <p>
     * The feature will be free from any holds and should be free to garbage
     * collect when needed.
     *
     * @param feature
     * @return <tt>true</tt> if this list contained the specified feature
     */
    protected boolean unloadFeature(FeatureHelper feature) {
        if (feature.stateIsEnabled()) {
            feature.disable();
        }
        return features.remove(feature);
    }

    protected String getModuleStatus() {
        return ChatColor.GOLD + "[" + getName() + "]" + ChatColor.GRAY + " Status: " + getModuleStatus_();
    }

    private String getModuleStatus_() {
        StringBuilder sb = new StringBuilder(300);
        for (FeatureHelper feature : features) {
            sb.append(feature.getStatus().getStatusColor()).append(feature.getName()).append(", ");
        }
        String status = sb.toString();
        return status.substring(0, status.lastIndexOf(", "));
    }

    static SamistineAPI getInstance() {
        return getPlugin(SamistineAPI.class);
    }
}
