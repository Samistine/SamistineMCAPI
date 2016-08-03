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

    Map<Plugin, List<FeatureHelper>> features = new HashMap<>();

    public static SamistineAPI getInstance() {
        return getPlugin(SamistineAPI.class);
    }

    public Collection<FeatureHelper> loadFeatures(Plugin plugin, String classPath, Predicate<FeatureHelper> shouldEnable) {
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

        features.putIfAbsent(plugin, new ArrayList<>());

        List<FeatureHelper> list = features.get(plugin);

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
    public boolean unloadFeature(FeatureHelper feature) {
        feature.disable();
        return features.get(feature.getPlugin()).remove(feature);
    }

    public static String[] getModuleStatus() {
        return SamistineAPI.getInstance().features.keySet().stream().map((plugin) -> {
            return ChatColor.GOLD + "[" + plugin.getName() + "]" + ChatColor.GRAY + " Status: " + getModuleStatus(plugin);
        }).toArray(String[]::new);
    }

    public static String getModuleStatus(Plugin plugin) {
        StringBuilder sb = new StringBuilder(300);
        for (FeatureHelper feature : SamistineAPI.getInstance().features.get(plugin)) {
            sb.append(feature.getStatus().getStatusColor()).append(feature.getName()).append(", ");
        }
        String status = sb.toString();
        return status.substring(0, status.lastIndexOf(", "));
    }
}
